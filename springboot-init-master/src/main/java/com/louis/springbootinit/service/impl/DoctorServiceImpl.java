package com.louis.springbootinit.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.louis.springbootinit.common.BaseResponse;
import com.louis.springbootinit.common.ErrorCode;
import com.louis.springbootinit.exception.BusinessException;
import com.louis.springbootinit.mapper.DoctorMapper;
import com.louis.springbootinit.mapper.DrugMapper;
import com.louis.springbootinit.mapper.MedicalRecordMapper;
import com.louis.springbootinit.mapper.PatientMapper;
import com.louis.springbootinit.model.dto.MedicalRecordDto;
import com.louis.springbootinit.model.dto.doctor.DoctorDto;
import com.louis.springbootinit.model.dto.patient.PatientDto;
import com.louis.springbootinit.model.entity.Doctor;
import com.louis.springbootinit.model.entity.Drug;
import com.louis.springbootinit.model.entity.MedicalRecord;
import com.louis.springbootinit.model.entity.Patient;
import com.louis.springbootinit.model.vo.medicalRecord.MedicalRecordForm;
import com.louis.springbootinit.model.vo.user.LoginForm;
import com.louis.springbootinit.model.vo.user.RegisterForm;
import com.louis.springbootinit.service.DoctorService;
import com.louis.springbootinit.service.DrugService;
import com.louis.springbootinit.service.PatientService;
import com.louis.springbootinit.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTimeUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.louis.springbootinit.constant.CommonConstant.USER_LOGIN_KEY;

/**
 * @author louis
 * @version 1.0
 * @date 2023/12/10 10:31
 */
@Service
@Slf4j
public class DoctorServiceImpl extends ServiceImpl<DoctorMapper, Doctor> implements DoctorService {
    @Resource
    private DoctorMapper doctorMapper;

    @Resource
    private MedicalRecordMapper medicalRecordMapper;

    @Resource
    private PatientMapper patientMapper;

    @Resource
    private DrugMapper drugMapper;

    @Resource
    private DrugService drugService;

    /**
     * 查询挂号医生
     * @param department
     * @param subspecialty
     * @return
     */
    @Override
    public BaseResponse<List<DoctorDto>> queryDoctorBySearch(String department, String subspecialty) {
        if(department == null || subspecialty == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数不能为空");
        }
        // 构建选择器
        QueryWrapper<Doctor> doctorQuery = new QueryWrapper<>();
        doctorQuery.eq("Department",department).eq("Subspecialty",subspecialty);
        List<Doctor> doctors = doctorMapper.selectList(doctorQuery);
        // 使用StreamApi将医生转换为医生Dto
        List<DoctorDto> doctorDtos = doctors.stream()
                .map(doctor -> BeanUtil.copyProperties(doctor, DoctorDto.class))
                .collect(Collectors.toList());
        return new BaseResponse<>(200,doctorDtos,"查询成功");
    }

    /**
     * 医生登录
     * @param loginForm
     * @param request
     * @return
     */
    @Override
    public BaseResponse<String> Login(LoginForm loginForm, HttpServletRequest request) {
        // 0. 校验账号密码基本规范
        if(StringUtils.isBlank(loginForm.getAccount()) || StringUtils.isBlank(loginForm.getPassword())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号或者密码不能为空");
        }

        if(StringUtils.length(loginForm.getAccount()) < 6 || StringUtils.length(loginForm.getPassword()) < 6){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号或者密码长度不能小于6");
        }
        String account = loginForm.getAccount();
        String password = loginForm.getPassword();
        // 1. 查数据库 是否存在该用户
        Doctor doctor = query().eq("Account", account).eq("Password", password).one();
        if(doctor == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"该用户未注册");
        }
        // 2. 查看账户状态
        if(doctor.getAccountStatus() == Integer.valueOf(-1)) {
            // 销户
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "该用户已注销");
        }
        // 3. 用户脱敏
        Doctor saftyDoctor = new Doctor();
        saftyDoctor.setId(doctor.getId());
        saftyDoctor.setName(doctor.getName());
        saftyDoctor.setDepartment(doctor.getDepartment());
        saftyDoctor.setSubspecialty(doctor.getSubspecialty());
        saftyDoctor.setExpertise(doctor.getExpertise());
        saftyDoctor.setVacancy(doctor.getVacancy());
        saftyDoctor.setAccountStatus(doctor.getAccountStatus());
        saftyDoctor.setLevel(doctor.getLevel());
        // 4. 服务端（后台）创建session存储（TODO 改为Redis存储）
        // 根据session的生命周期 同一个sessionKey减少管理过多的session
        request.getSession().setAttribute(USER_LOGIN_KEY ,saftyDoctor);
        DoctorDto doctorDto = BeanUtil.copyProperties(saftyDoctor, DoctorDto.class);
        // 5. ThreadLocal存储
        UserHolder.saveUser(doctorDto);
        log.info("登录成功");
        // 6. 返回登录成功
        return new BaseResponse<>(200,"登录成功");
    }

    /**
     * 医生注册
     * @param registerForm
     * @return
     */
    @Override
    @Transactional
    public BaseResponse<String> register(RegisterForm registerForm) {
        // 0. 校验账号密码基本规范
        if(StringUtils.isBlank(registerForm.getAccount()) || StringUtils.isBlank(registerForm.getPassword())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号或者密码不能为空");
        }

        if(StringUtils.length(registerForm.getAccount()) < 6 || StringUtils.length(registerForm.getPassword()) < 6){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号或者密码长度不能小于6");
        }
        if(!registerForm.getAccount().equals(registerForm.getCheckPassword())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"两次密码输入不相同");
        }
        String account = registerForm.getAccount();
        String password = registerForm.getPassword();
        // 1. 查数据库 是否存在该用户
        Doctor doctor = query().eq("Account", account).eq("Password", password).one();
        if(doctor != null && doctor.getAccountStatus() != Integer.valueOf(-1)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"该用户已注册");
        }
        // 注册，插入一条数据
        Doctor addDoctor = new Doctor();
        addDoctor.setCreatedAt(new java.sql.Timestamp(DateTimeUtils.currentTimeMillis()));
        addDoctor.setUpdatedAt(new java.sql.Timestamp(DateTimeUtils.currentTimeMillis()));
        addDoctor.setAccount(account);
        addDoctor.setPassword(password);
        addDoctor.setAccountStatus(0);
        // 返回成功插入的数据
        long res = doctorMapper.insert(addDoctor);

        log.info("注册成功");
        return new BaseResponse<>(200,"注册成功");
    }

    /**
     * 用户信息（医生）
     * @param id 医生Id
     * @return
     */
    @Override
    public BaseResponse<DoctorDto> showDoctorInfo(int id) {
        Doctor doctor = query().eq("Id", id).one();
        if(doctor == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"该医生不存在");
        }
        DoctorDto doctorDto = BeanUtil.copyProperties(doctor, DoctorDto.class);
        return new BaseResponse<>(200,doctorDto,"医生信息");
    }

    /**
     * 默认查询(展示)挂号列表
     * @return
     */
    @Override
    public BaseResponse<List<MedicalRecordDto>> queryMedicalRecordList() {
        // 获取当前登录医生的账号id
        DoctorDto doctor = (DoctorDto) UserHolder.getUser();
        Integer doctorId = doctor.getId();
        QueryWrapper<MedicalRecord> medicalRecordQW = new QueryWrapper<>();
        // 查询该医生当天的就诊列表 todo 默认查询当天就诊列表
        medicalRecordQW.eq("Doctor_Id", doctorId);
        List<MedicalRecord> medicalRecords = medicalRecordMapper.selectList(medicalRecordQW);
        List<MedicalRecordDto> medicalRecordDtos = medicalRecords.stream().
                map(medicalRecord -> BeanUtil.copyProperties(medicalRecord, MedicalRecordDto.class))
                .sorted(Comparator.comparing(MedicalRecordDto::getAppointTime))
                .collect(Collectors.toList());
        if(medicalRecordDtos.size() < 0  || medicalRecordDtos.isEmpty()){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"今日未有挂号");
        }
        // todo 重构通用返回类（将ok 和 error 封装到一个工具类）
        return new BaseResponse<>(200,medicalRecordDtos,"挂号列表");
    }

    /**
     * 收治患者
     * @param id 患者id
     * @return
     */
    @Override
    @Transactional
    public BaseResponse<Boolean> agree(int id) {
        // id 为挂号对象 todo id是会雪花Id（负数、正数都有可能）
        QueryWrapper<Patient> patientQW = new QueryWrapper<>();
        QueryWrapper<Patient> patient = patientQW.eq("Id", id);
        Patient appointPatient = patientMapper.selectOne(patient);
        if(appointPatient == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"该挂号不存在");
        }
        // 进行挂号
        if(appointPatient.getPatientStatus() != 0){
            // 无法救治
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"该患者未处于挂号状态");
        }
        // 修改状态——就诊中
        appointPatient.setPatientStatus(1);
        // 修改数据 (返回修改行数)
        int i = patientMapper.updateById(appointPatient);
        return i >= 1 ? new BaseResponse<>(200,true,"挂号成功")
                      : new BaseResponse<>(ErrorCode.SYSTEM_ERROR);
    }

    /**
     * 创建患者的就诊结果单
     * @return
     */
    @Override
    public BaseResponse<MedicalRecordForm> createJudgeDiagnosis(int id) {
        // 获取当前用户
        DoctorDto doctor = (DoctorDto)UserHolder.getUser();
        QueryWrapper<MedicalRecord> medicalRecordQW = new QueryWrapper<>();
        MedicalRecord medicalRecord = medicalRecordQW.eq("Doctor_Id", doctor.getId())
                .eq("Patient_Id", id)
                .select("Id", "DiagnosisPlan", "Department", "Subspecialty", "Cost", "Prescription", "Patient_Id", "Doctor_Id").getEntity();
        if(medicalRecord == null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"创建失败");
        }
        // 返回Form视图
        Integer doctor_id = medicalRecord.getDoctor_Id();
        Integer patient_id = medicalRecord.getPatient_Id();
        // 查询名字
        String doctorName = query().eq("Id", doctor_id).select("Name").one().getName();
        String patientName = patientMapper.selectById(patient_id).getName();
        MedicalRecordForm medicalRecordForm = new MedicalRecordForm();
        medicalRecordForm.setId(medicalRecord.getId());// 默认填写
        medicalRecordForm.setDoctor_name(doctorName); // 默认填写
        medicalRecordForm.setPatient_name(patientName);// 默认填写
        medicalRecordForm.setDiagnosisPlan(medicalRecord.getDiagnosisPlan());// 待填写
        medicalRecordForm.setDepartment(medicalRecord.getDepartment());// 默认填写
        medicalRecordForm.setSubspecialty(medicalRecord.getSubspecialty());// 默认填写
        medicalRecordForm.setPrescription(medicalRecord.getPrescription());// 待填写
        return new BaseResponse<>(200,medicalRecordForm,"创建成功");
    }

    /**
     * 提交就诊结果单（同时修改患者状态）
     * @param medicalRecordForm
     * @return
     */
    @Override
    @Transactional
    public BaseResponse<MedicalRecordDto> submitMedicalRecord(MedicalRecordForm medicalRecordForm) {
        // 0. 校验俩参数（处方和诊断）
        if(medicalRecordForm.getPrescription() == null || medicalRecordForm.getDiagnosisPlan() == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请填写完整");
        }
        // 1. 查询并修改medicalRecord(这里设计的不好)
        Integer medicalRecordFormId = medicalRecordForm.getId();
        MedicalRecord medicalRecord = medicalRecordMapper.selectById(medicalRecordFormId);
        QueryWrapper<MedicalRecord> medicalRecordQW = new QueryWrapper<>();
        // 1.1 填入处方和就诊方案
        medicalRecord.setPrescription(medicalRecordForm.getPrescription());
        medicalRecord.setDiagnosisPlan(medicalRecordForm.getDiagnosisPlan());
        // 分割处方字符串
        // 2. 进行药房药品的加减和计算总费用
        String prescription = medicalRecordForm.getPrescription();
        // 2.1 使用 split 方法切割字符串
        String[] stringArrays = prescription.split("\\r");
        // 2.2 药品库存减一操作
        // todo 默认医生所选的药品是1个
        Arrays.stream(stringArrays).forEach(this::subtractionDrug);
        // 2.3 计算总价
        int count = stringArrays.length;
        BigDecimal price = BigDecimal.ZERO;
        for (int i = 0; i < count; i++) {
            price = price.add(getCost(stringArrays[i]));
        }
        medicalRecord.setCost(price);
        // 3. 补充数据
        medicalRecord.setDoctorName(medicalRecordForm.getDoctor_name());
        medicalRecord.setPatientName(medicalRecordForm.getPatientName());
        medicalRecord.setUpdatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
        medicalRecord.setSubspecialty(medicalRecordForm.getSubspecialty());
        medicalRecord.setDepartment(medicalRecordForm.getDepartment());
        MedicalRecordDto medicalRecordDto = BeanUtil.copyProperties(medicalRecord, MedicalRecordDto.class);
        // 4. 修改患者状态
        //  4.1 查找对应修改患者
        Integer patient_id = medicalRecord.getPatient_Id();
        QueryWrapper<Patient> patientQueryWrapper = new QueryWrapper<>();
        patientQueryWrapper.eq("Id", patient_id);
        Patient patient = patientMapper.selectOne(patientQueryWrapper);
        //  4.2 对指定患者进行个修改状态（就诊结束）
        UpdateWrapper<Patient> patientUpdateWrapper= new UpdateWrapper<>();
        patientUpdateWrapper.eq("Id",patient_id).setSql("PatientStatus = 2");
        // 4.3 更新数据库
        int update = patientMapper.update(patient, patientUpdateWrapper);
        if(update == 0){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"患者状态更新失败");
        }
        // 5. 更新诊断单表数据
        int i = medicalRecordMapper.updateById(medicalRecord);
        if(i == 0){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"提交失败");
        }
        // 6. 成功返回
        return new BaseResponse<>(200,medicalRecordDto,"提交成功");
    }

    /**
     * 找到对应的药品进行数量减一
     * @param str
     */
    public void subtractionDrug(String str){
        // 找到指定药品
        UpdateWrapper<Drug> drugUpdateWrapper = new UpdateWrapper<>();
        QueryWrapper<Drug> drugQueryWrapper = new QueryWrapper<>();
        drugQueryWrapper.eq("DrugName",str);
        Drug drug = drugMapper.selectOne(drugQueryWrapper);
        // 查询该药品是否有库存
        if(drug == null){
            // 未查找到
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"未查找到药品");
        }
        // 减一操作
        drugUpdateWrapper.eq("DrugName",str).setSql("Count = Count - 1");
        // 更新数据库
        drugMapper.update(drug,drugUpdateWrapper);
    }
    public BigDecimal getCost(String str){
        // 找到指定药品
        QueryWrapper<Drug> drugQueryWrapper = new QueryWrapper<>();
        drugQueryWrapper.eq("DrugName",str);
        Drug drug = drugMapper.selectOne(drugQueryWrapper);
        // 查询该药品是否有库存
        if(drug == null){
            // 未查找到
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"未查找到药品");
        }
        return drug.getPrice();
    }



    @Override
    public BaseResponse<List<Drug>> queryDrugList(String drugName) {
        if(drugName == null){
            // 默认查询所有药品
            return new BaseResponse<>(200,drugService.list(),"查询成功");
        }
        QueryWrapper<Drug> drugQW = new QueryWrapper<>();
        // 模糊查询
        drugQW.like("DrugName",drugName);
        List<Drug> drugs = drugMapper.selectList(drugQW);
        List<Drug> sortDrugs = drugs.stream()
                .sorted(Comparator.comparingInt(Drug::getCount).reversed()) // 从小到大
                .collect(Collectors.toList());
        if(sortDrugs.size() <= 0 || sortDrugs.isEmpty()){
            // 未查找到
            return new BaseResponse<>(ErrorCode.NOT_FOUND_ERROR);
        }
        return new BaseResponse<>(200,sortDrugs,"查询成功");
    }
}

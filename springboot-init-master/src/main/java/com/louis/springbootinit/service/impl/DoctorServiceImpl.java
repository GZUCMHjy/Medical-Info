package com.louis.springbootinit.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.louis.springbootinit.common.BaseResponse;
import com.louis.springbootinit.common.ErrorCode;
import com.louis.springbootinit.exception.BusinessException;
import com.louis.springbootinit.mapper.DoctorMapper;
import com.louis.springbootinit.mapper.MedicalRecordMapper;
import com.louis.springbootinit.mapper.PatientMapper;
import com.louis.springbootinit.model.dto.MedicalRecordDto;
import com.louis.springbootinit.model.dto.doctor.DoctorDto;
import com.louis.springbootinit.model.dto.patient.PatientDto;
import com.louis.springbootinit.model.entity.Doctor;
import com.louis.springbootinit.model.entity.MedicalRecord;
import com.louis.springbootinit.model.entity.Patient;
import com.louis.springbootinit.model.vo.user.LoginForm;
import com.louis.springbootinit.model.vo.user.RegisterForm;
import com.louis.springbootinit.service.DoctorService;
import com.louis.springbootinit.service.PatientService;
import com.louis.springbootinit.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTimeUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    public BaseResponse<DoctorDto> showDoctorInfoBy(int id) {
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
        Doctor doctor = (Doctor) UserHolder.getUser();
        Integer doctorId = doctor.getId();
        QueryWrapper<MedicalRecord> medicalRecordQW = new QueryWrapper<>();
        // 查询该医生当天的就诊列表 todo 默认查询当天就诊列表
        medicalRecordQW.eq("Doctor_Id", doctorId)
                       .select("Patient_Id", "AppointTime", "Sign");
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
}

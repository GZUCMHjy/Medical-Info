package com.louis.springbootinit.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.louis.springbootinit.common.BaseResponse;
import com.louis.springbootinit.common.ErrorCode;
import com.louis.springbootinit.common.ResultUtils;
import com.louis.springbootinit.exception.BusinessException;
import com.louis.springbootinit.mapper.DoctorMapper;
import com.louis.springbootinit.mapper.MedicalRecordMapper;
import com.louis.springbootinit.mapper.PatientMapper;
import com.louis.springbootinit.model.Registered;
import com.louis.springbootinit.model.dto.MedicalRecordDto;
import com.louis.springbootinit.model.dto.patient.PatientDto;
import com.louis.springbootinit.model.entity.Doctor;
import com.louis.springbootinit.model.entity.MedicalRecord;
import com.louis.springbootinit.model.entity.Patient;
import com.louis.springbootinit.model.vo.medicalRecord.MedicalRecordVo;
import com.louis.springbootinit.model.vo.patient.PatientEditProfileVo;
import com.louis.springbootinit.model.vo.user.LoginForm;
import com.louis.springbootinit.model.vo.user.RegisterForm;
import com.louis.springbootinit.service.PatientService;
import com.louis.springbootinit.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTimeUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static com.louis.springbootinit.constant.CommonConstant.USER_LOGIN_KEY;

/**
 * @author louis
 * @version 1.0
 * @date 2023/12/10 10:31
 */
@Service
@Slf4j
public class PatientServiceImpl extends ServiceImpl<PatientMapper, Patient> implements PatientService {
    @Resource
    private PatientMapper patientMapper;
    @Resource
    private MedicalRecordMapper medicalRecordMapper;
    @Resource
    private DoctorMapper doctorMapper;


    /**
     * 患者登录
     * @param loginForm 登录表单
     * @param request Servlet请求
     * @return
     */
    @Override
    public BaseResponse<PatientDto> Login(LoginForm loginForm, HttpServletRequest request) {
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
        Patient loginPatient = query().eq("Account", account).one();
        if(loginPatient == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"该用户未注册");
        }
        if(!loginPatient.getPassword().equals(password)){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"账号或者密码错误");
        }
        // 2. 查看账户状态
        if(loginPatient.getAccountStatus() == Integer.valueOf(-1)) {
            // 销户
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "该用户已注销");
        }
        // 3. 用户脱敏
        Patient saftyPatient = new Patient();
        saftyPatient.setId(loginPatient.getId());
        saftyPatient.setName(loginPatient.getName());
        saftyPatient.setAge(loginPatient.getAge());
        saftyPatient.setAvatarUrl(loginPatient.getAvatarUrl());
        saftyPatient.setGender(loginPatient.getGender());

        // 4. 服务端（后台）创建session存储（TODO 改为Redis存储）
        // 根据session的生命周期 同一个sessionKey减少管理过多的session
        request.getSession().setAttribute(USER_LOGIN_KEY ,saftyPatient);

        PatientDto patientDto = BeanUtil.copyProperties(saftyPatient, PatientDto.class);
        // 5. ThreadLocal存储
        UserHolder.saveUser(patientDto);
        if(loginPatient.getName() == null || loginPatient.getAge() == null || loginPatient.getGender() == ""){
            return ResultUtils.success(patientDto,"注册成功，请完善个人信息");
        }
        // 6. 返回登陆成功
        return ResultUtils.success(patientDto);
    }

    /**
     * 患者注册（创建用户）
     * @param  registerForm 注册表
     */
    @Override
    @Transactional
    public BaseResponse<String> Register(RegisterForm registerForm) {
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
        Patient registerPatient = query().eq("Account", account).eq("Password", password).one();
        if(registerPatient != null && registerPatient.getAccountStatus() != Integer.valueOf(-1)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"该用户已注册");
        }
        // 注册，插入一条数据
        Patient addPatient = new Patient();
        addPatient.setAccount(account);
        // 不设置加密好了（TODO 加盐加密）
        addPatient.setPassword(password);
        addPatient.setCreatedAt(new java.sql.Timestamp(DateTimeUtils.currentTimeMillis()));
        addPatient.setUpdatedAt(new java.sql.Timestamp(DateTimeUtils.currentTimeMillis()));
        // 注册成功
        addPatient.setAccountStatus(0);
        // 未挂号状态
        addPatient.setPatientStatus(4);
        // 默认头像
        addPatient.setAvatarUrl("https://get.wallhere.com/photo/YJM-CGI-women-pink-hair-blushing-portrait-looking-at-viewer-2288644.jpg");
        // 返回成功插入的数据
        long res = patientMapper.insert(addPatient);

        log.info("注册成功");
        return ResultUtils.success("注册成功");
    }

    /**
     * 修改患者信息
     * @param patient
     * @return
     */
    @Override
    @Transactional
    public PatientDto editProfile(PatientEditProfileVo patient) {
        // 0. 获取基本信息
        Integer age = patient.getAge();
        String name = patient.getName();
        String avatarUrl = patient.getAvatarUrl();
        String gender = patient.getGender();
        // 1. 获取当前用户信息
        PatientDto patientDto = (PatientDto) UserHolder.getUser();
        Integer patientId = patientDto.getId();
        // 2. 修改目标对象
        Patient targetPatient = query().eq("Id", patientId).one();
        targetPatient.setAge(age);
        targetPatient.setName(name);
        targetPatient.setGender(gender);
        int i = patientMapper.updateById(targetPatient);
        if(i == 0){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"修改失败");
        }
        // 3. 复制传递
        PatientDto res = BeanUtil.copyProperties(targetPatient, PatientDto.class);
        // 4. 更新Session值
        UserHolder.saveUser(res);
        // 5. 返回修改结果
        return res;
    }

    /**
     * 查看个人信息
     * @param
     * @return
     */
    @Override
    public PatientDto showPatientInfo() {
        // 获取当前用户信息
        PatientDto patient = (PatientDto) UserHolder.getUser();
        // 当前用户是否填写基本信息
        if(patient.getName() == null && patient.getAge() == null && patient.getAge() == null){
            return null;
        }
        return patient;
    }

    /**
     * 患者挂号
     * @param medicalRecordVo
     * @return
     */
    @Override
    @Transactional
    public BaseResponse<MedicalRecordDto> submitAppointment(MedicalRecordVo medicalRecordVo) {
        // 0. 校验患者是否重复挂号
        PatientDto patient = (PatientDto)UserHolder.getUser();
        Integer patientId = patient.getId();
        if (query().eq("Id",patientId).one().getPatientStatus() == 0) {
            // 已挂号
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"您已挂号，请勿重复挂号");
        }
        // 1. 校验表单参数
        if(StringUtils.isBlank(medicalRecordVo.getDepartment())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请选择科室");
        }
        if(StringUtils.isBlank(medicalRecordVo.getSubspecialty())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请选择亚分科");
        }
        if(medicalRecordVo.getDoctor_Id().toString() == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请选择医生");
        }
        // 2. 查看该医生是否有空闲的号
        QueryWrapper<Doctor> doctorQuery = new QueryWrapper<Doctor>();
        Integer doctorId = medicalRecordVo.getDoctor_Id();
        doctorQuery.eq("Id",doctorId);
        Doctor doctor = doctorMapper.selectOne(doctorQuery);
        if(doctor == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"查无此医生");
        }
        if(doctor.getVacancy() <= 0){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"此医生挂号已满");
        }
        UpdateWrapper<Doctor> doctorUpdateWrapper = new UpdateWrapper<>();
        // 3. 医生挂号数量一
        doctorUpdateWrapper.eq("Id",doctorId).setSql("Vacancy = Vacancy - 1");
        // 3.1 更新数据
        doctorMapper.update(doctor,doctorUpdateWrapper);

        MedicalRecord medicalRecord = new MedicalRecord();
        // 医生挂号数据
        medicalRecord.setSign("等待中");
        medicalRecord.setDepartment(medicalRecordVo.getDepartment());
        medicalRecord.setSubspecialty(medicalRecordVo.getSubspecialty());
        medicalRecord.setPatient_Id(medicalRecordVo.getPatient_Id());
        medicalRecord.setDoctor_Id(medicalRecordVo.getDoctor_Id());
        medicalRecord.setCreatedAt(new java.sql.Timestamp(DateTimeUtils.currentTimeMillis()));
        medicalRecord.setUpdatedAt(new java.sql.Timestamp(DateTimeUtils.currentTimeMillis()));
        // 格式化预约时间
        String appointTime = medicalRecordVo.getAppointTime();
        // SimpleDateFormat sdf = new SimpleDateFormat("yy:MM:dd HH:mm");
        medicalRecord.setAppointTime(appointTime);
        // 4. 插入挂号记录
        medicalRecordMapper.insert(medicalRecord);
        MedicalRecordDto medicalRecordDto = BeanUtil.copyProperties(medicalRecord, MedicalRecordDto.class);
        // 5. 改变患者状态——挂号状态（候诊）

        boolean update = update().eq("Id", patient.getId()).set("PatientStatus", 0).update();
        if(!update){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"患者状态更新失败");
        }
        return ResultUtils.success(medicalRecordDto,"挂号成功，等待叫号");
    }

    @Override
    public BaseResponse<MedicalRecordDto> appintmentByPatient(int id) {
        PatientDto patient = (PatientDto)UserHolder.getUser();
        Integer patientId = patient.getId();
        QueryWrapper<Doctor> doctorQueryWrapper = new QueryWrapper<>();
        doctorQueryWrapper.eq("Id",id);
        Doctor doctor = doctorMapper.selectOne(doctorQueryWrapper);
        if(doctor == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"查无医生");
        }
        MedicalRecordDto medicalRecordDto = new MedicalRecordDto();
        // 赋值基本信息
        medicalRecordDto.setPatientName(patient.getName());
        medicalRecordDto.setPatient_Id(patientId);
        medicalRecordDto.setDoctor_Id(doctor.getId());
        medicalRecordDto.setDoctorName(doctor.getName());
        medicalRecordDto.setDepartment(doctor.getDepartment());
        medicalRecordDto.setSubspecialty(doctor.getSubspecialty());
        return ResultUtils.success(medicalRecordDto);
    }

    @Override
    public BaseResponse<List<Registered>> showRegisteredList() {
        PatientDto patient = (PatientDto)UserHolder.getUser();
        Integer patientId = patient.getId();
        List<MedicalRecord> medicalRecords = medicalRecordMapper.selectList(new QueryWrapper<MedicalRecord>().eq("Patient_Id", patientId));
        if(medicalRecords == null || medicalRecords.isEmpty()){
            return new BaseResponse<>(ErrorCode.SYSTEM_ERROR);
        }
        int count = medicalRecords.size();
        // 定好长度 避免扩容影响性能
        List<Registered> registereds = new ArrayList<>(count);
        for(int i = 0; i < count; i++){
            MedicalRecord medicalRecord = medicalRecords.get(i);
            Registered registered = new Registered();
            registered.setMedicalRecordId(medicalRecord.getId());
            registered.setDoctorName(medicalRecord.getDoctorName());
            registered.setDepartment(medicalRecord.getDepartment());
            registered.setSubspecialty(medicalRecord.getSubspecialty());
            registered.setPatientName(medicalRecord.getPatientName());
            registered.setCost(medicalRecord.getCost());
            registered.setAppointTime(medicalRecord.getAppointTime());
            registereds.add(registered);
        }
        return ResultUtils.success(registereds);
    }

    @Override
    public BaseResponse<String> LoginTest(String account, String password, HttpServletRequest request) {
        if(account == null || password == null){
            return new BaseResponse<>(200,"账号或者密码为空");
        }
        return new BaseResponse<>(200,"登录成功");
    }
}

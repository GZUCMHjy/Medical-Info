package com.louis.springbootinit.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.louis.springbootinit.common.BaseResponse;
import com.louis.springbootinit.common.ErrorCode;
import com.louis.springbootinit.exception.BusinessException;
import com.louis.springbootinit.mapper.DoctorMapper;
import com.louis.springbootinit.mapper.MedicalRecordMapper;
import com.louis.springbootinit.mapper.PatientMapper;
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

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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
        Patient loginPatient = query().eq("Account", account).eq("Password", password).one();
        if(loginPatient == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"该用户未注册");
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
        log.info("登录成功");
        // 6. 返回登陆成功
        return new BaseResponse<>(200,"登录成功");
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
        return new BaseResponse<>(200,"注册成功");
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
        targetPatient.setAvatarUrl(avatarUrl);
        targetPatient.setGender(gender);
        int i = patientMapper.updateById(targetPatient);
        if(i == 0){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"修改失败");
        }
        // 3. 复制传递
        PatientDto res = BeanUtil.copyProperties(targetPatient, PatientDto.class);
        // 4. 返回修改结果
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
    public BaseResponse<MedicalRecordDto> appointmentByPatient(MedicalRecordVo medicalRecordVo) {
        // 1. 校验表单参数
        if(StringUtils.isBlank(medicalRecordVo.getSubspecialty())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请选择亚分科");
        }
        if(StringUtils.isBlank(medicalRecordVo.getDepartment())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请选择科室");
        }
        if(medicalRecordVo.getDoctorId() == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请选择医生");
        }
        // 2. 查看该医生是否有空闲的号
        QueryWrapper<Doctor> doctorQuery = new QueryWrapper<Doctor>();
        Integer doctorId = medicalRecordVo.getDoctorId();
        doctorQuery.eq("Id",doctorId);
        Doctor doctor = doctorMapper.selectOne(doctorQuery);
        if(doctor == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"查无此医生");
        }
        if(doctor.getVacancy() <= 0){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"此医生挂号已满");
        }
        UpdateWrapper<Doctor> doctorUpdateWrapper = new UpdateWrapper<>();
        // 医生挂号数量一
        doctorUpdateWrapper.eq("Id",doctorId).setSql("Vacancy = Vacancy - 1");
        MedicalRecord medicalRecord = new MedicalRecord();
        // 医生挂号数据
        medicalRecord.setSign("等待中");
        medicalRecord.setDepartment(medicalRecordVo.getDepartment());
        medicalRecord.setSubspecialty(medicalRecordVo.getSubspecialty());
        medicalRecord.setPatientId(medicalRecordVo.getPatientId());
        medicalRecord.setDoctorId(medicalRecordVo.getDoctorId());
        medicalRecord.setCreatedAt(new java.sql.Timestamp(DateTimeUtils.currentTimeMillis()));
        medicalRecord.setUpdatedAt(new java.sql.Timestamp(DateTimeUtils.currentTimeMillis()));
        medicalRecordMapper.insert(medicalRecord);
        MedicalRecordDto medicalRecordDto = BeanUtil.copyProperties(medicalRecord, MedicalRecordDto.class);
        return new BaseResponse<>(200,medicalRecordDto,"挂号成功！等待叫号");
    }

}

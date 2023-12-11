package com.louis.springbootinit.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.louis.springbootinit.common.ErrorCode;
import com.louis.springbootinit.exception.BusinessException;
import com.louis.springbootinit.mapper.PatientMapper;
import com.louis.springbootinit.model.dto.patient.PatientDto;
import com.louis.springbootinit.model.entity.Patient;
import com.louis.springbootinit.model.vo.patient.PatientEditProfileVo;
import com.louis.springbootinit.model.vo.patient.PatientLoginVo;
import com.louis.springbootinit.model.vo.patient.PatientRegisterVo;
import com.louis.springbootinit.service.PatientService;
import com.louis.springbootinit.utils.PatientHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTimeUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * @author louis
 * @version 1.0
 * @date 2023/12/10 10:31
 */
@Service
@Slf4j
public class PatientServiceImpl extends ServiceImpl<PatientMapper, Patient> implements PatientService {
    public String sessionPrefix = "user_login_";
    @Resource
    private PatientMapper patientMapper;
    /**
     * 患者登录
     *
     * @param patient 患者登录信息
     * @return
     */
    @Override
    public String  Login(PatientLoginVo patient, HttpServletRequest request) {
        // 0. 校验账号密码基本规范
        if(StringUtils.isBlank(patient.getAccount()) || StringUtils.isBlank(patient.getPassword())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号或者密码不能为空");
        }

        if(StringUtils.length(patient.getAccount()) < 6 || StringUtils.length(patient.getPassword()) < 6){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号或者密码长度不能小于6");
        }
        String account = patient.getAccount();
        String password = patient.getPassword();
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
        // 4. 生成token
        String token = UUID.randomUUID().toString();
        String tokenKey = sessionPrefix + token;
        // 5. session存储（TODO 改为Redis存储）
        request.getSession().setAttribute(tokenKey ,saftyPatient);

        PatientDto patientDto = BeanUtil.copyProperties(saftyPatient, PatientDto.class);
        // 6. ThreadLocal存储
        PatientHolder.savePatient(patientDto);
        log.info("登录成功");
        // 7. 返回token
        return token;
    }

    /**
     * 患者注册（创建用户）
     * @param patient 患者注册信息
     */
    @Override
    @Transactional
    public long Register(PatientRegisterVo patient) {
        // 0. 校验账号密码基本规范
        if(StringUtils.isBlank(patient.getAccount()) || StringUtils.isBlank(patient.getPassword())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号或者密码不能为空");
        }

        if(StringUtils.length(patient.getAccount()) < 6 || StringUtils.length(patient.getPassword()) < 6){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号或者密码长度不能小于6");
        }
        if(!patient.getAccount().equals(patient.getCheckPassword())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"两次密码输入不相同");
        }
        String account = patient.getAccount();
        String password = patient.getPassword();
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
        return res;
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
        PatientDto patientDto = PatientHolder.getPatient();
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
        PatientDto patient = PatientHolder.getPatient();
        // 当前用户是否填写基本信息
        if(patient.getName() == null && patient.getAge() == null && patient.getAge() == null){
            return null;
        }
        return patient;
    }
}

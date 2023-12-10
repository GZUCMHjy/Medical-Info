package com.louis.springbootinit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.louis.springbootinit.common.ErrorCode;
import com.louis.springbootinit.exception.BusinessException;
import com.louis.springbootinit.mapper.PatientMapper;
import com.louis.springbootinit.model.entity.Patient;
import com.louis.springbootinit.model.vo.pation.PatientLoginVo;
import com.louis.springbootinit.model.vo.pation.PatientRegisterVo;
import com.louis.springbootinit.service.PatientService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTimeUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

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
    /**
     * 患者登录
     * @param patient 患者登录信息
     * @return
     */
    @Override
    public long Login(PatientLoginVo patient) {
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
        log.info("登录成功");
        return 1;
    }

    /**
     * 患者注册
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
        // 返回成功插入的数据
        long res = patientMapper.insert(addPatient);
        log.info("注册成功");
        return res;
    }


}

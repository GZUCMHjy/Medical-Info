package com.louis.springbootinit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.louis.springbootinit.model.entity.Patient;
import com.louis.springbootinit.model.vo.pation.PatientLoginVo;
import com.louis.springbootinit.model.vo.pation.PatientRegisterVo;

/**
 * @author louis
 * @version 1.0
 * @date 2023/12/10 10:22
 */
public interface PatientService extends IService<Patient> {

    long Login(PatientLoginVo patient);
    long Register(PatientRegisterVo patient);
}

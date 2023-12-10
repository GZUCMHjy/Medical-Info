package com.louis.springbootinit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.louis.springbootinit.model.dto.patient.PatientDto;
import com.louis.springbootinit.model.entity.Patient;
import com.louis.springbootinit.model.vo.patient.PatientEditProfileVo;
import com.louis.springbootinit.model.vo.patient.PatientLoginVo;
import com.louis.springbootinit.model.vo.patient.PatientRegisterVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author louis
 * @version 1.0
 * @date 2023/12/10 10:22
 */
public interface PatientService extends IService<Patient> {

    String Login(PatientLoginVo patient, HttpServletRequest request);
    long Register(PatientRegisterVo patient);

    PatientDto editProfile(PatientEditProfileVo patient,HttpServletRequest request);
}

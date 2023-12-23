package com.louis.springbootinit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.louis.springbootinit.common.BaseResponse;
import com.louis.springbootinit.model.dto.MedicalRecordDto;
import com.louis.springbootinit.model.dto.patient.PatientDto;
import com.louis.springbootinit.model.entity.Patient;
import com.louis.springbootinit.model.vo.medicalRecord.MedicalRecordVo;
import com.louis.springbootinit.model.vo.patient.PatientEditProfileVo;
import com.louis.springbootinit.model.vo.user.LoginForm;
import com.louis.springbootinit.model.vo.user.RegisterForm;

import javax.servlet.http.HttpServletRequest;

/**
 * @author louis
 * @version 1.0
 * @date 2023/12/10 10:22
 */
public interface PatientService extends IService<Patient> {

    BaseResponse<String> Login(LoginForm loginForm, HttpServletRequest request);

    BaseResponse<String> Register(RegisterForm registerForm);


    PatientDto editProfile(PatientEditProfileVo patient);

    PatientDto showPatientInfo();

    BaseResponse<MedicalRecordDto> submitAppointment(MedicalRecordVo medicalrecordVo);

    BaseResponse<MedicalRecordDto> appintmentByPatient(int parseInt);
}

package com.louis.springbootinit.controller;

import com.louis.springbootinit.common.BaseResponse;
import com.louis.springbootinit.common.ErrorCode;
import com.louis.springbootinit.exception.BusinessException;
import com.louis.springbootinit.model.dto.patient.PatientDto;
import com.louis.springbootinit.model.vo.patient.PatientEditProfileVo;
import com.louis.springbootinit.model.vo.patient.PatientLoginVo;
import com.louis.springbootinit.model.vo.patient.PatientRegisterVo;
import com.louis.springbootinit.service.PatientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author louis
 * @version 1.0
 * @date 2023/12/10 9:33
 */
@RestController
@RequestMapping("/patient")
@Slf4j
public class PatientController {
    @Resource
    private PatientService patientService;

    /**
     * 患者登录
     * @param patient
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<String> login(@RequestBody PatientLoginVo patient, HttpServletRequest request) {
        String loginStatus = patientService.Login(patient,request);
//        if(loginStatus != 1){
//            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"登录失败");
//        }
        return new BaseResponse<>(200,loginStatus,"token值");
    }

    @PostMapping("/register")
    public BaseResponse<Integer> register(@RequestBody PatientRegisterVo patient) {
        long registerStatus = patientService.Register(patient);
        if(registerStatus == 0){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"注册失败");
        }
        return new BaseResponse<>(ErrorCode.SUCCESS);
    }
    @PostMapping("/editProfile")
    public BaseResponse<Integer> editProfile(@RequestBody PatientEditProfileVo patient,HttpServletRequest request){
            patientService.editProfile(patient,request);
            return null;
    }

    @GetMapping("show")
    public BaseResponse<PatientDto> showPatientInfo(){
        PatientDto patientDto = patientService.showPatientInfo();
        if(patientDto == null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"获取患者信息失败");
        }
        return new BaseResponse<>(200,patientDto,"患者信息");
    }

}

package com.louis.springbootinit.controller;

import com.louis.springbootinit.common.BaseResponse;
import com.louis.springbootinit.common.ErrorCode;
import com.louis.springbootinit.exception.BusinessException;
import com.louis.springbootinit.model.entity.Patient;
import com.louis.springbootinit.model.vo.pation.PatientLoginVo;
import com.louis.springbootinit.model.vo.pation.PatientRegisterVo;
import com.louis.springbootinit.service.PatientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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
    public BaseResponse<Integer> login(@RequestBody PatientLoginVo patient) {
        long loginStatus = patientService.Login(patient);
        if(loginStatus != 1){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"登录失败");
        }
        return new BaseResponse<>(ErrorCode.SUCCESS);
    }

    @PostMapping("/register")
    public BaseResponse<Integer> register(@RequestBody PatientRegisterVo patient) {
        long registerStatus = patientService.Register(patient);
        if(registerStatus == 0){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"注册失败");
        }
        return new BaseResponse<>(ErrorCode.SUCCESS);
    }

}

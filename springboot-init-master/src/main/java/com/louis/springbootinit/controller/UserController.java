package com.louis.springbootinit.controller;

import com.louis.springbootinit.common.BaseResponse;
import com.louis.springbootinit.model.vo.user.LoginForm;
import com.louis.springbootinit.model.vo.user.RegisterForm;
import com.louis.springbootinit.service.DoctorService;
import com.louis.springbootinit.service.PatientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author louis
 * @version 1.0
 * @date 2023/12/12 19:48
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private PatientService patientService;

    @Resource
    private DoctorService doctorService;

    /**
     * 患者登录
     * @param loginForm 登录表单
     * @return
     */
    @RequestMapping("/patient/login")
    public BaseResponse<String> patientLogin(@RequestBody LoginForm loginForm, HttpServletRequest request) {
        return patientService.Login(loginForm,request);
    }

    /**
     * 医生登录
     * @param loginForm 登录表单
     * @return
     */
    @RequestMapping("/doctor/login")
    public BaseResponse<String> doctorLogin(@RequestBody LoginForm loginForm,HttpServletRequest request) {
        return doctorService.Login(loginForm,request);
    }

    /**
     * 公共的注册接口
     * @param registerForm 注册表单
     * @return
     */
    @RequestMapping("/register")
    public BaseResponse<String> Register(@RequestBody RegisterForm registerForm) {
        String account = registerForm.getAccount();
        if(account.contains("D")){
            return doctorService.register(registerForm);
        }
        return  patientService.Register(registerForm);
    }
}

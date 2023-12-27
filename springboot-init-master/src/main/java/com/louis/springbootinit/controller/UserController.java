package com.louis.springbootinit.controller;



import cn.hutool.json.JSONConfig;
import com.louis.springbootinit.common.BaseResponse;
import com.louis.springbootinit.common.ErrorCode;
import com.louis.springbootinit.model.dto.doctor.DoctorDto;
import com.louis.springbootinit.model.dto.patient.PatientDto;
import com.louis.springbootinit.model.vo.user.LoginForm;
import com.louis.springbootinit.model.vo.user.RegisterForm;
import com.louis.springbootinit.service.DoctorService;
import com.louis.springbootinit.service.PatientService;
import com.louis.springbootinit.utils.UserHolder;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import static com.louis.springbootinit.constant.CommonConstant.USER_LOGIN_KEY;

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

//    /**
//     * 患者登录
//     * @param loginForm 登录表单
//     * @return
//     */
//    @ApiOperation("患者登录")
//    @PostMapping("/patient/login")
//    public BaseResponse<PatientDto> patientLogin(LoginForm loginForm, HttpServletRequest request) {
//        String password = loginForm.getPassword();
//        String account = loginForm.getAccount();
//        return patientService.Login(loginForm,request);
//    }
    @ApiOperation("患者登录")
    @PostMapping("/patient/login")
    public BaseResponse<PatientDto> patientLoginTest(@RequestParam String account, @RequestParam String password, HttpServletRequest request) {
        return patientService.LoginTest1(account,password,request);
    }

    /**
     * 医生登录
     * @param loginForm 登录表单
     * @return
     */
//    @ApiOperation("医生登录")
//    @PostMapping("/doctor/login")
//    public BaseResponse<DoctorDto> doctorLogin(LoginForm loginForm, HttpServletRequest request) {
//        return doctorService.Login(loginForm,request);
//    }
    @ApiOperation("医生登录")
    @PostMapping("/doctor/login")
    public BaseResponse<DoctorDto> doctorLogin(@RequestParam String account, @RequestParam String password, HttpServletRequest request) {
        return doctorService.LoginTest(account,password,request);
    }

    /**
     * 公共的注册接口
     * @param registerForm 注册表单
     * @return
     */
    @ApiOperation("公共注册")
    @PostMapping("/register")
    public BaseResponse<String> Register(RegisterForm registerForm) {
        String account = registerForm.getAccount();
        if(account.contains("D")){
            return doctorService.register(registerForm);
        }
        return  patientService.Register(registerForm);
    }

    /**
     * 退出登录
     * @param request
     * @return
     */
    @ApiOperation("退出登录")
    @PostMapping("/logout")
    public BaseResponse<String> logout(HttpServletRequest request){
        Object attribute = request.getSession().getAttribute(USER_LOGIN_KEY);
        if(attribute != null){
            request.getSession().invalidate();
            UserHolder.removeUser();
        }else{
            return new BaseResponse<>(ErrorCode.NOT_LOGIN_ERROR);
        }
        return new BaseResponse<>(ErrorCode.SUCCESS);
    }
}

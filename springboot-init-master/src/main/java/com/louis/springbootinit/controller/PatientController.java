package com.louis.springbootinit.controller;

import com.louis.springbootinit.common.BaseResponse;
import com.louis.springbootinit.common.ErrorCode;
import com.louis.springbootinit.exception.BusinessException;
import com.louis.springbootinit.model.dto.patient.PatientDto;
import com.louis.springbootinit.model.entity.Doctor;
import com.louis.springbootinit.model.entity.Patient;
import com.louis.springbootinit.model.vo.patient.PatientEditProfileVo;
import com.louis.springbootinit.model.vo.patient.PatientLoginVo;
import com.louis.springbootinit.model.vo.patient.PatientRegisterVo;
import com.louis.springbootinit.service.DoctorService;
import com.louis.springbootinit.service.PatientService;
import com.louis.springbootinit.utils.PatientHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

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

    @Resource
    private DoctorService doctorService;

    /**
     * 患者登录
     * @param patient
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<String> login(@RequestBody PatientLoginVo patient, HttpServletRequest request) {
        String loginStatus = patientService.Login(patient,request);
        return new BaseResponse<>(200,loginStatus,"token值");
    }

    /**
     * 患者注册
     * @param patient
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Integer> register(@RequestBody PatientRegisterVo patient) {
        long registerStatus = patientService.Register(patient);
        if(registerStatus == 0){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"注册失败");
        }
        return new BaseResponse<>(ErrorCode.SUCCESS);
    }

    /**
     * 编辑患者信息
     * @param patient
     * @return
     */
    @PostMapping("/editProfile")
    public BaseResponse<PatientDto> editProfile(@RequestBody PatientEditProfileVo patient){
        PatientDto patientDto = patientService.editProfile(patient);
        return new BaseResponse<>(200,patientDto,"修改成功");
    }

    /**
     * 显示患者基本信息
     * @return
     */
    @GetMapping("show")
    public BaseResponse<PatientDto> showPatientInfo(){
        PatientDto patientDto = patientService.showPatientInfo();
        if(patientDto == null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"获取患者信息失败");
        }
        return new BaseResponse<>(200,patientDto,"患者信息");
    }

    /**
     * 患者退出登录
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<String> logout(HttpServletRequest request){
        String token = request.getHeader("token");
        String key = "user_login_" + token;
        Patient patient = (Patient)request.getSession().getAttribute(key);
        if(patient != null){
            // 删除session
            request.getSession().invalidate();
            // 删除ThreadLocal
            PatientHolder.removePatient();
            return new BaseResponse<>(200,"退出成功");
        }
        return new BaseResponse<>(ErrorCode.SYSTEM_ERROR);
    }

    /**
     * 查询医生
     * @param department 科室（内科）
     * @param subspecialty 亚专业（心脏科）
     * @return
     */
    @GetMapping("/queryDockerBySearch")
    public BaseResponse<List<Doctor>> queryDockerBySearch(@RequestParam String department, @RequestParam String subspecialty){
        return doctorService.queryDockerBySearch(department,subspecialty);
    }

}

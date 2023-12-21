package com.louis.springbootinit.controller;

import com.louis.springbootinit.common.BaseResponse;
import com.louis.springbootinit.common.ErrorCode;
import com.louis.springbootinit.exception.BusinessException;
import com.louis.springbootinit.model.dto.MedicalRecordDto;
import com.louis.springbootinit.model.dto.doctor.DoctorDto;
import com.louis.springbootinit.model.dto.patient.PatientDto;
import com.louis.springbootinit.model.entity.Patient;
import com.louis.springbootinit.model.vo.medicalRecord.MedicalRecordVo;
import com.louis.springbootinit.model.vo.patient.PatientEditProfileVo;
import com.louis.springbootinit.service.DoctorService;
import com.louis.springbootinit.service.PatientService;
import com.louis.springbootinit.utils.UserHolder;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.louis.springbootinit.constant.CommonConstant.USER_LOGIN_KEY;

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
     * 编辑患者信息
     * @param patient
     * @return
     */
    @ApiOperation("编辑患者信息")
    @PostMapping("/editProfile")
    public BaseResponse<PatientDto> editProfile(@RequestBody PatientEditProfileVo patient){
        PatientDto patientDto = patientService.editProfile(patient);
        return new BaseResponse<>(200,patientDto,"修改成功");
    }

    /**
     * 显示患者基本信息
     * @return
     */
    @ApiOperation("显示患者基本信息")
    @GetMapping("show")
    public BaseResponse<PatientDto> showPatientInfo(){
        PatientDto patientDto = patientService.showPatientInfo();
        if(patientDto == null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"获取患者信息失败");
        }
        return new BaseResponse<>(200,patientDto,"患者信息");
    }

    /**
     * 查询医生
     * @param department 科室（内科）
     * @param subspecialty 亚专业（心脏科）
     * @return
     */
    @ApiOperation("查询医生")
    @GetMapping("/queryDockerBySearch")
    public BaseResponse<List<DoctorDto>> queryDockerBySearch(@RequestParam String department, @RequestParam String subspecialty){
        return doctorService.queryDoctorBySearch(department,subspecialty);
    }

//    @GetMapping("/test")
//    public void test(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        String id = request.getSession().getId();
//        response.getWriter().write(id);
//    }

    /**
     * 提交挂号单
     * @param medicalrecordVo
     * @return
     */
    @ApiOperation("提交挂号单")
    @PostMapping("/appointment")
    public BaseResponse<MedicalRecordDto> appointmentByPatient(@RequestBody MedicalRecordVo medicalrecordVo){
        String subspecialty = medicalrecordVo.getSubspecialty();
        log.info(subspecialty);
        return patientService.appointmentByPatient(medicalrecordVo);
    }
}

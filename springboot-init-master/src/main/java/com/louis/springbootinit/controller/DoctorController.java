package com.louis.springbootinit.controller;

import com.louis.springbootinit.common.BaseResponse;
import com.louis.springbootinit.model.dto.MedicalRecordDto;
import com.louis.springbootinit.model.dto.doctor.DoctorDto;
import com.louis.springbootinit.model.dto.patient.PatientDto;
import com.louis.springbootinit.model.entity.Doctor;
import com.louis.springbootinit.service.DoctorService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author louis
 * @version 1.0
 * @date 2023/12/12 19:44
 */
@RestController
@Slf4j
@RequestMapping("/doctor")
public class DoctorController {
    @Resource
    private DoctorService doctorService;

    @ApiOperation("根据科室和专业查询医生")
    @GetMapping("queryDoctorList")
    public BaseResponse<List<DoctorDto>> queryDoctorListByDoctor(@RequestParam String department, @RequestParam String subspecialty){
        return doctorService.queryDoctorBySearch(department,subspecialty);
    }


    @ApiOperation("根据id查询医生")
    @GetMapping("/queryDoctor/{id}")
    public BaseResponse<DoctorDto> queryDoctorById(@PathVariable("id") int id){
        return doctorService.showDoctorInfoBy(id);
    }

    /**
     * 查询当天就诊病人列表
     * @return
     */
    @ApiOperation("默认查询当天挂号单列表")
    @PostMapping("/queryMedicalRecordList")
    public BaseResponse<List<MedicalRecordDto>> queryMedicalRecordList(){
        return doctorService.queryMedicalRecordList();
    }

    /**
     * 确认就诊
     * @param id
     * @return
     */
    @PostMapping("/agree/{id}")
    public BaseResponse<Boolean> agreePatientAppointment(@PathVariable("id") int id){
        return doctorService.agree(id);
    }
}

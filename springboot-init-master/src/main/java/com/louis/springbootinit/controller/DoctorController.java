package com.louis.springbootinit.controller;

import com.louis.springbootinit.common.BaseResponse;
import com.louis.springbootinit.model.dto.doctor.DoctorDto;
import com.louis.springbootinit.model.dto.patient.PatientDto;
import com.louis.springbootinit.model.entity.Doctor;
import com.louis.springbootinit.service.DoctorService;
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

    @GetMapping("queryDoctorList")
    public BaseResponse<List<DoctorDto>> queryDoctorListByDoctor(@RequestParam String department, @RequestParam String subspecialty){
        return doctorService.queryDoctorBySearch(department,subspecialty);
    }


    @GetMapping("/queryDoctor/{id}")
    public BaseResponse<DoctorDto> queryDoctorById(@PathVariable("id") int id){
        return doctorService.showDoctorInfoBy(id);
    }
}

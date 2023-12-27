package com.louis.springbootinit.controller;

import com.louis.springbootinit.common.BaseResponse;
import com.louis.springbootinit.model.dto.MedicalRecordDto;
import com.louis.springbootinit.model.dto.doctor.DoctorDto;
import com.louis.springbootinit.model.dto.patient.PatientDto;
import com.louis.springbootinit.model.entity.Doctor;
import com.louis.springbootinit.model.entity.Drug;
import com.louis.springbootinit.model.vo.medicalRecord.MedicalRecordForm;
import com.louis.springbootinit.service.DoctorService;
import com.louis.springbootinit.service.DrugService;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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

    @Resource
    private DrugService drugNameService;


    /**
     * 查询当天就诊病人列表
     * @return
     */
    @ApiOperation("默认查询当天挂号单列表")
    @GetMapping("/queryMedicalRecordList")
    public BaseResponse<List<MedicalRecordDto>> queryMedicalRecordList(){
        return doctorService.queryMedicalRecordList();
    }
    /**
     * 确认就诊
     * @param id
     * @return
     */
    @ApiOperation("确认就诊")
    @PostMapping("/agree/{id}")
    public BaseResponse<Boolean> agreePatientAppointment(@PathVariable("id") String id){
        return doctorService.agree(Integer.parseInt(id));
    }

    /**
     * 填写患者诊断单
     * @param id 患者id
     * @return
     */
    @ApiOperation("创建患者诊断单")
    @PostMapping("/createJudgeDiagnosis/{id}")
    public BaseResponse<MedicalRecordDto> createJudgeDiagnosis(@PathVariable(value = "id") String id){

        return doctorService.createJudgeDiagnosis(Integer.parseInt(id));
    }
    /**
     * 提交就诊单
     * @param medicalRecordForm
     * @return
     */
    @ApiOperation("提交就诊单")
    @PostMapping("/submitMedicalRecord")
    public BaseResponse<MedicalRecordDto> submitMedicalRecord(MedicalRecordForm medicalRecordForm, HttpServletRequest request){
        return doctorService.submitMedicalRecord(medicalRecordForm,request);
    }
    /**
     * 模糊查询
     * @return
     */
    @ApiOperation("模糊查询药品列表")
    @GetMapping("/queryDrugList")
    public BaseResponse<List<Drug>> queryDrugList(String drugName){
        return doctorService.queryDrugList(drugName);
    }
    @ApiOperation("查询所有药品")
    @GetMapping("/queryAllDrugList")
    public BaseResponse<List<Drug>> queryAllDrugList(){
        return new BaseResponse<>(0,drugNameService.list(),"查询成功");
    }

    @ApiOperation("根据id查询医生")
    @GetMapping("/queryDoctor")
    public BaseResponse<DoctorDto> queryDoctorById(){
        return doctorService.showDoctorInfo();
    }
}

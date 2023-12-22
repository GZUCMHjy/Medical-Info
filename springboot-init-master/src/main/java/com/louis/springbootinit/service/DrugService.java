package com.louis.springbootinit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.louis.springbootinit.common.BaseResponse;
import com.louis.springbootinit.model.dto.MedicalRecordDto;
import com.louis.springbootinit.model.dto.doctor.DoctorDto;
import com.louis.springbootinit.model.entity.Doctor;
import com.louis.springbootinit.model.entity.Drug;
import com.louis.springbootinit.model.vo.medicalRecord.MedicalRecordForm;
import com.louis.springbootinit.model.vo.user.LoginForm;
import com.louis.springbootinit.model.vo.user.RegisterForm;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author louis
 * @version 1.0
 * @date 2023/12/10 10:22
 */
public interface DrugService extends IService<Drug> {
}

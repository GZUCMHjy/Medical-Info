package com.louis.springbootinit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.louis.springbootinit.common.BaseResponse;
import com.louis.springbootinit.common.ErrorCode;
import com.louis.springbootinit.exception.BusinessException;
import com.louis.springbootinit.mapper.DoctorMapper;
import com.louis.springbootinit.mapper.MedicalRecordMapper;
import com.louis.springbootinit.model.entity.Doctor;
import com.louis.springbootinit.model.entity.MedicalRecord;
import com.louis.springbootinit.service.DoctorService;
import com.louis.springbootinit.service.MedicalRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author louis
 * @version 1.0
 * @date 2023/12/10 10:31
 */
@Service
@Slf4j
public class MedicalRecordServiceImpl extends ServiceImpl<MedicalRecordMapper, MedicalRecord> implements MedicalRecordService {

}

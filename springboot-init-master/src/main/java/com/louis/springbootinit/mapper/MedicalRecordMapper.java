package com.louis.springbootinit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.louis.springbootinit.model.entity.Doctor;
import com.louis.springbootinit.model.entity.MedicalRecord;
import org.apache.ibatis.annotations.Mapper;

import java.util.Queue;

/**
 * @author louis
 * @version 1.0
 * @date 2023/12/10 10:16
 */
@Mapper
public interface MedicalRecordMapper extends BaseMapper<MedicalRecord> {

}

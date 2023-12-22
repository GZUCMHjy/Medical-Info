package com.louis.springbootinit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.louis.springbootinit.model.entity.Doctor;
import com.louis.springbootinit.model.entity.Drug;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author louis
 * @version 1.0
 * @date 2023/12/10 10:16
 */
@Mapper
public interface DrugMapper extends BaseMapper<Drug> {

}

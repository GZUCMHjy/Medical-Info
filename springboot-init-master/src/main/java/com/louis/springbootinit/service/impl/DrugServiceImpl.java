package com.louis.springbootinit.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.louis.springbootinit.mapper.DrugMapper;
import com.louis.springbootinit.model.entity.Drug;
import com.louis.springbootinit.service.DrugService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author louis
 * @version 1.0
 * @date 2023/12/10 10:31
 */
@Service
@Slf4j
public class DrugServiceImpl extends ServiceImpl<DrugMapper, Drug> implements DrugService {

}

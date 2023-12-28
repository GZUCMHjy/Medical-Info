package com.louis.springbootinit.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.louis.springbootinit.mapper.DrugMapper;
import com.louis.springbootinit.model.entity.Drug;
import com.louis.springbootinit.service.DrugService;
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
public class DrugServiceImpl extends ServiceImpl<DrugMapper, Drug> implements DrugService {
    @Resource
    private DrugMapper drugMapper;


    @Override
    public List<Drug> drugList(String drugType) {
        QueryWrapper<Drug> drugQueryWrapper = new QueryWrapper<>();
        drugQueryWrapper.like("Type",drugType);
        List<Drug> drugs = list(drugQueryWrapper);
        return drugs;
    }
}

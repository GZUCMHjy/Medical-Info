package com.louis.springbootinit.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.louis.springbootinit.common.BaseResponse;
import com.louis.springbootinit.common.ErrorCode;
import com.louis.springbootinit.exception.BusinessException;
import com.louis.springbootinit.mapper.DoctorMapper;
import com.louis.springbootinit.mapper.PatientMapper;
import com.louis.springbootinit.model.dto.patient.PatientDto;
import com.louis.springbootinit.model.entity.Doctor;
import com.louis.springbootinit.model.entity.Patient;
import com.louis.springbootinit.model.vo.patient.PatientEditProfileVo;
import com.louis.springbootinit.model.vo.patient.PatientLoginVo;
import com.louis.springbootinit.model.vo.patient.PatientRegisterVo;
import com.louis.springbootinit.service.DoctorService;
import com.louis.springbootinit.service.PatientService;
import com.louis.springbootinit.utils.PatientHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTimeUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;

/**
 * @author louis
 * @version 1.0
 * @date 2023/12/10 10:31
 */
@Service
@Slf4j
public class DoctorServiceImpl extends ServiceImpl<DoctorMapper, Doctor> implements DoctorService {
    @Resource
    private DoctorMapper doctorMapper;
    /**
     * 查询挂号医生
     * @param department
     * @param subspecialty
     * @return
     */
    @Override
    public BaseResponse<List<Doctor>> queryDockerBySearch(String department, String subspecialty) {
        if(department == null || subspecialty == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数不能为空");
        }
        // 构建选择器
        QueryWrapper<Doctor> doctorQuery = new QueryWrapper<>();
        doctorQuery.eq("Department",department).eq("Subspecialty",subspecialty);
        List<Doctor> doctors = doctorMapper.selectList(doctorQuery);

        return new BaseResponse<>(200,doctors,"查询成功");
    }
}

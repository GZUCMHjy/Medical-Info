package com.louis.springbootinit;

import com.louis.springbootinit.mapper.DoctorMapper;
import com.louis.springbootinit.model.entity.Doctor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * 主类测试
 
 */
@SpringBootTest
class MainApplicationTests {
// 已删测试
    @Resource
    private DoctorMapper doctorMapper;

    /**
     * 添加医生
     */
    @Test
    public void addDoctor(){
        Doctor doctor = new Doctor();
        doctor.setName("李四");
        doctor.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
        doctor.setDepartment("外科");
        doctor.setSubspecialty("胸外科");
        doctor.setExpertise("胸外科专家");
        doctor.setUpdatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
        doctorMapper.insert(doctor);
    }
}

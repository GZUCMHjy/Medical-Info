package com.louis.springbootinit;

import com.louis.springbootinit.mapper.DoctorMapper;
import com.louis.springbootinit.mapper.MedicalRecordMapper;
import com.louis.springbootinit.model.entity.Doctor;
import com.louis.springbootinit.model.entity.MedicalRecord;
import org.joda.time.DateTimeUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * 主类测试
 
 */
@SpringBootTest
class MainApplicationTests {
// 已删测试
    @Resource
    private DoctorMapper doctorMapper;

    @Resource
    private MedicalRecordMapper medicalRecordMapper;

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
    @Test
    public void addMedicalRecord(){
        MedicalRecord medicalRecord = new MedicalRecord();
        // 医生挂号数据
        medicalRecord.setSign("等待中");
        medicalRecord.setDepartment("");
        medicalRecord.setSubspecialty("");
        medicalRecord.setPatient_Id(-1946628095);
        medicalRecord.setDoctor_Id(-822538238);
        medicalRecord.setCreatedAt(new java.sql.Timestamp(DateTimeUtils.currentTimeMillis()));
        medicalRecord.setUpdatedAt(new java.sql.Timestamp(DateTimeUtils.currentTimeMillis()));
        // 格式化预约时间
        String appointTime ="";
        // SimpleDateFormat sdf = new SimpleDateFormat("yy:MM:dd HH:mm");
        medicalRecord.setAppointTime(appointTime);
        // 4. 插入挂号记录
        medicalRecordMapper.insert(medicalRecord);
    }

    /**
     * 切割字符串
     */
    @Test
    public void splitStr(){
        String input = "a\rb\rc\rd";

        // 使用 split 方法切割字符串
        String[] stringArray = input.split("\\r");

        // 打印结果
        Arrays.stream(stringArray).forEach(System.out::print);
    }
}

package com.louis.springbootinit.model.vo.medicalRecord;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.apache.poi.hpsf.Decimal;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author louis
 * @version 1.0
 * @date 2023/12/21 21:55
 */
@Data
public class MedicalRecordForm implements Serializable {

    private static final long serialVersionUID = 881412327013747275L;
    /**
     * 诊断单Id
     */
    private Integer Id;
    /**
     * 诊断结果
     */
    private String DiagnosisPlan;
    /**
     * 医生姓名
     */
    private String doctor_name;
    /**
     * 患者姓名
     */
    private String patient_name;
    /**
     * 科室
     */
    private String Department;
    /**
     * 亚分科
     */
    private String Subspecialty;

    /**
     * 处方
     */
    private String Prescription;
    /**
     * 就诊人姓名
     */
    private String PatientName;
    /**
     * 医生姓名
     */
    private String DoctorName;

}

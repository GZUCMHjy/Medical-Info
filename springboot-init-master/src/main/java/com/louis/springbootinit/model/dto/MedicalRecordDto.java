package com.louis.springbootinit.model.dto;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author louis
 * @version 1.0
 * @date 2023/12/12 15:41
 */
@Data
public class MedicalRecordDto implements Serializable {

    private static final long serialVersionUID = -4753479612525349365L;

    /**
     * 就诊单号Id
     */
    private Integer Id;
    /**
     * 就诊对象Id
     */
    private Integer Patient_Id;
    /**
     * 医生Id
     */
    private Integer Doctor_Id;
    /**
     * 诊断结果和方案
     */
    private String DiagnosisPlan;
    /**
     * 诊断总费用（诊断 + 药品）
     */
    private BigDecimal Cost;
    /**
     * 所挂科室
     */
    private String Department;
    /**
     * 所挂亚分科
     */
    private String Subspecialty;

    /**
     * 挂号状态(等待中，就诊中，已取消，已完成)
     */
    private String Sign;

    /**
     * 预约时间
     */
    private String AppointTime;
    /**
     * 就诊人姓名
     */
    private String PatientName;
    /**
     * 医生姓名
     */
    private String DoctorName;
}


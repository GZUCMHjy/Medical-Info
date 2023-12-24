package com.louis.springbootinit.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author louis
 * @version 1.0
 * @date 2023/12/23 10:05
 */
@Data
public class Registered implements Serializable {

    private static final long serialVersionUID = 6664801453606005968L;
    /**
     * 患者姓名
     */
    private String patientName;
    /**
     * 医生姓名
     */
    private String doctorName;
    /**
     * 科室
     */
    private String department;
    /**
     * 亚分科
     */
    private String subspecialty;
    /**
     * 诊断费用
     */
    private BigDecimal cost ;
    /**
     * 就诊号
     */
    private Integer medicalRecordId;

    private String appointTime;
}

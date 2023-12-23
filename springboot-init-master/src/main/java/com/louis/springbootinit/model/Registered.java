package com.louis.springbootinit.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author louis
 * @version 1.0
 * @date 2023/12/23 10:05
 */
@Data
public class Registered implements Serializable {

    private static final long serialVersionUID = 6664801453606005968L;

    /**
     * 患者id
     */
    private Integer patientId;

    /**
     * 患者姓名
     */
    private String patientName;
    /**
     * 医生id
     */
    private Integer doctorId;

    /**
     * 医生姓名
     */
    private String doctorName;
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
     * 诊断结果和方案
     */
    private String DiagnosisPlan;
    /**
     * 诊断费用
     */
    private BigDecimal Cost ;
}

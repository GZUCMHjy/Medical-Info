package com.louis.springbootinit.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName(value = "medicalrecord")
public class MedicalRecord implements Serializable {

    @TableField(exist = false)
    private static final long serialVersionUID = 1483904168119453335L;
    /**
     * 诊断单Id
     */
    @TableId(type = IdType.AUTO)
    private Integer Id;
    /**
     * 诊断结果和方案
     */
    private String DiagnosisPlan;
    /**
     * 诊断费用
     * 默认零元
     */
    private BigDecimal Cost = BigDecimal.valueOf(0);
    /**
     * 创建时间
     */
    private Timestamp CreatedAt;
    /**
     * 更新时间
     */
    private Timestamp UpdatedAt;
    /**
     * 就诊对象Id
     */
    private Integer PatientId;
    /**
     * 医生Id
     */
    private Integer DoctorId;
    /**
     * 科室
     */
    private String Department;
    /**
     * 亚分科
     */
    private String Subspecialty;

    /**
     * 挂号状态(等待中，就诊中，已取消，已完成)
     */
    private String Sign;
}

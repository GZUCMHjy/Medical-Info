package com.louis.springbootinit.model.vo.medicalRecord;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author louis
 * @version 1.0
 * @date 2023/12/12 15:52
 */
@Data
public class MedicalRecordVo implements Serializable {

    private static final long serialVersionUID = 5108889941813371585L;
    /**
     * 就诊对象Id
     */
    private Integer Patient_Id;
    /**
     * 医生Id（工号）
     */
    private Integer Doctor_Id;
    /**
     * 所挂科室
     */
    private String Department;
    /**
     * 所挂亚分科
     */
    private String Subspecialty;

    /**
     * 预约时间
     */
    private String AppointTime;
}

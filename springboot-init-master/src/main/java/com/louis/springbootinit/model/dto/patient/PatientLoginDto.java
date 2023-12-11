package com.louis.springbootinit.model.dto.patient;

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author louis
 * @version 1.0
 * @date 2023/12/10 9:50
 */
@Data
public class PatientLoginDto implements Serializable {

    private static final long serialVersionUID = -5746543684698208292L;
    /**
     * 患者账户ID
     */
    private Long Id;
    /**
     * 账号状态（注销-1、登录失败0、登录成功1、未登录2）
     */
    private Integer AccountStatus;
}

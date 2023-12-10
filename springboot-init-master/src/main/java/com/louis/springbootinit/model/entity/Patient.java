package com.louis.springbootinit.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author louis
 * @version 1.0
 * @date 2023/12/10 9:37
 */
@Data
@TableName(value = "patient")
public class Patient implements Serializable {

    @TableField(exist = false)
    private static final long serialVersionUID = 2111988488319990858L;

    /**
     * 账号状态（注销-1、注册成功0、登录成功1、未登录2）
     */
    @TableField(exist = false)
    private Integer AccountStatus;
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Integer Id;

    /**
     * 账号
     */
    private String Account;
    /**
     * 密码
     */
    private String Password;
    /**
     * 创建时间
     */
    private Timestamp CreatedAt;
    /**
     * 更新时间
     */
    private Timestamp UpdatedAt;
    /**
     * 用户状态（候诊0、就诊中1、就诊结束2、延后3，未挂号4）
     * 默认未挂号
     */
    private Integer PatientStatus = 4;
    /**
     * 性别
     */
    private Boolean Gender;
    /**
     * 患者年龄
     */
    private Integer Age;
    /**
     * 患者姓名
     */
    private String Name;
    /**
     * 就诊时间
     */
    private Timestamp VisitTime;



}

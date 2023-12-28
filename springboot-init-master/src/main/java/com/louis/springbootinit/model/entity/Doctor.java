package com.louis.springbootinit.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.apache.poi.hpsf.Decimal;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author louis
 * @version 1.0
 * @date 2023/12/11 16:29
 */
@Data
@TableName(value = "doctor")
public class Doctor implements Serializable {

    @TableField(exist = false)
    private static final long serialVersionUID = -4158790727764710460L;

    /**
     * 账号状态（注销-1、注册成功0、登录成功1、未登录2）
     */
    @TableField(exist = false)
    private Integer AccountStatus ;

    @TableId(type = IdType.ASSIGN_ID)
    private Integer Id;

    /**
     * 医生姓名
     */
    private String Name;

    /**
     * 科室
     */
    private String Department;

    /**
     * 注册时间
     */
    private Timestamp CreatedAt;
    /**
     * 更新时间
     */
    private Timestamp UpdatedAt;
    /**
     * 医生擅长领域
     */
    private String Expertise;
    /**
     * 亚分科
     */
    private String Subspecialty;

    /**
     * 剩余挂号量
     * 默认50
     */
    private Integer Vacancy = 50;
    /**
     * 账号
     */
    private String Account;
    /**
     * 密码
     */
    private String Password;
    /**
     * 医生职称
     */
    private String Level;

    private String AvatarUrl;
    private BigDecimal Cost;
}

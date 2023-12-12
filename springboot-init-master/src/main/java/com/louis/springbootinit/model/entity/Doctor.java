package com.louis.springbootinit.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.Serializable;
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
}

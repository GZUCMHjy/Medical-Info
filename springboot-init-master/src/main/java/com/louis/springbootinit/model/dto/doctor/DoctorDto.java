package com.louis.springbootinit.model.dto.doctor;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author louis
 * @version 1.0
 * @date 2023/12/13 10:19
 */
@Data
public class DoctorDto  implements Serializable {

    private static final long serialVersionUID = 1793858874942398973L;

    private Integer AccountStatus;

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

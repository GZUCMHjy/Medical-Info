package com.louis.springbootinit.model.vo.patient;

import lombok.Data;

import java.io.Serializable;

/**
 * @author louis
 * @version 1.0
 * @date 2023/12/10 14:31
 */
@Data
public class PatientEditProfileVo implements Serializable {

    private static final long serialVersionUID = -8145700468062709540L;
    /**
     * 患者Id
     */
    private Integer Id;
    /**
     * 昵称（患者姓名）
     */
    private String Name;
    /**
     * 性别（1：男，2：女）
     */
    private String Gender;
    /**
     * 年龄
     */
    private Integer Age;
//    /**
//     * 头像
//     */
//    private String AvatarUrl;
}

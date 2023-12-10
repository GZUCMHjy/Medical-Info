package com.louis.springbootinit.model.vo.patient;

import lombok.Data;

import java.io.Serializable;

/**
 * @author louis
 * @version 1.0
 * @date 2023/12/10 11:17
 */
@Data
public class PatientRegisterVo implements Serializable {

    private static final long serialVersionUID = 1200055976510121638L;
    private String account;
    private String password;
    private String checkPassword;
}

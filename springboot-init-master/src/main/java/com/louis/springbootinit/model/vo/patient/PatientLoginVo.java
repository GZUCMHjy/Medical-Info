package com.louis.springbootinit.model.vo.patient;

import lombok.Data;

import java.io.Serializable;

/**
 * @author louis
 * @version 1.0
 * @date 2023/12/10 9:55
 */
@Data
public class PatientLoginVo implements Serializable {

    private static final long serialVersionUID = 3526678194171774614L;
    private String account;
    private String password;
}

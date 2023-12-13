package com.louis.springbootinit.model.vo.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author louis
 * @version 1.0
 * @date 2023/12/13 9:52
 */
@Data
public class RegisterForm implements Serializable {

    private static final long serialVersionUID = -3984952219298274809L;
    private String Account;
    private String Password;
    private String CheckPassword;
}

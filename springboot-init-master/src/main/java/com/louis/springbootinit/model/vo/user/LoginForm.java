package com.louis.springbootinit.model.vo.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author louis
 * @version 1.0
 * @date 2023/12/13 9:47
 */
@Data
public class LoginForm{

    //private static final long serialVersionUID = -8228583190159830145L;
    private String Account;
    private String Password;
}

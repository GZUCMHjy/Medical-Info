package com.louis.springbootinit.model.dto.patient;

import lombok.Data;

import java.io.Serializable;

/**
 * @author louis
 * @version 1.0
 * @date 2023/12/10 15:08
 */
@Data
public class PatientDto implements Serializable {

    private static final long serialVersionUID = -2621200267805315020L;
    private Integer Id;
    private String Name;
    private String Gender;
    private String Age;
    private String AvatarUrl;
}

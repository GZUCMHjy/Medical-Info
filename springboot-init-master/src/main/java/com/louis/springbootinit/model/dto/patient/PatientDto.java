package com.louis.springbootinit.model.dto.patient;

import lombok.Data;

/**
 * @author louis
 * @version 1.0
 * @date 2023/12/10 15:08
 */
@Data
public class PatientDto {
    private String Name;
    private String Gender;
    private String Age;
    private String AvatarUrl;
}

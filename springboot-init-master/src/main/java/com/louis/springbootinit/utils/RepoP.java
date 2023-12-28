package com.louis.springbootinit.utils;

import com.louis.springbootinit.model.dto.patient.PatientDto;

/**
 * @author louis
 * @version 1.0
 * @date 2023/12/25 22:22
 */
public class RepoP {
    public static PatientDto repo;
    public static void save(PatientDto t){
        repo = t;
    }
    public static PatientDto get(){
        return repo;
    }
    public static void remove(){
        repo = null;
    }
}

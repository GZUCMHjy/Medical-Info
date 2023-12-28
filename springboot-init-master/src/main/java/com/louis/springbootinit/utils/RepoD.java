package com.louis.springbootinit.utils;

import com.louis.springbootinit.model.dto.doctor.DoctorDto;
import com.louis.springbootinit.model.dto.patient.PatientDto;
import com.louis.springbootinit.model.entity.Doctor;

/**
 * @author louis
 * @version 1.0
 * @date 2023/12/25 22:22
 */
public class RepoD {
    public static DoctorDto repo;
    public static void save(DoctorDto t){
        repo = t;
    }
    public static DoctorDto get(){
        return repo;
    }
    public static void remove(){
        repo = null;
    }
}

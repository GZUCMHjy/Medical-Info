package com.louis.springbootinit.utils;

import com.louis.springbootinit.model.dto.patient.PatientDto;

/**
 * @author louis
 * @version 1.0
 * @date 2023/12/10 19:52
 */
public class PatientHolder {
    private static final ThreadLocal<PatientDto> tl = new ThreadLocal<>();

    public static void savePatient(PatientDto patient){
        tl.set(patient);
    }

    public static PatientDto getPatient(){
        return tl.get();
    }

    public static void removePatient(){
        tl.remove();
    }
}

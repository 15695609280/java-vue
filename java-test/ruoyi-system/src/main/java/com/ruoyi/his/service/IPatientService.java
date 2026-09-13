package com.ruoyi.his.service;

import com.ruoyi.his.domain.Patient;
import java.util.List;

/**
 * 患者Service接口
 */
public interface IPatientService
{
    public Patient selectPatientByPatientId(Long patientId);

    public List<Patient> selectPatientList(Patient patient);

    public int insertPatient(Patient patient);

    public int updatePatient(Patient patient);

    public int deletePatientByPatientIds(Long[] patientIds);

    public int deletePatientByPatientId(Long patientId);
}

package com.ruoyi.his.mapper;

import com.ruoyi.his.domain.Patient;
import java.util.List;

/**
 * 患者Mapper接口
 */
public interface PatientMapper
{
    public Patient selectPatientByPatientId(Long patientId);

    public List<Patient> selectPatientList(Patient patient);

    public int insertPatient(Patient patient);

    public int updatePatient(Patient patient);

    public int deletePatientByPatientId(Long patientId);

    public int deletePatientByPatientIds(Long[] patientIds);
}

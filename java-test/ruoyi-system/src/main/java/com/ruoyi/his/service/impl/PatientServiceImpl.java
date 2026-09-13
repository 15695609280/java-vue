package com.ruoyi.his.service.impl;

import com.ruoyi.his.domain.Patient;
import com.ruoyi.his.mapper.PatientMapper;
import com.ruoyi.his.service.IPatientService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 患者Service业务层处理
 */
@Service
public class PatientServiceImpl implements IPatientService
{
    @Autowired
    private PatientMapper patientMapper;

    @Override
    public Patient selectPatientByPatientId(Long patientId)
    {
        return patientMapper.selectPatientByPatientId(patientId);
    }

    @Override
    public List<Patient> selectPatientList(Patient patient)
    {
        return patientMapper.selectPatientList(patient);
    }

    @Override
    public int insertPatient(Patient patient)
    {
        patient.setCreateTime(com.ruoyi.common.utils.DateUtils.getNowDate());
        int rows = patientMapper.insertPatient(patient);
        return rows;
    }

    @Override
    public int updatePatient(Patient patient)
    {
        patient.setUpdateTime(com.ruoyi.common.utils.DateUtils.getNowDate());
        return patientMapper.updatePatient(patient);
    }

    @Override
    public int deletePatientByPatientIds(Long[] patientIds)
    {
        return patientMapper.deletePatientByPatientIds(patientIds);
    }

    @Override
    public int deletePatientByPatientId(Long patientId)
    {
        return patientMapper.deletePatientByPatientId(patientId);
    }
}

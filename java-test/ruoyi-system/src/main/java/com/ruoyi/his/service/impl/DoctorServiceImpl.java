package com.ruoyi.his.service.impl;

import com.ruoyi.his.domain.Doctor;
import com.ruoyi.his.mapper.DoctorMapper;
import com.ruoyi.his.service.IDoctorService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 医生Service业务层处理
 */
@Service
public class DoctorServiceImpl implements IDoctorService
{
    @Autowired
    private DoctorMapper doctorMapper;

    @Override
    public Doctor selectDoctorByDoctorId(Long doctorId)
    {
        return doctorMapper.selectDoctorByDoctorId(doctorId);
    }

    @Override
    public List<Doctor> selectDoctorList(Doctor doctor)
    {
        return doctorMapper.selectDoctorList(doctor);
    }

    @Override
    public int insertDoctor(Doctor doctor)
    {
        doctor.setCreateTime(com.ruoyi.common.utils.DateUtils.getNowDate());
        int rows = doctorMapper.insertDoctor(doctor);
        return rows;
    }

    @Override
    public int updateDoctor(Doctor doctor)
    {
        doctor.setUpdateTime(com.ruoyi.common.utils.DateUtils.getNowDate());
        return doctorMapper.updateDoctor(doctor);
    }

    @Override
    public int deleteDoctorByDoctorIds(Long[] doctorIds)
    {
        return doctorMapper.deleteDoctorByDoctorIds(doctorIds);
    }

    @Override
    public int deleteDoctorByDoctorId(Long doctorId)
    {
        return doctorMapper.deleteDoctorByDoctorId(doctorId);
    }
}

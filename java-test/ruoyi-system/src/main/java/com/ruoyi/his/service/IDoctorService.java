package com.ruoyi.his.service;

import com.ruoyi.his.domain.Doctor;
import java.util.List;

/**
 * 医生Service接口
 */
public interface IDoctorService
{
    public Doctor selectDoctorByDoctorId(Long doctorId);

    public List<Doctor> selectDoctorList(Doctor doctor);

    public int insertDoctor(Doctor doctor);

    public int updateDoctor(Doctor doctor);

    public int deleteDoctorByDoctorIds(Long[] doctorIds);

    public int deleteDoctorByDoctorId(Long doctorId);
}

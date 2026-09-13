package com.ruoyi.his.mapper;

import com.ruoyi.his.domain.Doctor;
import java.util.List;

/**
 * 医生Mapper接口
 */
public interface DoctorMapper
{
    public Doctor selectDoctorByDoctorId(Long doctorId);

    public List<Doctor> selectDoctorList(Doctor doctor);

    public int insertDoctor(Doctor doctor);

    public int updateDoctor(Doctor doctor);

    public int deleteDoctorByDoctorId(Long doctorId);

    public int deleteDoctorByDoctorIds(Long[] doctorIds);
}

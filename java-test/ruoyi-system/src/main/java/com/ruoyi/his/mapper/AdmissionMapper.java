package com.ruoyi.his.mapper;

import com.ruoyi.his.domain.Admission;
import java.util.List;

/**
 * 住院登记Mapper接口
 */
public interface AdmissionMapper
{
    public Admission selectAdmissionByAdmId(Long admId);

    public List<Admission> selectAdmissionList(Admission admission);

    public int insertAdmission(Admission admission);

    public int updateAdmission(Admission admission);

    public int deleteAdmissionByAdmId(Long admId);

    public int deleteAdmissionByAdmIds(Long[] admIds);

    /** 查询患者在院记录 */
    public Admission selectInHospitalByPatient(Long patientId);
}

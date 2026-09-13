package com.ruoyi.his.service;

import com.ruoyi.his.domain.Admission;
import java.util.List;

/**
 * 住院登记Service接口
 */
public interface IAdmissionService
{
    public Admission selectAdmissionByAdmId(Long admId);

    public List<Admission> selectAdmissionList(Admission admission);

    public int insertAdmission(Admission admission);

    public int updateAdmission(Admission admission);

    public int deleteAdmissionByAdmIds(Long[] admIds);

    public int deleteAdmissionByAdmId(Long admId);

    /** 住院登记(占用床位) */
    public Long admit(Admission admission);

    /** 出院(释放床位+生成床位费账单) */
    public int discharge(Long admId);

    /** 查询患者在院记录 */
    public Admission selectInHospitalByPatient(Long patientId);
}

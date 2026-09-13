package com.ruoyi.his.mapper;

import com.ruoyi.his.domain.Prescription;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * 处方Mapper接口
 */
public interface PrescriptionMapper
{
    public Prescription selectPrescriptionByRxId(Long rxId);

    public List<Prescription> selectPrescriptionList(Prescription prescription);

    public int insertPrescription(Prescription prescription);

    public int updatePrescription(Prescription prescription);

    public int deletePrescriptionByRxId(Long rxId);

    public int deletePrescriptionByRxIds(Long[] rxIds);

    /** 修改处方状态 */
    public int updateStatus(@Param("rxId") Long rxId, @Param("status") String status);
}

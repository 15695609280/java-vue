package com.ruoyi.his.mapper;

import com.ruoyi.his.domain.MedicalOrder;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * 医嘱Mapper接口
 */
public interface MedicalOrderMapper
{
    public MedicalOrder selectMedicalOrderByOrderId(Long orderId);

    public List<MedicalOrder> selectMedicalOrderList(MedicalOrder medicalOrder);

    public int insertMedicalOrder(MedicalOrder medicalOrder);

    public int updateMedicalOrder(MedicalOrder medicalOrder);

    public int deleteMedicalOrderByOrderId(Long orderId);

    public int deleteMedicalOrderByOrderIds(Long[] orderIds);

    /** 修改医嘱状态 */
    public int updateStatus(@Param("orderId") Long orderId, @Param("status") String status, @Param("execBy") String execBy);
}

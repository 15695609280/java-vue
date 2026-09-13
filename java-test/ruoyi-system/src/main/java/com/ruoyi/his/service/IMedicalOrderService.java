package com.ruoyi.his.service;

import com.ruoyi.his.domain.MedicalOrder;
import java.util.List;

/**
 * 医嘱Service接口
 */
public interface IMedicalOrderService
{
    public MedicalOrder selectMedicalOrderByOrderId(Long orderId);

    public List<MedicalOrder> selectMedicalOrderList(MedicalOrder medicalOrder);

    public int insertMedicalOrder(MedicalOrder medicalOrder);

    public int updateMedicalOrder(MedicalOrder medicalOrder);

    public int deleteMedicalOrderByOrderIds(Long[] orderIds);

    public int deleteMedicalOrderByOrderId(Long orderId);

    /** 执行医嘱(关联药品自动生成费用) */
    public int execute(Long orderId);

    /** 停止医嘱 */
    public int stop(Long orderId);
}

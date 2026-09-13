package com.ruoyi.his.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.his.domain.Charge;
import com.ruoyi.his.domain.Drug;
import com.ruoyi.his.domain.MedicalOrder;
import com.ruoyi.his.mapper.ChargeMapper;
import com.ruoyi.his.mapper.DrugMapper;
import com.ruoyi.his.mapper.MedicalOrderMapper;
import com.ruoyi.his.service.IMedicalOrderService;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 医嘱Service业务层处理
 */
@Service
public class MedicalOrderServiceImpl implements IMedicalOrderService
{
    @Autowired
    private MedicalOrderMapper medicalOrderMapper;

    @Autowired
    private DrugMapper drugMapper;

    @Autowired
    private ChargeMapper chargeMapper;

    @Override
    public MedicalOrder selectMedicalOrderByOrderId(Long orderId)
    {
        return medicalOrderMapper.selectMedicalOrderByOrderId(orderId);
    }

    @Override
    public List<MedicalOrder> selectMedicalOrderList(MedicalOrder medicalOrder)
    {
        return medicalOrderMapper.selectMedicalOrderList(medicalOrder);
    }

    @Override
    public int insertMedicalOrder(MedicalOrder medicalOrder)
    {
        medicalOrder.setCreateTime(com.ruoyi.common.utils.DateUtils.getNowDate());
        int rows = medicalOrderMapper.insertMedicalOrder(medicalOrder);
        return rows;
    }

    @Override
    public int updateMedicalOrder(MedicalOrder medicalOrder)
    {
        medicalOrder.setUpdateTime(com.ruoyi.common.utils.DateUtils.getNowDate());
        return medicalOrderMapper.updateMedicalOrder(medicalOrder);
    }

    @Override
    public int deleteMedicalOrderByOrderIds(Long[] orderIds)
    {
        return medicalOrderMapper.deleteMedicalOrderByOrderIds(orderIds);
    }

    @Override
    public int deleteMedicalOrderByOrderId(Long orderId)
    {
        return medicalOrderMapper.deleteMedicalOrderByOrderId(orderId);
    }

    /** 执行医嘱：置已执行，关联药品自动计费 */
    @Override
    @Transactional
    public int execute(Long orderId)
    {
        MedicalOrder order = medicalOrderMapper.selectMedicalOrderByOrderId(orderId);
        if (order == null) { throw new ServiceException("医嘱不存在"); }
        if (!"0".equals(order.getOrderStatus())) { throw new ServiceException("医嘱已执行或已停止"); }
        int rows = medicalOrderMapper.updateStatus(orderId, "1", SecurityUtils.getUsername());
        if (order.getDrugId() != null)
        {
            Drug drug = drugMapper.selectDrugByDrugId(order.getDrugId());
            if (drug != null)
            {
                Charge charge = new Charge();
                charge.setChargeNo("YZ" + DateUtils.dateTimeNow("yyyyMMddHHmmss") + String.format("%03d", new java.util.Random().nextInt(1000)));
                charge.setPatientId(order.getPatientId());
                charge.setSourceType("4");
                charge.setSourceId(orderId);
                charge.setItemName("用药-" + drug.getDrugName());
                charge.setPrice(drug.getPrice());
                charge.setQuantity(1);
                charge.setAmount(drug.getPrice());
                charge.setChargeStatus("0");
                chargeMapper.insertCharge(charge);
            }
        }
        return rows;
    }

    /** 停止医嘱 */
    @Override
    public int stop(Long orderId)
    {
        MedicalOrder order = medicalOrderMapper.selectMedicalOrderByOrderId(orderId);
        if (order == null) { throw new ServiceException("医嘱不存在"); }
        if ("2".equals(order.getOrderStatus())) { throw new ServiceException("医嘱已停止"); }
        return medicalOrderMapper.updateStatus(orderId, "2", null);
    }
}

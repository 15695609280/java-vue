package com.ruoyi.his.service;

import com.ruoyi.his.domain.Prescription;
import com.ruoyi.his.domain.PrescriptionItem;
import java.util.List;

/**
 * 处方Service接口
 */
public interface IPrescriptionService
{
    public Prescription selectPrescriptionByRxId(Long rxId);

    public List<Prescription> selectPrescriptionList(Prescription prescription);

    public int insertPrescription(Prescription prescription);

    public int updatePrescription(Prescription prescription);

    public int deletePrescriptionByRxIds(Long[] rxIds);

    public int deletePrescriptionByRxId(Long rxId);

    /** 开具处方(含明细，自动算总额+生成待收费账单) */
    public Long createPrescription(Prescription prescription);

    /** 发药(校验库存->扣减->写出入库记录) */
    public int dispense(Long rxId);

    /** 作废处方(未发药才允许) */
    public int cancel(Long rxId);

    /** 处方明细 */
    public List<PrescriptionItem> selectItemList(Long rxId);
}

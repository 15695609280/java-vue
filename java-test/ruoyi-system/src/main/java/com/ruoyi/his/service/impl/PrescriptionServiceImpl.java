package com.ruoyi.his.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.his.domain.Charge;
import com.ruoyi.his.domain.Drug;
import com.ruoyi.his.domain.Prescription;
import com.ruoyi.his.domain.PrescriptionItem;
import com.ruoyi.his.domain.StockRecord;
import com.ruoyi.his.mapper.ChargeMapper;
import com.ruoyi.his.mapper.DrugMapper;
import com.ruoyi.his.mapper.PrescriptionItemMapper;
import com.ruoyi.his.mapper.PrescriptionMapper;
import com.ruoyi.his.mapper.StockRecordMapper;
import com.ruoyi.his.service.IPrescriptionService;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 处方Service业务层处理
 */
@Service
public class PrescriptionServiceImpl implements IPrescriptionService
{
    @Autowired
    private PrescriptionMapper prescriptionMapper;

    @Autowired
    private PrescriptionItemMapper prescriptionItemMapper;

    @Autowired
    private ChargeMapper chargeMapper;

    @Autowired
    private DrugMapper drugMapper;

    @Autowired
    private StockRecordMapper stockRecordMapper;

    @Override
    public Prescription selectPrescriptionByRxId(Long rxId)
    {
        return prescriptionMapper.selectPrescriptionByRxId(rxId);
    }

    @Override
    public List<Prescription> selectPrescriptionList(Prescription prescription)
    {
        return prescriptionMapper.selectPrescriptionList(prescription);
    }

    @Override
    public int insertPrescription(Prescription prescription)
    {
        prescription.setCreateTime(com.ruoyi.common.utils.DateUtils.getNowDate());
        int rows = prescriptionMapper.insertPrescription(prescription);
        return rows;
    }

    @Override
    public int updatePrescription(Prescription prescription)
    {
        prescription.setUpdateTime(com.ruoyi.common.utils.DateUtils.getNowDate());
        return prescriptionMapper.updatePrescription(prescription);
    }

    @Override
    public int deletePrescriptionByRxIds(Long[] rxIds)
    {
        return prescriptionMapper.deletePrescriptionByRxIds(rxIds);
    }

    @Override
    public int deletePrescriptionByRxId(Long rxId)
    {
        return prescriptionMapper.deletePrescriptionByRxId(rxId);
    }

    /** 开具处方(含明细) */
    @Override
    @Transactional
    public Long createPrescription(Prescription rx)
    {
        if (rx.getItemList() == null || rx.getItemList().isEmpty())
        {
            throw new ServiceException("处方明细不能为空");
        }
        BigDecimal total = BigDecimal.ZERO;
        for (PrescriptionItem item : rx.getItemList())
        {
            if (item.getDrugId() == null || item.getQuantity() == null || item.getQuantity() <= 0)
            {
                throw new ServiceException("明细中药品/数量不完整");
            }
            item.setAmount(item.getPrice().multiply(new BigDecimal(item.getQuantity())));
            total = total.add(item.getAmount());
        }
        rx.setTotalAmount(total);
        rx.setStatus("0");
        rx.setRxNo("CF" + DateUtils.dateTimeNow("yyyyMMddHHmmss") + String.format("%03d", new java.util.Random().nextInt(1000)));
        prescriptionMapper.insertPrescription(rx);
        for (PrescriptionItem item : rx.getItemList())
        {
            item.setRxId(rx.getRxId());
            prescriptionItemMapper.insertPrescriptionItem(item);
        }
        Charge charge = new Charge();
        charge.setChargeNo("CF" + rx.getRxNo());
        charge.setPatientId(rx.getPatientId());
        charge.setSourceType("1");
        charge.setSourceId(rx.getRxId());
        charge.setItemName("处方药品费");
        charge.setPrice(total);
        charge.setQuantity(1);
        charge.setAmount(total);
        charge.setChargeStatus("0");
        chargeMapper.insertCharge(charge);
        return rx.getRxId();
    }

    /** 发药：校验库存 -> 逐行扣减 -> 写出库记录 -> 处方状态置已发药 */
    @Override
    @Transactional
    public int dispense(Long rxId)
    {
        Prescription rx = prescriptionMapper.selectPrescriptionByRxId(rxId);
        if (rx == null) { throw new ServiceException("处方不存在"); }
        if (!"1".equals(rx.getStatus())) { throw new ServiceException("处方未收费或已发药"); }
        List<PrescriptionItem> items = prescriptionItemMapper.selectByRxId(rxId);
        for (PrescriptionItem item : items)
        {
            if (drugMapper.deductStock(item.getDrugId(), item.getQuantity()) <= 0)
            {
                throw new ServiceException("药品【" + item.getDrugName() + "】库存不足");
            }
            Drug drug = drugMapper.selectDrugByDrugId(item.getDrugId());
            StockRecord rec = new StockRecord();
            rec.setRecordNo("CK" + DateUtils.dateTimeNow("yyyyMMddHHmmss") + String.format("%03d", new java.util.Random().nextInt(1000)));
            rec.setRecordType("1");
            rec.setDrugId(item.getDrugId());
            rec.setQuantity(item.getQuantity());
            rec.setAfterStock(drug.getStock());
            rec.setBeforeStock(drug.getStock() + item.getQuantity());
            rec.setSourceId(rxId);
            rec.setOperator(SecurityUtils.getUsername());
            stockRecordMapper.insertStockRecord(rec);
        }
        Prescription upd = new Prescription();
        upd.setRxId(rxId);
        upd.setStatus("2");
        upd.setDispenseBy(SecurityUtils.getUsername());
        upd.setDispenseTime(new Date());
        return prescriptionMapper.updatePrescription(upd);
    }

    /** 作废处方(未发药才允许；已收费的先退费) */
    @Override
    @Transactional
    public int cancel(Long rxId)
    {
        Prescription rx = prescriptionMapper.selectPrescriptionByRxId(rxId);
        if (rx == null) { throw new ServiceException("处方不存在"); }
        if ("2".equals(rx.getStatus())) { throw new ServiceException("已发药的处方不能作废"); }
        if ("1".equals(rx.getStatus()))
        {
            chargeMapper.refundBySource(rxId, "1");
        }
        else
        {
            chargeMapper.cancelBySource(rxId, "1");
        }
        Prescription upd = new Prescription();
        upd.setRxId(rxId);
        upd.setStatus("3");
        return prescriptionMapper.updatePrescription(upd);
    }

    @Override
    public List<PrescriptionItem> selectItemList(Long rxId)
    {
        return prescriptionItemMapper.selectByRxId(rxId);
    }
}

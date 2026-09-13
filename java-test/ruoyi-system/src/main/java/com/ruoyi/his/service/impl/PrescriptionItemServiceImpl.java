package com.ruoyi.his.service.impl;

import com.ruoyi.his.domain.PrescriptionItem;
import com.ruoyi.his.mapper.PrescriptionItemMapper;
import com.ruoyi.his.service.IPrescriptionItemService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 处方明细Service业务层处理
 */
@Service
public class PrescriptionItemServiceImpl implements IPrescriptionItemService
{
    @Autowired
    private PrescriptionItemMapper prescriptionItemMapper;

    @Override
    public PrescriptionItem selectPrescriptionItemByItemId(Long itemId)
    {
        return prescriptionItemMapper.selectPrescriptionItemByItemId(itemId);
    }

    @Override
    public List<PrescriptionItem> selectPrescriptionItemList(PrescriptionItem prescriptionItem)
    {
        return prescriptionItemMapper.selectPrescriptionItemList(prescriptionItem);
    }

    @Override
    public int insertPrescriptionItem(PrescriptionItem prescriptionItem)
    {
        prescriptionItem.setCreateTime(com.ruoyi.common.utils.DateUtils.getNowDate());
        int rows = prescriptionItemMapper.insertPrescriptionItem(prescriptionItem);
        return rows;
    }

    @Override
    public int updatePrescriptionItem(PrescriptionItem prescriptionItem)
    {
        prescriptionItem.setUpdateTime(com.ruoyi.common.utils.DateUtils.getNowDate());
        return prescriptionItemMapper.updatePrescriptionItem(prescriptionItem);
    }

    @Override
    public int deletePrescriptionItemByItemIds(Long[] itemIds)
    {
        return prescriptionItemMapper.deletePrescriptionItemByItemIds(itemIds);
    }

    @Override
    public int deletePrescriptionItemByItemId(Long itemId)
    {
        return prescriptionItemMapper.deletePrescriptionItemByItemId(itemId);
    }

    @Override
    public List<PrescriptionItem> selectByRxId(Long rxId)
    {
        return prescriptionItemMapper.selectByRxId(rxId);
    }
}

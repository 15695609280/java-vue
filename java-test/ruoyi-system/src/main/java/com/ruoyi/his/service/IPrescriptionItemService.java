package com.ruoyi.his.service;

import com.ruoyi.his.domain.PrescriptionItem;
import java.util.List;

/**
 * 处方明细Service接口
 */
public interface IPrescriptionItemService
{
    public PrescriptionItem selectPrescriptionItemByItemId(Long itemId);

    public List<PrescriptionItem> selectPrescriptionItemList(PrescriptionItem prescriptionItem);

    public int insertPrescriptionItem(PrescriptionItem prescriptionItem);

    public int updatePrescriptionItem(PrescriptionItem prescriptionItem);

    public int deletePrescriptionItemByItemIds(Long[] itemIds);

    public int deletePrescriptionItemByItemId(Long itemId);

    /** 按处方查明细 */
    public List<PrescriptionItem> selectByRxId(Long rxId);
}

package com.ruoyi.his.mapper;

import com.ruoyi.his.domain.PrescriptionItem;
import java.util.List;

/**
 * 处方明细Mapper接口
 */
public interface PrescriptionItemMapper
{
    public PrescriptionItem selectPrescriptionItemByItemId(Long itemId);

    public List<PrescriptionItem> selectPrescriptionItemList(PrescriptionItem prescriptionItem);

    public int insertPrescriptionItem(PrescriptionItem prescriptionItem);

    public int updatePrescriptionItem(PrescriptionItem prescriptionItem);

    public int deletePrescriptionItemByItemId(Long itemId);

    public int deletePrescriptionItemByItemIds(Long[] itemIds);

    /** 按处方查明细 */
    public List<PrescriptionItem> selectByRxId(Long rxId);

    /** 按处方删明细 */
    public int deleteByRxId(Long rxId);
}

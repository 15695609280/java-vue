package com.ruoyi.his.mapper;

import com.ruoyi.his.domain.PurchaseItem;
import java.util.List;

/**
 * 采购明细Mapper接口
 */
public interface PurchaseItemMapper
{
    public PurchaseItem selectPurchaseItemByItemId(Long itemId);

    public List<PurchaseItem> selectPurchaseItemList(PurchaseItem purchaseItem);

    public int insertPurchaseItem(PurchaseItem purchaseItem);

    public int updatePurchaseItem(PurchaseItem purchaseItem);

    public int deletePurchaseItemByItemId(Long itemId);

    public int deletePurchaseItemByItemIds(Long[] itemIds);

    /** 按采购单查明细 */
    public List<PurchaseItem> selectByPurchaseId(Long purchaseId);

    /** 按采购单删明细 */
    public int deleteByPurchaseId(Long purchaseId);
}

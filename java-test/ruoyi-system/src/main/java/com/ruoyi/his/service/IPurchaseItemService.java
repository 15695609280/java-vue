package com.ruoyi.his.service;

import com.ruoyi.his.domain.PurchaseItem;
import java.util.List;

/**
 * 采购明细Service接口
 */
public interface IPurchaseItemService
{
    public PurchaseItem selectPurchaseItemByItemId(Long itemId);

    public List<PurchaseItem> selectPurchaseItemList(PurchaseItem purchaseItem);

    public int insertPurchaseItem(PurchaseItem purchaseItem);

    public int updatePurchaseItem(PurchaseItem purchaseItem);

    public int deletePurchaseItemByItemIds(Long[] itemIds);

    public int deletePurchaseItemByItemId(Long itemId);

    /** 按采购单查明细 */
    public List<PurchaseItem> selectByPurchaseId(Long purchaseId);
}

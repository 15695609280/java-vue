package com.ruoyi.his.service;

import com.ruoyi.his.domain.Purchase;
import com.ruoyi.his.domain.PurchaseItem;
import java.util.List;

/**
 * 采购单Service接口
 */
public interface IPurchaseService
{
    public Purchase selectPurchaseByPurchaseId(Long purchaseId);

    public List<Purchase> selectPurchaseList(Purchase purchase);

    public int insertPurchase(Purchase purchase);

    public int updatePurchase(Purchase purchase);

    public int deletePurchaseByPurchaseIds(Long[] purchaseIds);

    public int deletePurchaseByPurchaseId(Long purchaseId);

    /** 新建采购单(含明细) */
    public Long createPurchase(Purchase purchase);

    /** 入库审核(增加库存+写入库记录) */
    public int inbound(Long purchaseId);

    /** 采购明细 */
    public List<PurchaseItem> selectItemList(Long purchaseId);
}

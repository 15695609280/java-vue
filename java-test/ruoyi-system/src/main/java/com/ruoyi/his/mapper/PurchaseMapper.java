package com.ruoyi.his.mapper;

import com.ruoyi.his.domain.Purchase;
import java.util.List;

/**
 * 采购单Mapper接口
 */
public interface PurchaseMapper
{
    public Purchase selectPurchaseByPurchaseId(Long purchaseId);

    public List<Purchase> selectPurchaseList(Purchase purchase);

    public int insertPurchase(Purchase purchase);

    public int updatePurchase(Purchase purchase);

    public int deletePurchaseByPurchaseId(Long purchaseId);

    public int deletePurchaseByPurchaseIds(Long[] purchaseIds);
}

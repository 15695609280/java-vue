package com.ruoyi.his.service.impl;

import com.ruoyi.his.domain.PurchaseItem;
import com.ruoyi.his.mapper.PurchaseItemMapper;
import com.ruoyi.his.service.IPurchaseItemService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 采购明细Service业务层处理
 */
@Service
public class PurchaseItemServiceImpl implements IPurchaseItemService
{
    @Autowired
    private PurchaseItemMapper purchaseItemMapper;

    @Override
    public PurchaseItem selectPurchaseItemByItemId(Long itemId)
    {
        return purchaseItemMapper.selectPurchaseItemByItemId(itemId);
    }

    @Override
    public List<PurchaseItem> selectPurchaseItemList(PurchaseItem purchaseItem)
    {
        return purchaseItemMapper.selectPurchaseItemList(purchaseItem);
    }

    @Override
    public int insertPurchaseItem(PurchaseItem purchaseItem)
    {
        purchaseItem.setCreateTime(com.ruoyi.common.utils.DateUtils.getNowDate());
        int rows = purchaseItemMapper.insertPurchaseItem(purchaseItem);
        return rows;
    }

    @Override
    public int updatePurchaseItem(PurchaseItem purchaseItem)
    {
        purchaseItem.setUpdateTime(com.ruoyi.common.utils.DateUtils.getNowDate());
        return purchaseItemMapper.updatePurchaseItem(purchaseItem);
    }

    @Override
    public int deletePurchaseItemByItemIds(Long[] itemIds)
    {
        return purchaseItemMapper.deletePurchaseItemByItemIds(itemIds);
    }

    @Override
    public int deletePurchaseItemByItemId(Long itemId)
    {
        return purchaseItemMapper.deletePurchaseItemByItemId(itemId);
    }

    @Override
    public List<PurchaseItem> selectByPurchaseId(Long purchaseId)
    {
        return purchaseItemMapper.selectByPurchaseId(purchaseId);
    }
}

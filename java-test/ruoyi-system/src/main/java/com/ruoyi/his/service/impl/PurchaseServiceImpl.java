package com.ruoyi.his.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.his.domain.Drug;
import com.ruoyi.his.domain.Purchase;
import com.ruoyi.his.domain.PurchaseItem;
import com.ruoyi.his.domain.StockRecord;
import com.ruoyi.his.mapper.DrugMapper;
import com.ruoyi.his.mapper.PurchaseItemMapper;
import com.ruoyi.his.mapper.PurchaseMapper;
import com.ruoyi.his.mapper.StockRecordMapper;
import com.ruoyi.his.service.IPurchaseService;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 采购单Service业务层处理
 */
@Service
public class PurchaseServiceImpl implements IPurchaseService
{
    @Autowired
    private PurchaseMapper purchaseMapper;

    @Autowired
    private PurchaseItemMapper purchaseItemMapper;

    @Autowired
    private DrugMapper drugMapper;

    @Autowired
    private StockRecordMapper stockRecordMapper;

    @Override
    public Purchase selectPurchaseByPurchaseId(Long purchaseId)
    {
        return purchaseMapper.selectPurchaseByPurchaseId(purchaseId);
    }

    @Override
    public List<Purchase> selectPurchaseList(Purchase purchase)
    {
        return purchaseMapper.selectPurchaseList(purchase);
    }

    @Override
    public int insertPurchase(Purchase purchase)
    {
        purchase.setCreateTime(com.ruoyi.common.utils.DateUtils.getNowDate());
        int rows = purchaseMapper.insertPurchase(purchase);
        return rows;
    }

    @Override
    public int updatePurchase(Purchase purchase)
    {
        purchase.setUpdateTime(com.ruoyi.common.utils.DateUtils.getNowDate());
        return purchaseMapper.updatePurchase(purchase);
    }

    @Override
    public int deletePurchaseByPurchaseIds(Long[] purchaseIds)
    {
        return purchaseMapper.deletePurchaseByPurchaseIds(purchaseIds);
    }

    @Override
    public int deletePurchaseByPurchaseId(Long purchaseId)
    {
        return purchaseMapper.deletePurchaseByPurchaseId(purchaseId);
    }

    /** 新建采购单(含明细，算总额) */
    @Override
    @Transactional
    public Long createPurchase(Purchase purchase)
    {
        if (purchase.getItemList() == null || purchase.getItemList().isEmpty())
        {
            throw new ServiceException("采购明细不能为空");
        }
        BigDecimal total = BigDecimal.ZERO;
        for (PurchaseItem item : purchase.getItemList())
        {
            if (item.getDrugId() == null || item.getQuantity() == null || item.getQuantity() <= 0)
            {
                throw new ServiceException("明细中药品/数量不完整");
            }
            item.setAmount(item.getPrice().multiply(new BigDecimal(item.getQuantity())));
            total = total.add(item.getAmount());
        }
        purchase.setTotalAmount(total);
        purchase.setStatus("0");
        purchase.setPurchaseNo("CG" + DateUtils.dateTimeNow("yyyyMMddHHmmss") + String.format("%03d", new java.util.Random().nextInt(1000)));
        purchaseMapper.insertPurchase(purchase);
        for (PurchaseItem item : purchase.getItemList())
        {
            item.setPurchaseId(purchase.getPurchaseId());
            purchaseItemMapper.insertPurchaseItem(item);
        }
        return purchase.getPurchaseId();
    }

    /** 入库审核：逐行加库存 + 写入库记录 */
    @Override
    @Transactional
    public int inbound(Long purchaseId)
    {
        Purchase purchase = purchaseMapper.selectPurchaseByPurchaseId(purchaseId);
        if (purchase == null) { throw new ServiceException("采购单不存在"); }
        if (!"0".equals(purchase.getStatus())) { throw new ServiceException("采购单已入库或已取消"); }
        List<PurchaseItem> items = purchaseItemMapper.selectByPurchaseId(purchaseId);
        for (PurchaseItem item : items)
        {
            drugMapper.addStock(item.getDrugId(), item.getQuantity());
            Drug drug = drugMapper.selectDrugByDrugId(item.getDrugId());
            StockRecord rec = new StockRecord();
            rec.setRecordNo("RK" + DateUtils.dateTimeNow("yyyyMMddHHmmss") + String.format("%03d", new java.util.Random().nextInt(1000)));
            rec.setRecordType("0");
            rec.setDrugId(item.getDrugId());
            rec.setQuantity(item.getQuantity());
            rec.setBeforeStock(drug.getStock() - item.getQuantity());
            rec.setAfterStock(drug.getStock());
            rec.setSourceId(purchaseId);
            rec.setOperator(SecurityUtils.getUsername());
            stockRecordMapper.insertStockRecord(rec);
        }
        Purchase upd = new Purchase();
        upd.setPurchaseId(purchaseId);
        upd.setStatus("1");
        upd.setInTime(new Date());
        return purchaseMapper.updatePurchase(upd);
    }

    @Override
    public List<PurchaseItem> selectItemList(Long purchaseId)
    {
        return purchaseItemMapper.selectByPurchaseId(purchaseId);
    }
}

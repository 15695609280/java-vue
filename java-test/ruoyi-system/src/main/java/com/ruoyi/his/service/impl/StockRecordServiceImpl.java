package com.ruoyi.his.service.impl;

import com.ruoyi.his.domain.StockRecord;
import com.ruoyi.his.mapper.StockRecordMapper;
import com.ruoyi.his.service.IStockRecordService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 出入库记录Service业务层处理
 */
@Service
public class StockRecordServiceImpl implements IStockRecordService
{
    @Autowired
    private StockRecordMapper stockRecordMapper;

    @Override
    public StockRecord selectStockRecordByRecordId(Long recordId)
    {
        return stockRecordMapper.selectStockRecordByRecordId(recordId);
    }

    @Override
    public List<StockRecord> selectStockRecordList(StockRecord stockRecord)
    {
        return stockRecordMapper.selectStockRecordList(stockRecord);
    }

    @Override
    public int insertStockRecord(StockRecord stockRecord)
    {
        stockRecord.setCreateTime(com.ruoyi.common.utils.DateUtils.getNowDate());
        int rows = stockRecordMapper.insertStockRecord(stockRecord);
        return rows;
    }

    @Override
    public int updateStockRecord(StockRecord stockRecord)
    {
        stockRecord.setUpdateTime(com.ruoyi.common.utils.DateUtils.getNowDate());
        return stockRecordMapper.updateStockRecord(stockRecord);
    }

    @Override
    public int deleteStockRecordByRecordIds(Long[] recordIds)
    {
        return stockRecordMapper.deleteStockRecordByRecordIds(recordIds);
    }

    @Override
    public int deleteStockRecordByRecordId(Long recordId)
    {
        return stockRecordMapper.deleteStockRecordByRecordId(recordId);
    }
}

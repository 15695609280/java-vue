package com.ruoyi.his.service;

import com.ruoyi.his.domain.StockRecord;
import java.util.List;

/**
 * 出入库记录Service接口
 */
public interface IStockRecordService
{
    public StockRecord selectStockRecordByRecordId(Long recordId);

    public List<StockRecord> selectStockRecordList(StockRecord stockRecord);

    public int insertStockRecord(StockRecord stockRecord);

    public int updateStockRecord(StockRecord stockRecord);

    public int deleteStockRecordByRecordIds(Long[] recordIds);

    public int deleteStockRecordByRecordId(Long recordId);
}

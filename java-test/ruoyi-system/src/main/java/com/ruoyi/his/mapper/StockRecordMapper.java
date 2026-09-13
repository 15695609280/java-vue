package com.ruoyi.his.mapper;

import com.ruoyi.his.domain.StockRecord;
import java.util.List;

/**
 * 出入库记录Mapper接口
 */
public interface StockRecordMapper
{
    public StockRecord selectStockRecordByRecordId(Long recordId);

    public List<StockRecord> selectStockRecordList(StockRecord stockRecord);

    public int insertStockRecord(StockRecord stockRecord);

    public int updateStockRecord(StockRecord stockRecord);

    public int deleteStockRecordByRecordId(Long recordId);

    public int deleteStockRecordByRecordIds(Long[] recordIds);
}

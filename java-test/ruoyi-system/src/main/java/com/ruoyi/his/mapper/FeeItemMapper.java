package com.ruoyi.his.mapper;

import com.ruoyi.his.domain.FeeItem;
import java.util.List;

/**
 * 收费项目Mapper接口
 */
public interface FeeItemMapper
{
    public FeeItem selectFeeItemByItemId(Long itemId);

    public List<FeeItem> selectFeeItemList(FeeItem feeItem);

    public int insertFeeItem(FeeItem feeItem);

    public int updateFeeItem(FeeItem feeItem);

    public int deleteFeeItemByItemId(Long itemId);

    public int deleteFeeItemByItemIds(Long[] itemIds);
}

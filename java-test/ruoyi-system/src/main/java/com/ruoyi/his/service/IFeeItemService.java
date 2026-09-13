package com.ruoyi.his.service;

import com.ruoyi.his.domain.FeeItem;
import java.util.List;

/**
 * 收费项目Service接口
 */
public interface IFeeItemService
{
    public FeeItem selectFeeItemByItemId(Long itemId);

    public List<FeeItem> selectFeeItemList(FeeItem feeItem);

    public int insertFeeItem(FeeItem feeItem);

    public int updateFeeItem(FeeItem feeItem);

    public int deleteFeeItemByItemIds(Long[] itemIds);

    public int deleteFeeItemByItemId(Long itemId);
}

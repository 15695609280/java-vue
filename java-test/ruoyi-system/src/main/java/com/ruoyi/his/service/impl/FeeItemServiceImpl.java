package com.ruoyi.his.service.impl;

import com.ruoyi.his.domain.FeeItem;
import com.ruoyi.his.mapper.FeeItemMapper;
import com.ruoyi.his.service.IFeeItemService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 收费项目Service业务层处理
 */
@Service
public class FeeItemServiceImpl implements IFeeItemService
{
    @Autowired
    private FeeItemMapper feeItemMapper;

    @Override
    public FeeItem selectFeeItemByItemId(Long itemId)
    {
        return feeItemMapper.selectFeeItemByItemId(itemId);
    }

    @Override
    public List<FeeItem> selectFeeItemList(FeeItem feeItem)
    {
        return feeItemMapper.selectFeeItemList(feeItem);
    }

    @Override
    public int insertFeeItem(FeeItem feeItem)
    {
        feeItem.setCreateTime(com.ruoyi.common.utils.DateUtils.getNowDate());
        int rows = feeItemMapper.insertFeeItem(feeItem);
        return rows;
    }

    @Override
    public int updateFeeItem(FeeItem feeItem)
    {
        feeItem.setUpdateTime(com.ruoyi.common.utils.DateUtils.getNowDate());
        return feeItemMapper.updateFeeItem(feeItem);
    }

    @Override
    public int deleteFeeItemByItemIds(Long[] itemIds)
    {
        return feeItemMapper.deleteFeeItemByItemIds(itemIds);
    }

    @Override
    public int deleteFeeItemByItemId(Long itemId)
    {
        return feeItemMapper.deleteFeeItemByItemId(itemId);
    }
}

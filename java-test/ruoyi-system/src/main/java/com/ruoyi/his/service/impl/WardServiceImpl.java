package com.ruoyi.his.service.impl;

import com.ruoyi.his.domain.Ward;
import com.ruoyi.his.mapper.WardMapper;
import com.ruoyi.his.service.IWardService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 病区Service业务层处理
 */
@Service
public class WardServiceImpl implements IWardService
{
    @Autowired
    private WardMapper wardMapper;

    @Override
    public Ward selectWardByWardId(Long wardId)
    {
        return wardMapper.selectWardByWardId(wardId);
    }

    @Override
    public List<Ward> selectWardList(Ward ward)
    {
        return wardMapper.selectWardList(ward);
    }

    @Override
    public int insertWard(Ward ward)
    {
        ward.setCreateTime(com.ruoyi.common.utils.DateUtils.getNowDate());
        int rows = wardMapper.insertWard(ward);
        return rows;
    }

    @Override
    public int updateWard(Ward ward)
    {
        ward.setUpdateTime(com.ruoyi.common.utils.DateUtils.getNowDate());
        return wardMapper.updateWard(ward);
    }

    @Override
    public int deleteWardByWardIds(Long[] wardIds)
    {
        return wardMapper.deleteWardByWardIds(wardIds);
    }

    @Override
    public int deleteWardByWardId(Long wardId)
    {
        return wardMapper.deleteWardByWardId(wardId);
    }
}

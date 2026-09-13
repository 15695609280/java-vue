package com.ruoyi.his.service.impl;

import com.ruoyi.his.domain.Surgery;
import com.ruoyi.his.mapper.SurgeryMapper;
import com.ruoyi.his.service.ISurgeryService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 手术安排Service业务层处理
 */
@Service
public class SurgeryServiceImpl implements ISurgeryService
{
    @Autowired
    private SurgeryMapper surgeryMapper;

    @Override
    public Surgery selectSurgeryBySurgeryId(Long surgeryId)
    {
        return surgeryMapper.selectSurgeryBySurgeryId(surgeryId);
    }

    @Override
    public List<Surgery> selectSurgeryList(Surgery surgery)
    {
        return surgeryMapper.selectSurgeryList(surgery);
    }

    @Override
    public int insertSurgery(Surgery surgery)
    {
        surgery.setCreateTime(com.ruoyi.common.utils.DateUtils.getNowDate());
        int rows = surgeryMapper.insertSurgery(surgery);
        return rows;
    }

    @Override
    public int updateSurgery(Surgery surgery)
    {
        surgery.setUpdateTime(com.ruoyi.common.utils.DateUtils.getNowDate());
        return surgeryMapper.updateSurgery(surgery);
    }

    @Override
    public int deleteSurgeryBySurgeryIds(Long[] surgeryIds)
    {
        return surgeryMapper.deleteSurgeryBySurgeryIds(surgeryIds);
    }

    @Override
    public int deleteSurgeryBySurgeryId(Long surgeryId)
    {
        return surgeryMapper.deleteSurgeryBySurgeryId(surgeryId);
    }
}

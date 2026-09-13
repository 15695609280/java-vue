package com.ruoyi.his.service.impl;

import com.ruoyi.his.domain.Bed;
import com.ruoyi.his.mapper.BedMapper;
import com.ruoyi.his.service.IBedService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 床位Service业务层处理
 */
@Service
public class BedServiceImpl implements IBedService
{
    @Autowired
    private BedMapper bedMapper;

    @Override
    public Bed selectBedByBedId(Long bedId)
    {
        return bedMapper.selectBedByBedId(bedId);
    }

    @Override
    public List<Bed> selectBedList(Bed bed)
    {
        return bedMapper.selectBedList(bed);
    }

    @Override
    public int insertBed(Bed bed)
    {
        bed.setCreateTime(com.ruoyi.common.utils.DateUtils.getNowDate());
        int rows = bedMapper.insertBed(bed);
        return rows;
    }

    @Override
    public int updateBed(Bed bed)
    {
        bed.setUpdateTime(com.ruoyi.common.utils.DateUtils.getNowDate());
        return bedMapper.updateBed(bed);
    }

    @Override
    public int deleteBedByBedIds(Long[] bedIds)
    {
        return bedMapper.deleteBedByBedIds(bedIds);
    }

    @Override
    public int deleteBedByBedId(Long bedId)
    {
        return bedMapper.deleteBedByBedId(bedId);
    }

    @Override
    public List<Bed> selectFreeBeds(Long wardId) { return bedMapper.selectFreeBeds(wardId); }
}

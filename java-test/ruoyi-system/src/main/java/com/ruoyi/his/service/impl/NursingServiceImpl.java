package com.ruoyi.his.service.impl;

import com.ruoyi.his.domain.Nursing;
import com.ruoyi.his.mapper.NursingMapper;
import com.ruoyi.his.service.INursingService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 护理记录Service业务层处理
 */
@Service
public class NursingServiceImpl implements INursingService
{
    @Autowired
    private NursingMapper nursingMapper;

    @Override
    public Nursing selectNursingByRecordId(Long recordId)
    {
        return nursingMapper.selectNursingByRecordId(recordId);
    }

    @Override
    public List<Nursing> selectNursingList(Nursing nursing)
    {
        return nursingMapper.selectNursingList(nursing);
    }

    @Override
    public int insertNursing(Nursing nursing)
    {
        nursing.setCreateTime(com.ruoyi.common.utils.DateUtils.getNowDate());
        int rows = nursingMapper.insertNursing(nursing);
        return rows;
    }

    @Override
    public int updateNursing(Nursing nursing)
    {
        nursing.setUpdateTime(com.ruoyi.common.utils.DateUtils.getNowDate());
        return nursingMapper.updateNursing(nursing);
    }

    @Override
    public int deleteNursingByRecordIds(Long[] recordIds)
    {
        return nursingMapper.deleteNursingByRecordIds(recordIds);
    }

    @Override
    public int deleteNursingByRecordId(Long recordId)
    {
        return nursingMapper.deleteNursingByRecordId(recordId);
    }
}

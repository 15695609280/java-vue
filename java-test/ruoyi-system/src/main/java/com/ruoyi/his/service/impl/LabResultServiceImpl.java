package com.ruoyi.his.service.impl;

import com.ruoyi.his.domain.LabResult;
import com.ruoyi.his.mapper.LabResultMapper;
import com.ruoyi.his.service.ILabResultService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 检验结果Service业务层处理
 */
@Service
public class LabResultServiceImpl implements ILabResultService
{
    @Autowired
    private LabResultMapper labResultMapper;

    @Override
    public LabResult selectLabResultByResultId(Long resultId)
    {
        return labResultMapper.selectLabResultByResultId(resultId);
    }

    @Override
    public List<LabResult> selectLabResultList(LabResult labResult)
    {
        return labResultMapper.selectLabResultList(labResult);
    }

    @Override
    public int insertLabResult(LabResult labResult)
    {
        labResult.setCreateTime(com.ruoyi.common.utils.DateUtils.getNowDate());
        int rows = labResultMapper.insertLabResult(labResult);
        return rows;
    }

    @Override
    public int updateLabResult(LabResult labResult)
    {
        labResult.setUpdateTime(com.ruoyi.common.utils.DateUtils.getNowDate());
        return labResultMapper.updateLabResult(labResult);
    }

    @Override
    public int deleteLabResultByResultIds(Long[] resultIds)
    {
        return labResultMapper.deleteLabResultByResultIds(resultIds);
    }

    @Override
    public int deleteLabResultByResultId(Long resultId)
    {
        return labResultMapper.deleteLabResultByResultId(resultId);
    }

    @Override
    public List<LabResult> selectByTestId(Long testId)
    {
        return labResultMapper.selectByTestId(testId);
    }
}

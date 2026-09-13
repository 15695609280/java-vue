package com.ruoyi.his.service;

import com.ruoyi.his.domain.LabResult;
import java.util.List;

/**
 * 检验结果Service接口
 */
public interface ILabResultService
{
    public LabResult selectLabResultByResultId(Long resultId);

    public List<LabResult> selectLabResultList(LabResult labResult);

    public int insertLabResult(LabResult labResult);

    public int updateLabResult(LabResult labResult);

    public int deleteLabResultByResultIds(Long[] resultIds);

    public int deleteLabResultByResultId(Long resultId);

    /** 按检验单查结果 */
    public List<LabResult> selectByTestId(Long testId);
}

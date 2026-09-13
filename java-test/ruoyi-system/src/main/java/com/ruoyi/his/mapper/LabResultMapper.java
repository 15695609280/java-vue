package com.ruoyi.his.mapper;

import com.ruoyi.his.domain.LabResult;
import java.util.List;

/**
 * 检验结果Mapper接口
 */
public interface LabResultMapper
{
    public LabResult selectLabResultByResultId(Long resultId);

    public List<LabResult> selectLabResultList(LabResult labResult);

    public int insertLabResult(LabResult labResult);

    public int updateLabResult(LabResult labResult);

    public int deleteLabResultByResultId(Long resultId);

    public int deleteLabResultByResultIds(Long[] resultIds);

    /** 按检验单查结果 */
    public List<LabResult> selectByTestId(Long testId);

    /** 按检验单删结果 */
    public int deleteByTestId(Long testId);
}

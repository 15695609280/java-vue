package com.ruoyi.his.service;

import com.ruoyi.his.domain.LabResult;
import com.ruoyi.his.domain.LabTest;
import java.util.List;

/**
 * 检验单Service接口
 */
public interface ILabTestService
{
    public LabTest selectLabTestByTestId(Long testId);

    public List<LabTest> selectLabTestList(LabTest labTest);

    public int insertLabTest(LabTest labTest);

    public int updateLabTest(LabTest labTest);

    public int deleteLabTestByTestIds(Long[] testIds);

    public int deleteLabTestByTestId(Long testId);

    /** 状态流转(1采样 2开始检验 4作废) */
    public int flow(Long testId, String status);

    /** 出报告(含结果明细) */
    public int report(LabTest labTest);

    /** 检验结果明细 */
    public List<LabResult> selectResultList(Long testId);
}

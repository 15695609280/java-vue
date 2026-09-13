package com.ruoyi.his.mapper;

import com.ruoyi.his.domain.LabTest;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * 检验单Mapper接口
 */
public interface LabTestMapper
{
    public LabTest selectLabTestByTestId(Long testId);

    public List<LabTest> selectLabTestList(LabTest labTest);

    public int insertLabTest(LabTest labTest);

    public int updateLabTest(LabTest labTest);

    public int deleteLabTestByTestId(Long testId);

    public int deleteLabTestByTestIds(Long[] testIds);

    /** 修改检验状态 */
    public int updateStatus(@Param("testId") Long testId, @Param("status") String status);
}

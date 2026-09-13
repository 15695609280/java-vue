package com.ruoyi.his.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.his.domain.Charge;
import com.ruoyi.his.domain.LabResult;
import com.ruoyi.his.domain.LabTest;
import com.ruoyi.his.mapper.ChargeMapper;
import com.ruoyi.his.mapper.LabResultMapper;
import com.ruoyi.his.mapper.LabTestMapper;
import com.ruoyi.his.service.ILabTestService;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 检验单Service业务层处理
 */
@Service
public class LabTestServiceImpl implements ILabTestService
{
    @Autowired
    private LabTestMapper labTestMapper;

    @Autowired
    private LabResultMapper labResultMapper;

    @Autowired
    private ChargeMapper chargeMapper;

    @Override
    public LabTest selectLabTestByTestId(Long testId)
    {
        return labTestMapper.selectLabTestByTestId(testId);
    }

    @Override
    public List<LabTest> selectLabTestList(LabTest labTest)
    {
        return labTestMapper.selectLabTestList(labTest);
    }

    @Override
    public int insertLabTest(LabTest labTest)
    {
        labTest.setCreateTime(com.ruoyi.common.utils.DateUtils.getNowDate());
        int rows = labTestMapper.insertLabTest(labTest);
        // 申请检验联动生成待缴检验费账单
        if (labTest.getFee() != null)
        {
            Charge charge = new Charge();
            charge.setChargeNo("JYF" + labTest.getTestNo());
            charge.setPatientId(labTest.getPatientId());
            charge.setSourceType("2");
            charge.setSourceId(labTest.getTestId());
            charge.setItemName("检验费-" + labTest.getTestItem());
            charge.setPrice(labTest.getFee());
            charge.setQuantity(1);
            charge.setAmount(labTest.getFee());
            charge.setChargeStatus("0");
            chargeMapper.insertCharge(charge);
        }
        return rows;
    }

    @Override
    public int updateLabTest(LabTest labTest)
    {
        labTest.setUpdateTime(com.ruoyi.common.utils.DateUtils.getNowDate());
        return labTestMapper.updateLabTest(labTest);
    }

    @Override
    public int deleteLabTestByTestIds(Long[] testIds)
    {
        return labTestMapper.deleteLabTestByTestIds(testIds);
    }

    @Override
    public int deleteLabTestByTestId(Long testId)
    {
        return labTestMapper.deleteLabTestByTestId(testId);
    }

    /** 状态流转: 0已申请 -> 1已采样 -> 2检验中；4作废(仅申请态) */
    @Override
    public int flow(Long testId, String status)
    {
        LabTest test = labTestMapper.selectLabTestByTestId(testId);
        if (test == null) { throw new ServiceException("检验单不存在"); }
        if ("1".equals(status) && !"0".equals(test.getStatus())) { throw new ServiceException("只有已申请才能采样"); }
        if ("2".equals(status) && !"1".equals(test.getStatus())) { throw new ServiceException("只有已采样才能开始检验"); }
        if ("4".equals(status) && !"0".equals(test.getStatus())) { throw new ServiceException("只有已申请才能作废"); }
        return labTestMapper.updateStatus(testId, status);
    }

    /** 出报告：保存明细结果 + 结论，状态置已出报告 */
    @Override
    @Transactional
    public int report(LabTest labTest)
    {
        LabTest test = labTestMapper.selectLabTestByTestId(labTest.getTestId());
        if (test == null) { throw new ServiceException("检验单不存在"); }
        if (!"2".equals(test.getStatus())) { throw new ServiceException("检验中状态才能出报告"); }
        labResultMapper.deleteByTestId(labTest.getTestId());
        if (labTest.getResultList() != null)
        {
            for (LabResult item : labTest.getResultList())
            {
                item.setTestId(labTest.getTestId());
                labResultMapper.insertLabResult(item);
            }
        }
        LabTest upd = new LabTest();
        upd.setTestId(labTest.getTestId());
        upd.setStatus("3");
        upd.setResultSummary(labTest.getResultSummary());
        upd.setReportTime(new Date());
        upd.setReportBy(SecurityUtils.getUsername());
        return labTestMapper.updateLabTest(upd);
    }

    @Override
    public List<LabResult> selectResultList(Long testId)
    {
        return labResultMapper.selectByTestId(testId);
    }
}

package com.ruoyi.his.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.his.domain.Charge;
import com.ruoyi.his.domain.Exam;
import com.ruoyi.his.mapper.ChargeMapper;
import com.ruoyi.his.mapper.ExamMapper;
import com.ruoyi.his.service.IExamService;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 检查单Service业务层处理
 */
@Service
public class ExamServiceImpl implements IExamService
{
    @Autowired
    private ExamMapper examMapper;

    @Autowired
    private ChargeMapper chargeMapper;

    @Override
    public Exam selectExamByExamId(Long examId)
    {
        return examMapper.selectExamByExamId(examId);
    }

    @Override
    public List<Exam> selectExamList(Exam exam)
    {
        return examMapper.selectExamList(exam);
    }

    @Override
    public int insertExam(Exam exam)
    {
        exam.setCreateTime(com.ruoyi.common.utils.DateUtils.getNowDate());
        int rows = examMapper.insertExam(exam);
        // 申请检查联动生成待缴检查费账单
        if (exam.getFee() != null)
        {
            Charge charge = new Charge();
            charge.setChargeNo("JCF" + exam.getExamNo());
            charge.setPatientId(exam.getPatientId());
            charge.setSourceType("3");
            charge.setSourceId(exam.getExamId());
            charge.setItemName("检查费-" + exam.getBodyPart());
            charge.setPrice(exam.getFee());
            charge.setQuantity(1);
            charge.setAmount(exam.getFee());
            charge.setChargeStatus("0");
            chargeMapper.insertCharge(charge);
        }
        return rows;
    }

    @Override
    public int updateExam(Exam exam)
    {
        exam.setUpdateTime(com.ruoyi.common.utils.DateUtils.getNowDate());
        return examMapper.updateExam(exam);
    }

    @Override
    public int deleteExamByExamIds(Long[] examIds)
    {
        return examMapper.deleteExamByExamIds(examIds);
    }

    @Override
    public int deleteExamByExamId(Long examId)
    {
        return examMapper.deleteExamByExamId(examId);
    }

    /** 状态流转: 0已申请 -> 1已检查；3取消(仅申请态) */
    @Override
    public int flow(Long examId, String status)
    {
        Exam exam = examMapper.selectExamByExamId(examId);
        if (exam == null) { throw new ServiceException("检查单不存在"); }
        if ("1".equals(status) && !"0".equals(exam.getStatus())) { throw new ServiceException("只有已申请才能登记检查"); }
        if ("3".equals(status) && !"0".equals(exam.getStatus())) { throw new ServiceException("只有已申请才能取消"); }
        return examMapper.updateStatus(examId, status);
    }

    /** 出报告 */
    @Override
    public int report(Exam exam)
    {
        Exam old = examMapper.selectExamByExamId(exam.getExamId());
        if (old == null) { throw new ServiceException("检查单不存在"); }
        if (!"1".equals(old.getStatus())) { throw new ServiceException("已检查状态才能出报告"); }
        Exam upd = new Exam();
        upd.setExamId(exam.getExamId());
        upd.setStatus("2");
        upd.setFinding(exam.getFinding());
        upd.setConclusion(exam.getConclusion());
        upd.setReportTime(new Date());
        upd.setReportBy(SecurityUtils.getUsername());
        return examMapper.updateExam(upd);
    }
}

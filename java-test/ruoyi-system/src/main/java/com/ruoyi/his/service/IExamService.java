package com.ruoyi.his.service;

import com.ruoyi.his.domain.Exam;
import java.util.List;

/**
 * 检查单Service接口
 */
public interface IExamService
{
    public Exam selectExamByExamId(Long examId);

    public List<Exam> selectExamList(Exam exam);

    public int insertExam(Exam exam);

    public int updateExam(Exam exam);

    public int deleteExamByExamIds(Long[] examIds);

    public int deleteExamByExamId(Long examId);

    /** 状态流转(1已检查 3取消) */
    public int flow(Long examId, String status);

    /** 出报告 */
    public int report(Exam exam);
}

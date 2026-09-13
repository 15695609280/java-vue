package com.ruoyi.his.mapper;

import com.ruoyi.his.domain.Exam;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * 检查单Mapper接口
 */
public interface ExamMapper
{
    public Exam selectExamByExamId(Long examId);

    public List<Exam> selectExamList(Exam exam);

    public int insertExam(Exam exam);

    public int updateExam(Exam exam);

    public int deleteExamByExamId(Long examId);

    public int deleteExamByExamIds(Long[] examIds);

    /** 修改检查状态 */
    public int updateStatus(@Param("examId") Long examId, @Param("status") String status);
}

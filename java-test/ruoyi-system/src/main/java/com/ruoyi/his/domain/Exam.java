package com.ruoyi.his.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import java.util.Date;

/**
 * 检查单对象 his_exam
 */
public class Exam extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 检查ID */
    private Long examId;

    /** 检查单号 */
    private String examNo;

    /** 患者 */
    private Long patientId;

    /** 患者姓名 */
    private String patientName;

    /** 申请医生 */
    private Long doctorId;

    /** 申请医生 */
    private String doctorName;

    /** 检查类型 */
    private String examType;

    /** 检查部位 */
    private String bodyPart;

    /** 检查目的 */
    private String purpose;

    /** 状态 */
    private String status;

    /** 申请时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date applyTime;

    /** 报告时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date reportTime;

    /** 影像所见 */
    private String finding;

    /** 诊断结论 */
    private String conclusion;

    /** 报告人 */
    private String reportBy;

    /** 检查费(申请时联动生成账单用，非持久) */
    private java.math.BigDecimal fee;

    public java.math.BigDecimal getFee() { return fee; }
    public void setFee(java.math.BigDecimal fee) { this.fee = fee; }

    public void setExamId(Long examId) { this.examId = examId; }
    public Long getExamId() { return examId; }

    public void setExamNo(String examNo) { this.examNo = examNo; }
    public String getExamNo() { return examNo; }

    public void setPatientId(Long patientId) { this.patientId = patientId; }
    public Long getPatientId() { return patientId; }

    public void setPatientName(String patientName) { this.patientName = patientName; }
    public String getPatientName() { return patientName; }

    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }
    public Long getDoctorId() { return doctorId; }

    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    public String getDoctorName() { return doctorName; }

    public void setExamType(String examType) { this.examType = examType; }
    public String getExamType() { return examType; }

    public void setBodyPart(String bodyPart) { this.bodyPart = bodyPart; }
    public String getBodyPart() { return bodyPart; }

    public void setPurpose(String purpose) { this.purpose = purpose; }
    public String getPurpose() { return purpose; }

    public void setStatus(String status) { this.status = status; }
    public String getStatus() { return status; }

    public void setApplyTime(Date applyTime) { this.applyTime = applyTime; }
    public Date getApplyTime() { return applyTime; }

    public void setReportTime(Date reportTime) { this.reportTime = reportTime; }
    public Date getReportTime() { return reportTime; }

    public void setFinding(String finding) { this.finding = finding; }
    public String getFinding() { return finding; }

    public void setConclusion(String conclusion) { this.conclusion = conclusion; }
    public String getConclusion() { return conclusion; }

    public void setReportBy(String reportBy) { this.reportBy = reportBy; }
    public String getReportBy() { return reportBy; }
}

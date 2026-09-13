package com.ruoyi.his.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import java.util.Date;
import java.util.List;

/**
 * 检验单对象 his_lab_test
 */
public class LabTest extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 检验ID */
    private Long testId;

    /** 检验单号 */
    private String testNo;

    /** 患者 */
    private Long patientId;

    /** 患者姓名 */
    private String patientName;

    /** 性别 */
    private String gender;

    /** 年龄 */
    private Integer age;

    /** 门诊就诊ID */
    private Long visitId;

    /** 住院ID */
    private Long admId;

    /** 申请医生 */
    private Long doctorId;

    /** 申请医生 */
    private String doctorName;

    /** 检验项目 */
    private String testItem;

    /** 标本类型 */
    private String sampleType;

    /** 状态 */
    private String status;

    /** 申请时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date applyTime;

    /** 采样时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date sampleTime;

    /** 报告时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date reportTime;

    /** 检验结论 */
    private String resultSummary;

    /** 报告人 */
    private String reportBy;

    /** 检验结果明细 */
    private List<LabResult> resultList;

    /** 检验费(申请时联动生成账单用，非持久) */
    private java.math.BigDecimal fee;

    public List<LabResult> getResultList() { return resultList; }
    public void setResultList(List<LabResult> resultList) { this.resultList = resultList; }
    public java.math.BigDecimal getFee() { return fee; }
    public void setFee(java.math.BigDecimal fee) { this.fee = fee; }

    public void setTestId(Long testId) { this.testId = testId; }
    public Long getTestId() { return testId; }

    public void setTestNo(String testNo) { this.testNo = testNo; }
    public String getTestNo() { return testNo; }

    public void setPatientId(Long patientId) { this.patientId = patientId; }
    public Long getPatientId() { return patientId; }

    public void setPatientName(String patientName) { this.patientName = patientName; }
    public String getPatientName() { return patientName; }

    public void setGender(String gender) { this.gender = gender; }
    public String getGender() { return gender; }

    public void setAge(Integer age) { this.age = age; }
    public Integer getAge() { return age; }

    public void setVisitId(Long visitId) { this.visitId = visitId; }
    public Long getVisitId() { return visitId; }

    public void setAdmId(Long admId) { this.admId = admId; }
    public Long getAdmId() { return admId; }

    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }
    public Long getDoctorId() { return doctorId; }

    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    public String getDoctorName() { return doctorName; }

    public void setTestItem(String testItem) { this.testItem = testItem; }
    public String getTestItem() { return testItem; }

    public void setSampleType(String sampleType) { this.sampleType = sampleType; }
    public String getSampleType() { return sampleType; }

    public void setStatus(String status) { this.status = status; }
    public String getStatus() { return status; }

    public void setApplyTime(Date applyTime) { this.applyTime = applyTime; }
    public Date getApplyTime() { return applyTime; }

    public void setSampleTime(Date sampleTime) { this.sampleTime = sampleTime; }
    public Date getSampleTime() { return sampleTime; }

    public void setReportTime(Date reportTime) { this.reportTime = reportTime; }
    public Date getReportTime() { return reportTime; }

    public void setResultSummary(String resultSummary) { this.resultSummary = resultSummary; }
    public String getResultSummary() { return resultSummary; }

    public void setReportBy(String reportBy) { this.reportBy = reportBy; }
    public String getReportBy() { return reportBy; }
}

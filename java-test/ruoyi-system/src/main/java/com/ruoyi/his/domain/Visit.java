package com.ruoyi.his.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 就诊记录对象 his_visit
 */
public class Visit extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 就诊ID */
    private Long visitId;

    /** 就诊编号 */
    @Excel(name = "就诊编号")
    private String visitNo;

    /** 挂号ID */
    private Long regId;

    /** 患者 */
    private Long patientId;

    /** 患者姓名 */
    @Excel(name = "患者姓名")
    private String patientName;

    /** 医生 */
    private Long doctorId;

    /** 医生 */
    @Excel(name = "医生")
    private String doctorName;

    /** 科室 */
    private Long deptId;

    /** 科室 */
    @Excel(name = "科室")
    private String deptName;

    /** 就诊时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "就诊时间", width = 20, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date visitTime;

    /** 主诉 */
    @Excel(name = "主诉")
    private String chiefComplaint;

    /** 现病史 */
    @Excel(name = "现病史")
    private String presentIllness;

    /** 既往史 */
    @Excel(name = "既往史")
    private String pastIllness;

    /** 诊断结果 */
    @Excel(name = "诊断结果")
    private String diagnosis;

    /** 处理意见 */
    @Excel(name = "处理意见")
    private String treatment;

    /** 就诊状态 */
    @Excel(name = "就诊状态", dictType = "his_visit_status")
    private String status;


    public void setVisitId(Long visitId) { this.visitId = visitId; }
    public Long getVisitId() { return visitId; }

    public void setVisitNo(String visitNo) { this.visitNo = visitNo; }
    public String getVisitNo() { return visitNo; }

    public void setRegId(Long regId) { this.regId = regId; }
    public Long getRegId() { return regId; }

    public void setPatientId(Long patientId) { this.patientId = patientId; }
    public Long getPatientId() { return patientId; }

    public void setPatientName(String patientName) { this.patientName = patientName; }
    public String getPatientName() { return patientName; }

    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }
    public Long getDoctorId() { return doctorId; }

    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    public String getDoctorName() { return doctorName; }

    public void setDeptId(Long deptId) { this.deptId = deptId; }
    public Long getDeptId() { return deptId; }

    public void setDeptName(String deptName) { this.deptName = deptName; }
    public String getDeptName() { return deptName; }

    public void setVisitTime(Date visitTime) { this.visitTime = visitTime; }
    public Date getVisitTime() { return visitTime; }

    public void setChiefComplaint(String chiefComplaint) { this.chiefComplaint = chiefComplaint; }
    public String getChiefComplaint() { return chiefComplaint; }

    public void setPresentIllness(String presentIllness) { this.presentIllness = presentIllness; }
    public String getPresentIllness() { return presentIllness; }

    public void setPastIllness(String pastIllness) { this.pastIllness = pastIllness; }
    public String getPastIllness() { return pastIllness; }

    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
    public String getDiagnosis() { return diagnosis; }

    public void setTreatment(String treatment) { this.treatment = treatment; }
    public String getTreatment() { return treatment; }

    public void setStatus(String status) { this.status = status; }
    public String getStatus() { return status; }
}

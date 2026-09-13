package com.ruoyi.his.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import java.util.Date;

/**
 * 医嘱对象 his_medical_order
 */
public class MedicalOrder extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 医嘱ID */
    private Long orderId;

    /** 住院ID */
    private Long admId;

    /** 住院号 */
    private String admNo;

    /** 患者 */
    private Long patientId;

    /** 患者姓名 */
    private String patientName;

    /** 开立医生 */
    private Long doctorId;

    /** 开立医生 */
    private String doctorName;

    /** 医嘱类型 */
    private String orderType;

    /** 医嘱内容 */
    private String content;

    /** 关联药品 */
    private Long drugId;

    /** 关联药品 */
    private String drugName;

    /** 单次剂量 */
    private String dose;

    /** 频次 */
    private String frequency;

    /** 开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /** 停止时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /** 状态 */
    private String orderStatus;

    /** 执行护士 */
    private String execBy;

    /** 执行时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date execTime;


    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public Long getOrderId() { return orderId; }

    public void setAdmId(Long admId) { this.admId = admId; }
    public Long getAdmId() { return admId; }

    public void setAdmNo(String admNo) { this.admNo = admNo; }
    public String getAdmNo() { return admNo; }

    public void setPatientId(Long patientId) { this.patientId = patientId; }
    public Long getPatientId() { return patientId; }

    public void setPatientName(String patientName) { this.patientName = patientName; }
    public String getPatientName() { return patientName; }

    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }
    public Long getDoctorId() { return doctorId; }

    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    public String getDoctorName() { return doctorName; }

    public void setOrderType(String orderType) { this.orderType = orderType; }
    public String getOrderType() { return orderType; }

    public void setContent(String content) { this.content = content; }
    public String getContent() { return content; }

    public void setDrugId(Long drugId) { this.drugId = drugId; }
    public Long getDrugId() { return drugId; }

    public void setDrugName(String drugName) { this.drugName = drugName; }
    public String getDrugName() { return drugName; }

    public void setDose(String dose) { this.dose = dose; }
    public String getDose() { return dose; }

    public void setFrequency(String frequency) { this.frequency = frequency; }
    public String getFrequency() { return frequency; }

    public void setStartTime(Date startTime) { this.startTime = startTime; }
    public Date getStartTime() { return startTime; }

    public void setEndTime(Date endTime) { this.endTime = endTime; }
    public Date getEndTime() { return endTime; }

    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
    public String getOrderStatus() { return orderStatus; }

    public void setExecBy(String execBy) { this.execBy = execBy; }
    public String getExecBy() { return execBy; }

    public void setExecTime(Date execTime) { this.execTime = execTime; }
    public Date getExecTime() { return execTime; }
}

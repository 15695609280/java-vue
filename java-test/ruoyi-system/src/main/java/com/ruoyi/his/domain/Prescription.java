package com.ruoyi.his.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.his.domain.PrescriptionItem;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 处方对象 his_prescription
 */
public class Prescription extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 处方ID */
    private Long rxId;

    /** 处方号 */
    private String rxNo;

    /** 就诊ID */
    private Long visitId;

    /** 患者 */
    private Long patientId;

    /** 患者姓名 */
    private String patientName;

    /** 医生 */
    private Long doctorId;

    /** 医生 */
    private String doctorName;

    /** 处方类型 */
    private String rxType;

    /** 合计金额(元) */
    private BigDecimal totalAmount;

    /** 状态 */
    private String status;

    /** 发药药师 */
    private String dispenseBy;

    /** 发药时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dispenseTime;

    /** 处方明细 */
    private List<PrescriptionItem> itemList;

    public List<PrescriptionItem> getItemList() { return itemList; }
    public void setItemList(List<PrescriptionItem> itemList) { this.itemList = itemList; }

    public void setRxId(Long rxId) { this.rxId = rxId; }
    public Long getRxId() { return rxId; }

    public void setRxNo(String rxNo) { this.rxNo = rxNo; }
    public String getRxNo() { return rxNo; }

    public void setVisitId(Long visitId) { this.visitId = visitId; }
    public Long getVisitId() { return visitId; }

    public void setPatientId(Long patientId) { this.patientId = patientId; }
    public Long getPatientId() { return patientId; }

    public void setPatientName(String patientName) { this.patientName = patientName; }
    public String getPatientName() { return patientName; }

    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }
    public Long getDoctorId() { return doctorId; }

    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    public String getDoctorName() { return doctorName; }

    public void setRxType(String rxType) { this.rxType = rxType; }
    public String getRxType() { return rxType; }

    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public BigDecimal getTotalAmount() { return totalAmount; }

    public void setStatus(String status) { this.status = status; }
    public String getStatus() { return status; }

    public void setDispenseBy(String dispenseBy) { this.dispenseBy = dispenseBy; }
    public String getDispenseBy() { return dispenseBy; }

    public void setDispenseTime(Date dispenseTime) { this.dispenseTime = dispenseTime; }
    public Date getDispenseTime() { return dispenseTime; }
}

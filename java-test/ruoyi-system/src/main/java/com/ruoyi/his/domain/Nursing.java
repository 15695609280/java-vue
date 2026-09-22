package com.ruoyi.his.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 护理记录对象 his_nursing
 */
public class Nursing extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 记录ID */
    private Long recordId;

    /** 住院ID */
    private Long admId;

    /** 患者 */
    private Long patientId;

    /** 患者姓名 */
    @Excel(name = "患者姓名")
    private String patientName;

    /** 护士 */
    @Excel(name = "护士")
    private String nurseName;

    /** 记录时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "记录时间", width = 20, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date recordTime;

    /** 体温(℃) */
    @Excel(name = "体温(℃)")
    private BigDecimal temperature;

    /** 脉搏(次/分) */
    @Excel(name = "脉搏(次/分)")
    private Integer pulse;

    /** 呼吸(次/分) */
    @Excel(name = "呼吸(次/分)")
    private Integer breath;

    /** 收缩压(mmHg) */
    @Excel(name = "收缩压(mmHg)")
    private Integer bpHigh;

    /** 舒张压(mmHg) */
    @Excel(name = "舒张压(mmHg)")
    private Integer bpLow;

    /** 血氧(%) */
    @Excel(name = "血氧(%)")
    private Integer spo2;

    /** 护理内容 */
    @Excel(name = "护理内容")
    private String content;


    public void setRecordId(Long recordId) { this.recordId = recordId; }
    public Long getRecordId() { return recordId; }

    public void setAdmId(Long admId) { this.admId = admId; }
    public Long getAdmId() { return admId; }

    public void setPatientId(Long patientId) { this.patientId = patientId; }
    public Long getPatientId() { return patientId; }

    public void setPatientName(String patientName) { this.patientName = patientName; }
    public String getPatientName() { return patientName; }

    public void setNurseName(String nurseName) { this.nurseName = nurseName; }
    public String getNurseName() { return nurseName; }

    public void setRecordTime(Date recordTime) { this.recordTime = recordTime; }
    public Date getRecordTime() { return recordTime; }

    public void setTemperature(BigDecimal temperature) { this.temperature = temperature; }
    public BigDecimal getTemperature() { return temperature; }

    public void setPulse(Integer pulse) { this.pulse = pulse; }
    public Integer getPulse() { return pulse; }

    public void setBreath(Integer breath) { this.breath = breath; }
    public Integer getBreath() { return breath; }

    public void setBpHigh(Integer bpHigh) { this.bpHigh = bpHigh; }
    public Integer getBpHigh() { return bpHigh; }

    public void setBpLow(Integer bpLow) { this.bpLow = bpLow; }
    public Integer getBpLow() { return bpLow; }

    public void setSpo2(Integer spo2) { this.spo2 = spo2; }
    public Integer getSpo2() { return spo2; }

    public void setContent(String content) { this.content = content; }
    public String getContent() { return content; }
}

package com.ruoyi.his.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 挂号对象 his_registration
 */
public class Registration extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 挂号ID */
    private Long regId;

    /** 挂号单号 */
    @Excel(name = "挂号单号")
    private String regNo;

    /** 患者 */
    private Long patientId;

    /** 患者姓名 */
    @Excel(name = "患者姓名")
    private String patientName;

    /** 就诊卡号 */
    @Excel(name = "就诊卡号")
    private String patientNo;

    /** 性别 */
    @Excel(name = "性别", dictType = "sys_user_sex")
    private String gender;

    /** 年龄 */
    @Excel(name = "年龄")
    private Integer age;

    /** 科室 */
    private Long deptId;

    /** 科室 */
    @Excel(name = "科室")
    private String deptName;

    /** 医生 */
    private Long doctorId;

    /** 医生 */
    @Excel(name = "医生")
    private String doctorName;

    /** 排班ID */
    private Long scheduleId;

    /** 就诊日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "就诊日期", width = 15, dateFormat = "yyyy-MM-dd")
    private Date regDate;

    /** 时段 */
    @Excel(name = "时段", dictType = "his_time_slot")
    private String timeSlot;

    /** 排队号 */
    @Excel(name = "排队号")
    private Integer queueNo;

    /** 挂号费(元) */
    @Excel(name = "挂号费(元)")
    private BigDecimal regFee;

    /** 状态 */
    @Excel(name = "就诊状态", dictType = "his_reg_status")
    private String visitStatus;

    /** 缴费状态 */
    @Excel(name = "缴费状态", readConverterExp = "0=未缴,1=已缴")
    private String payStatus;


    public void setRegId(Long regId) { this.regId = regId; }
    public Long getRegId() { return regId; }

    public void setRegNo(String regNo) { this.regNo = regNo; }
    public String getRegNo() { return regNo; }

    public void setPatientId(Long patientId) { this.patientId = patientId; }
    public Long getPatientId() { return patientId; }

    public void setPatientName(String patientName) { this.patientName = patientName; }
    public String getPatientName() { return patientName; }

    public void setPatientNo(String patientNo) { this.patientNo = patientNo; }
    public String getPatientNo() { return patientNo; }

    public void setGender(String gender) { this.gender = gender; }
    public String getGender() { return gender; }

    public void setAge(Integer age) { this.age = age; }
    public Integer getAge() { return age; }

    public void setDeptId(Long deptId) { this.deptId = deptId; }
    public Long getDeptId() { return deptId; }

    public void setDeptName(String deptName) { this.deptName = deptName; }
    public String getDeptName() { return deptName; }

    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }
    public Long getDoctorId() { return doctorId; }

    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    public String getDoctorName() { return doctorName; }

    public void setScheduleId(Long scheduleId) { this.scheduleId = scheduleId; }
    public Long getScheduleId() { return scheduleId; }

    public void setRegDate(Date regDate) { this.regDate = regDate; }
    public Date getRegDate() { return regDate; }

    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }
    public String getTimeSlot() { return timeSlot; }

    public void setQueueNo(Integer queueNo) { this.queueNo = queueNo; }
    public Integer getQueueNo() { return queueNo; }

    public void setRegFee(BigDecimal regFee) { this.regFee = regFee; }
    public BigDecimal getRegFee() { return regFee; }

    public void setVisitStatus(String visitStatus) { this.visitStatus = visitStatus; }
    public String getVisitStatus() { return visitStatus; }

    public void setPayStatus(String payStatus) { this.payStatus = payStatus; }
    public String getPayStatus() { return payStatus; }
}

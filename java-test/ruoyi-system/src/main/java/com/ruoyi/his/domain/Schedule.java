package com.ruoyi.his.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import java.util.Date;

/**
 * 排班对象 his_schedule
 */
public class Schedule extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 排班ID */
    private Long scheduleId;

    /** 科室 */
    private Long deptId;

    /** 科室 */
    private String deptName;

    /** 医生 */
    private Long doctorId;

    /** 医生 */
    private String doctorName;

    /** 职称 */
    private String doctorTitle;

    /** 出诊日期 */
    @Excel(name = "出诊日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date workDate;

    /** 时段 */
    @Excel(name = "时段")
    private String timeSlot;

    /** 总号源 */
    private Integer quota;

    /** 剩余号源 */
    private Integer remainQuota;

    /** 状态 */
    private String status;


    public void setScheduleId(Long scheduleId) { this.scheduleId = scheduleId; }
    public Long getScheduleId() { return scheduleId; }

    public void setDeptId(Long deptId) { this.deptId = deptId; }
    public Long getDeptId() { return deptId; }

    public void setDeptName(String deptName) { this.deptName = deptName; }
    public String getDeptName() { return deptName; }

    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }
    public Long getDoctorId() { return doctorId; }

    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    public String getDoctorName() { return doctorName; }

    public void setDoctorTitle(String doctorTitle) { this.doctorTitle = doctorTitle; }
    public String getDoctorTitle() { return doctorTitle; }

    public void setWorkDate(Date workDate) { this.workDate = workDate; }
    public Date getWorkDate() { return workDate; }

    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }
    public String getTimeSlot() { return timeSlot; }

    public void setQuota(Integer quota) { this.quota = quota; }
    public Integer getQuota() { return quota; }

    public void setRemainQuota(Integer remainQuota) { this.remainQuota = remainQuota; }
    public Integer getRemainQuota() { return remainQuota; }

    public void setStatus(String status) { this.status = status; }
    public String getStatus() { return status; }
}

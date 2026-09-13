package com.ruoyi.his.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import java.util.Date;

/**
 * 手术安排对象 his_surgery
 */
public class Surgery extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 手术ID */
    private Long surgeryId;

    /** 手术编号 */
    private String surgeryNo;

    /** 手术名称 */
    private String surgeryName;

    /** 患者 */
    private Long patientId;

    /** 患者姓名 */
    private String patientName;

    /** 科室 */
    private Long deptId;

    /** 科室 */
    private String deptName;

    /** 主刀医生 */
    private Long surgeonId;

    /** 主刀医生 */
    private String surgeonName;

    /** 麻醉方式 */
    private String anesthesia;

    /** 手术间 */
    private String roomNo;

    /** 手术级别 */
    private String level;

    /** 计划时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date planTime;

    /** 开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /** 结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /** 状态 */
    private String status;


    public void setSurgeryId(Long surgeryId) { this.surgeryId = surgeryId; }
    public Long getSurgeryId() { return surgeryId; }

    public void setSurgeryNo(String surgeryNo) { this.surgeryNo = surgeryNo; }
    public String getSurgeryNo() { return surgeryNo; }

    public void setSurgeryName(String surgeryName) { this.surgeryName = surgeryName; }
    public String getSurgeryName() { return surgeryName; }

    public void setPatientId(Long patientId) { this.patientId = patientId; }
    public Long getPatientId() { return patientId; }

    public void setPatientName(String patientName) { this.patientName = patientName; }
    public String getPatientName() { return patientName; }

    public void setDeptId(Long deptId) { this.deptId = deptId; }
    public Long getDeptId() { return deptId; }

    public void setDeptName(String deptName) { this.deptName = deptName; }
    public String getDeptName() { return deptName; }

    public void setSurgeonId(Long surgeonId) { this.surgeonId = surgeonId; }
    public Long getSurgeonId() { return surgeonId; }

    public void setSurgeonName(String surgeonName) { this.surgeonName = surgeonName; }
    public String getSurgeonName() { return surgeonName; }

    public void setAnesthesia(String anesthesia) { this.anesthesia = anesthesia; }
    public String getAnesthesia() { return anesthesia; }

    public void setRoomNo(String roomNo) { this.roomNo = roomNo; }
    public String getRoomNo() { return roomNo; }

    public void setLevel(String level) { this.level = level; }
    public String getLevel() { return level; }

    public void setPlanTime(Date planTime) { this.planTime = planTime; }
    public Date getPlanTime() { return planTime; }

    public void setStartTime(Date startTime) { this.startTime = startTime; }
    public Date getStartTime() { return startTime; }

    public void setEndTime(Date endTime) { this.endTime = endTime; }
    public Date getEndTime() { return endTime; }

    public void setStatus(String status) { this.status = status; }
    public String getStatus() { return status; }
}

package com.ruoyi.his.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 住院登记对象 his_admission
 */
public class Admission extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 住院ID */
    private Long admId;

    /** 住院号 */
    private String admNo;

    /** 患者 */
    private Long patientId;

    /** 患者姓名 */
    private String patientName;

    /** 性别 */
    private String gender;

    /** 年龄 */
    private Integer age;

    /** 科室 */
    private Long deptId;

    /** 科室 */
    private String deptName;

    /** 病区 */
    private Long wardId;

    /** 病区 */
    private String wardName;

    /** 床位 */
    private Long bedId;

    /** 床号 */
    private String bedNo;

    /** 主治医生 */
    private Long doctorId;

    /** 主治医生 */
    private String doctorName;

    /** 入院时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date inDate;

    /** 出院时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date outDate;

    /** 预缴押金(元) */
    private BigDecimal deposit;

    /** 住院状态 */
    private String admStatus;

    /** 入院诊断 */
    private String diagnosisIn;


    public void setAdmId(Long admId) { this.admId = admId; }
    public Long getAdmId() { return admId; }

    public void setAdmNo(String admNo) { this.admNo = admNo; }
    public String getAdmNo() { return admNo; }

    public void setPatientId(Long patientId) { this.patientId = patientId; }
    public Long getPatientId() { return patientId; }

    public void setPatientName(String patientName) { this.patientName = patientName; }
    public String getPatientName() { return patientName; }

    public void setGender(String gender) { this.gender = gender; }
    public String getGender() { return gender; }

    public void setAge(Integer age) { this.age = age; }
    public Integer getAge() { return age; }

    public void setDeptId(Long deptId) { this.deptId = deptId; }
    public Long getDeptId() { return deptId; }

    public void setDeptName(String deptName) { this.deptName = deptName; }
    public String getDeptName() { return deptName; }

    public void setWardId(Long wardId) { this.wardId = wardId; }
    public Long getWardId() { return wardId; }

    public void setWardName(String wardName) { this.wardName = wardName; }
    public String getWardName() { return wardName; }

    public void setBedId(Long bedId) { this.bedId = bedId; }
    public Long getBedId() { return bedId; }

    public void setBedNo(String bedNo) { this.bedNo = bedNo; }
    public String getBedNo() { return bedNo; }

    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }
    public Long getDoctorId() { return doctorId; }

    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    public String getDoctorName() { return doctorName; }

    public void setInDate(Date inDate) { this.inDate = inDate; }
    public Date getInDate() { return inDate; }

    public void setOutDate(Date outDate) { this.outDate = outDate; }
    public Date getOutDate() { return outDate; }

    public void setDeposit(BigDecimal deposit) { this.deposit = deposit; }
    public BigDecimal getDeposit() { return deposit; }

    public void setAdmStatus(String admStatus) { this.admStatus = admStatus; }
    public String getAdmStatus() { return admStatus; }

    public void setDiagnosisIn(String diagnosisIn) { this.diagnosisIn = diagnosisIn; }
    public String getDiagnosisIn() { return diagnosisIn; }
}

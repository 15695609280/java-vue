package com.ruoyi.his.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import java.math.BigDecimal;

/**
 * 医生对象 his_doctor
 */
public class Doctor extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 医生ID */
    private Long doctorId;

    /** 医生姓名 */
    @Excel(name = "医生姓名")
    private String doctorName;

    /** 所属科室 */
    private Long deptId;

    /** 所属科室 */
    private String deptName;

    /** 职称 */
    @Excel(name = "职称")
    private String title;

    /** 性别 */
    private String gender;

    /** 联系电话 */
    private String phone;

    /** 擅长领域 */
    private String specialty;

    /** 挂号费(元) */
    private BigDecimal regFee;

    /** 个人简介 */
    private String intro;

    /** 状态 */
    private String status;


    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }
    public Long getDoctorId() { return doctorId; }

    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    public String getDoctorName() { return doctorName; }

    public void setDeptId(Long deptId) { this.deptId = deptId; }
    public Long getDeptId() { return deptId; }

    public void setDeptName(String deptName) { this.deptName = deptName; }
    public String getDeptName() { return deptName; }

    public void setTitle(String title) { this.title = title; }
    public String getTitle() { return title; }

    public void setGender(String gender) { this.gender = gender; }
    public String getGender() { return gender; }

    public void setPhone(String phone) { this.phone = phone; }
    public String getPhone() { return phone; }

    public void setSpecialty(String specialty) { this.specialty = specialty; }
    public String getSpecialty() { return specialty; }

    public void setRegFee(BigDecimal regFee) { this.regFee = regFee; }
    public BigDecimal getRegFee() { return regFee; }

    public void setIntro(String intro) { this.intro = intro; }
    public String getIntro() { return intro; }

    public void setStatus(String status) { this.status = status; }
    public String getStatus() { return status; }
}

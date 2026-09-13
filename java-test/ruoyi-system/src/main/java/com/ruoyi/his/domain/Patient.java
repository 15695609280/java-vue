package com.ruoyi.his.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import java.util.Date;

/**
 * 患者对象 his_patient
 */
public class Patient extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 患者ID */
    private Long patientId;

    /** 就诊卡号 */
    @Excel(name = "就诊卡号")
    private String patientNo;

    /** 患者姓名 */
    @Excel(name = "患者姓名")
    private String patientName;

    /** 性别 */
    private String gender;

    /** 出生日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthDate;

    /** 年龄 */
    private Integer age;

    /** 身份证号 */
    private String idCard;

    /** 联系电话 */
    private String phone;

    /** 住址 */
    private String address;

    /** 血型 */
    private String bloodType;

    /** 医保类型 */
    private String insuranceType;

    /** 过敏史 */
    private String allergy;

    /** 状态 */
    private String status;


    public void setPatientId(Long patientId) { this.patientId = patientId; }
    public Long getPatientId() { return patientId; }

    public void setPatientNo(String patientNo) { this.patientNo = patientNo; }
    public String getPatientNo() { return patientNo; }

    public void setPatientName(String patientName) { this.patientName = patientName; }
    public String getPatientName() { return patientName; }

    public void setGender(String gender) { this.gender = gender; }
    public String getGender() { return gender; }

    public void setBirthDate(Date birthDate) { this.birthDate = birthDate; }
    public Date getBirthDate() { return birthDate; }

    public void setAge(Integer age) { this.age = age; }
    public Integer getAge() { return age; }

    public void setIdCard(String idCard) { this.idCard = idCard; }
    public String getIdCard() { return idCard; }

    public void setPhone(String phone) { this.phone = phone; }
    public String getPhone() { return phone; }

    public void setAddress(String address) { this.address = address; }
    public String getAddress() { return address; }

    public void setBloodType(String bloodType) { this.bloodType = bloodType; }
    public String getBloodType() { return bloodType; }

    public void setInsuranceType(String insuranceType) { this.insuranceType = insuranceType; }
    public String getInsuranceType() { return insuranceType; }

    public void setAllergy(String allergy) { this.allergy = allergy; }
    public String getAllergy() { return allergy; }

    public void setStatus(String status) { this.status = status; }
    public String getStatus() { return status; }
}

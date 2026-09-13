package com.ruoyi.his.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import java.math.BigDecimal;

/**
 * 床位对象 his_bed
 */
public class Bed extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 床位ID */
    private Long bedId;

    /** 所属病区 */
    private Long wardId;

    /** 所属病区 */
    private String wardName;

    /** 床号 */
    private String bedNo;

    /** 床位类型 */
    private String bedType;

    /** 每日价格(元) */
    private BigDecimal pricePerDay;

    /** 床位状态 */
    private String status;

    /** 在住患者 */
    private String patientName;


    public void setBedId(Long bedId) { this.bedId = bedId; }
    public Long getBedId() { return bedId; }

    public void setWardId(Long wardId) { this.wardId = wardId; }
    public Long getWardId() { return wardId; }

    public void setWardName(String wardName) { this.wardName = wardName; }
    public String getWardName() { return wardName; }

    public void setBedNo(String bedNo) { this.bedNo = bedNo; }
    public String getBedNo() { return bedNo; }

    public void setBedType(String bedType) { this.bedType = bedType; }
    public String getBedType() { return bedType; }

    public void setPricePerDay(BigDecimal pricePerDay) { this.pricePerDay = pricePerDay; }
    public BigDecimal getPricePerDay() { return pricePerDay; }

    public void setStatus(String status) { this.status = status; }
    public String getStatus() { return status; }

    public void setPatientName(String patientName) { this.patientName = patientName; }
    public String getPatientName() { return patientName; }
}

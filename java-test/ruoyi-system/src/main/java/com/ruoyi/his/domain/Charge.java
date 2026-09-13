package com.ruoyi.his.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 费用单对象 his_charge
 */
public class Charge extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 费用ID */
    private Long chargeId;

    /** 单据号 */
    private String chargeNo;

    /** 患者 */
    private Long patientId;

    /** 患者姓名 */
    private String patientName;

    /** 就诊卡号 */
    private String patientNo;

    /** 费用类型 */
    private String sourceType;

    /** 来源单据ID */
    private Long sourceId;

    /** 项目名称 */
    private String itemName;

    /** 单价(元) */
    private BigDecimal price;

    /** 数量 */
    private Integer quantity;

    /** 金额(元) */
    private BigDecimal amount;

    /** 收费状态 */
    private String chargeStatus;

    /** 支付方式 */
    private String payType;

    /** 结算时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date settleTime;

    /** 收费员 */
    private String operator;

    /** 批量结算IDS(结算接口用) */
    private Long[] chargeIds;

    public Long[] getChargeIds() { return chargeIds; }
    public void setChargeIds(Long[] chargeIds) { this.chargeIds = chargeIds; }

    public void setChargeId(Long chargeId) { this.chargeId = chargeId; }
    public Long getChargeId() { return chargeId; }

    public void setChargeNo(String chargeNo) { this.chargeNo = chargeNo; }
    public String getChargeNo() { return chargeNo; }

    public void setPatientId(Long patientId) { this.patientId = patientId; }
    public Long getPatientId() { return patientId; }

    public void setPatientName(String patientName) { this.patientName = patientName; }
    public String getPatientName() { return patientName; }

    public void setPatientNo(String patientNo) { this.patientNo = patientNo; }
    public String getPatientNo() { return patientNo; }

    public void setSourceType(String sourceType) { this.sourceType = sourceType; }
    public String getSourceType() { return sourceType; }

    public void setSourceId(Long sourceId) { this.sourceId = sourceId; }
    public Long getSourceId() { return sourceId; }

    public void setItemName(String itemName) { this.itemName = itemName; }
    public String getItemName() { return itemName; }

    public void setPrice(BigDecimal price) { this.price = price; }
    public BigDecimal getPrice() { return price; }

    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public Integer getQuantity() { return quantity; }

    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public BigDecimal getAmount() { return amount; }

    public void setChargeStatus(String chargeStatus) { this.chargeStatus = chargeStatus; }
    public String getChargeStatus() { return chargeStatus; }

    public void setPayType(String payType) { this.payType = payType; }
    public String getPayType() { return payType; }

    public void setSettleTime(Date settleTime) { this.settleTime = settleTime; }
    public Date getSettleTime() { return settleTime; }

    public void setOperator(String operator) { this.operator = operator; }
    public String getOperator() { return operator; }
}

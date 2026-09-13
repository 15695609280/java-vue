package com.ruoyi.his.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import java.math.BigDecimal;

/**
 * 处方明细对象 his_prescription_item
 */
public class PrescriptionItem extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 明细ID */
    private Long itemId;

    /** 处方ID */
    private Long rxId;

    /** 药品 */
    private Long drugId;

    /** 药品名称 */
    private String drugName;

    /** 规格 */
    private String specification;

    /** 单位 */
    private String unit;

    /** 单价(元) */
    private BigDecimal price;

    /** 数量 */
    private Integer quantity;

    /** 金额(元) */
    private BigDecimal amount;

    /** 用法用量 */
    private String usageDose;

    /** 频次 */
    private String frequency;

    /** 天数 */
    private Integer days;


    public void setItemId(Long itemId) { this.itemId = itemId; }
    public Long getItemId() { return itemId; }

    public void setRxId(Long rxId) { this.rxId = rxId; }
    public Long getRxId() { return rxId; }

    public void setDrugId(Long drugId) { this.drugId = drugId; }
    public Long getDrugId() { return drugId; }

    public void setDrugName(String drugName) { this.drugName = drugName; }
    public String getDrugName() { return drugName; }

    public void setSpecification(String specification) { this.specification = specification; }
    public String getSpecification() { return specification; }

    public void setUnit(String unit) { this.unit = unit; }
    public String getUnit() { return unit; }

    public void setPrice(BigDecimal price) { this.price = price; }
    public BigDecimal getPrice() { return price; }

    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public Integer getQuantity() { return quantity; }

    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public BigDecimal getAmount() { return amount; }

    public void setUsageDose(String usageDose) { this.usageDose = usageDose; }
    public String getUsageDose() { return usageDose; }

    public void setFrequency(String frequency) { this.frequency = frequency; }
    public String getFrequency() { return frequency; }

    public void setDays(Integer days) { this.days = days; }
    public Integer getDays() { return days; }
}

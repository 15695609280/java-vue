package com.ruoyi.his.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import java.math.BigDecimal;

/**
 * 采购明细对象 his_purchase_item
 */
public class PurchaseItem extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 明细ID */
    private Long itemId;

    /** 采购ID */
    private Long purchaseId;

    /** 药品 */
    private Long drugId;

    /** 药品名称 */
    private String drugName;

    /** 规格 */
    private String specification;

    /** 数量 */
    private Integer quantity;

    /** 采购单价(元) */
    private BigDecimal price;

    /** 金额(元) */
    private BigDecimal amount;


    public void setItemId(Long itemId) { this.itemId = itemId; }
    public Long getItemId() { return itemId; }

    public void setPurchaseId(Long purchaseId) { this.purchaseId = purchaseId; }
    public Long getPurchaseId() { return purchaseId; }

    public void setDrugId(Long drugId) { this.drugId = drugId; }
    public Long getDrugId() { return drugId; }

    public void setDrugName(String drugName) { this.drugName = drugName; }
    public String getDrugName() { return drugName; }

    public void setSpecification(String specification) { this.specification = specification; }
    public String getSpecification() { return specification; }

    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public Integer getQuantity() { return quantity; }

    public void setPrice(BigDecimal price) { this.price = price; }
    public BigDecimal getPrice() { return price; }

    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public BigDecimal getAmount() { return amount; }
}

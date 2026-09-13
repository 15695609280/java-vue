package com.ruoyi.his.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import java.math.BigDecimal;

/**
 * 收费项目对象 his_fee_item
 */
public class FeeItem extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 项目ID */
    private Long itemId;

    /** 项目名称 */
    @Excel(name = "项目名称")
    private String itemName;

    /** 项目类别 */
    @Excel(name = "项目类别")
    private String category;

    /** 单价(元) */
    @Excel(name = "单价(元)")
    private BigDecimal price;

    /** 单位 */
    private String unit;

    /** 状态 */
    private String status;


    public void setItemId(Long itemId) { this.itemId = itemId; }
    public Long getItemId() { return itemId; }

    public void setItemName(String itemName) { this.itemName = itemName; }
    public String getItemName() { return itemName; }

    public void setCategory(String category) { this.category = category; }
    public String getCategory() { return category; }

    public void setPrice(BigDecimal price) { this.price = price; }
    public BigDecimal getPrice() { return price; }

    public void setUnit(String unit) { this.unit = unit; }
    public String getUnit() { return unit; }

    public void setStatus(String status) { this.status = status; }
    public String getStatus() { return status; }
}

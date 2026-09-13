package com.ruoyi.his.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import java.math.BigDecimal;

/**
 * 药品对象 his_drug
 */
public class Drug extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 药品ID */
    private Long drugId;

    /** 药品编码 */
    private String drugCode;

    /** 药品名称 */
    @Excel(name = "药品名称")
    private String drugName;

    /** 药品分类 */
    @Excel(name = "药品分类")
    private String category;

    /** 规格 */
    private String specification;

    /** 单位 */
    private String unit;

    /** 单价(元) */
    @Excel(name = "单价(元)")
    private BigDecimal price;

    /** 库存 */
    private Integer stock;

    /** 预警库存 */
    private Integer stockWarn;

    /** 生产厂家 */
    private String manufacturer;

    /** 批准文号 */
    private String approvalNo;

    /** 处方药 */
    private String isPrescription;

    /** 状态 */
    private String status;


    public void setDrugId(Long drugId) { this.drugId = drugId; }
    public Long getDrugId() { return drugId; }

    public void setDrugCode(String drugCode) { this.drugCode = drugCode; }
    public String getDrugCode() { return drugCode; }

    public void setDrugName(String drugName) { this.drugName = drugName; }
    public String getDrugName() { return drugName; }

    public void setCategory(String category) { this.category = category; }
    public String getCategory() { return category; }

    public void setSpecification(String specification) { this.specification = specification; }
    public String getSpecification() { return specification; }

    public void setUnit(String unit) { this.unit = unit; }
    public String getUnit() { return unit; }

    public void setPrice(BigDecimal price) { this.price = price; }
    public BigDecimal getPrice() { return price; }

    public void setStock(Integer stock) { this.stock = stock; }
    public Integer getStock() { return stock; }

    public void setStockWarn(Integer stockWarn) { this.stockWarn = stockWarn; }
    public Integer getStockWarn() { return stockWarn; }

    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }
    public String getManufacturer() { return manufacturer; }

    public void setApprovalNo(String approvalNo) { this.approvalNo = approvalNo; }
    public String getApprovalNo() { return approvalNo; }

    public void setIsPrescription(String isPrescription) { this.isPrescription = isPrescription; }
    public String getIsPrescription() { return isPrescription; }

    public void setStatus(String status) { this.status = status; }
    public String getStatus() { return status; }
}

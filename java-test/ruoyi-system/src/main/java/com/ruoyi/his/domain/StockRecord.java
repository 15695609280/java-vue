package com.ruoyi.his.domain;

import java.util.Date;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 出入库记录对象 his_stock_record
 */
public class StockRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 记录ID */
    private Long recordId;

    /** 单据号 */
    @Excel(name = "单据号")
    private String recordNo;

    /** 变动类型 */
    @Excel(name = "变动类型", dictType = "his_stock_type")
    private String recordType;

    /** 药品 */
    private Long drugId;

    /** 药品名称 */
    @Excel(name = "药品")
    private String drugName;

    /** 规格 */
    @Excel(name = "规格")
    private String specification;

    /** 变动数量 */
    @Excel(name = "变动数量")
    private Integer quantity;

    /** 变动前库存 */
    @Excel(name = "变动前库存")
    private Integer beforeStock;

    /** 变动后库存 */
    @Excel(name = "变动后库存")
    private Integer afterStock;

    /** 来源单据ID */
    private Long sourceId;

    /** 经办人 */
    @Excel(name = "经办人")
    private String operator;

    /** 时间（映射父类 createTime，仅供导出） */
    @Excel(name = "时间", width = 20, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 备注（映射父类 remark，仅供导出） */
    @Excel(name = "备注")
    private String remark;


    public void setRecordId(Long recordId) { this.recordId = recordId; }
    public Long getRecordId() { return recordId; }

    public void setRecordNo(String recordNo) { this.recordNo = recordNo; }
    public String getRecordNo() { return recordNo; }

    public void setRecordType(String recordType) { this.recordType = recordType; }
    public String getRecordType() { return recordType; }

    public void setDrugId(Long drugId) { this.drugId = drugId; }
    public Long getDrugId() { return drugId; }

    public void setDrugName(String drugName) { this.drugName = drugName; }
    public String getDrugName() { return drugName; }

    public void setSpecification(String specification) { this.specification = specification; }
    public String getSpecification() { return specification; }

    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public Integer getQuantity() { return quantity; }

    public void setBeforeStock(Integer beforeStock) { this.beforeStock = beforeStock; }
    public Integer getBeforeStock() { return beforeStock; }

    public void setAfterStock(Integer afterStock) { this.afterStock = afterStock; }
    public Integer getAfterStock() { return afterStock; }

    public void setSourceId(Long sourceId) { this.sourceId = sourceId; }
    public Long getSourceId() { return sourceId; }

    public void setOperator(String operator) { this.operator = operator; }
    public String getOperator() { return operator; }
}

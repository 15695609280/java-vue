package com.ruoyi.his.domain;

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
    private String recordNo;

    /** 变动类型 */
    private String recordType;

    /** 药品 */
    private Long drugId;

    /** 药品名称 */
    private String drugName;

    /** 规格 */
    private String specification;

    /** 变动数量 */
    private Integer quantity;

    /** 变动前库存 */
    private Integer beforeStock;

    /** 变动后库存 */
    private Integer afterStock;

    /** 来源单据ID */
    private Long sourceId;

    /** 经办人 */
    private String operator;


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

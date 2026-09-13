package com.ruoyi.his.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 采购单对象 his_purchase
 */
public class Purchase extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 采购ID */
    private Long purchaseId;

    /** 采购单号 */
    private String purchaseNo;

    /** 供应商 */
    private Long supplierId;

    /** 供应商 */
    private String supplierName;

    /** 合计金额(元) */
    private BigDecimal totalAmount;

    /** 状态 */
    private String status;

    /** 入库时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date inTime;

    /** 采购明细 */
    private List<PurchaseItem> itemList;

    public List<PurchaseItem> getItemList() { return itemList; }
    public void setItemList(List<PurchaseItem> itemList) { this.itemList = itemList; }

    public void setPurchaseId(Long purchaseId) { this.purchaseId = purchaseId; }
    public Long getPurchaseId() { return purchaseId; }

    public void setPurchaseNo(String purchaseNo) { this.purchaseNo = purchaseNo; }
    public String getPurchaseNo() { return purchaseNo; }

    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
    public Long getSupplierId() { return supplierId; }

    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public String getSupplierName() { return supplierName; }

    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public BigDecimal getTotalAmount() { return totalAmount; }

    public void setStatus(String status) { this.status = status; }
    public String getStatus() { return status; }

    public void setInTime(Date inTime) { this.inTime = inTime; }
    public Date getInTime() { return inTime; }
}

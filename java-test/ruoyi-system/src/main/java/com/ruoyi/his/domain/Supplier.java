package com.ruoyi.his.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 供应商对象 his_supplier
 */
public class Supplier extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 供应商ID */
    private Long supplierId;

    /** 供应商名称 */
    @Excel(name = "供应商名称")
    private String supplierName;

    /** 联系人 */
    private String contact;

    /** 联系电话 */
    private String phone;

    /** 地址 */
    private String address;

    /** 状态 */
    private String status;


    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
    public Long getSupplierId() { return supplierId; }

    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public String getSupplierName() { return supplierName; }

    public void setContact(String contact) { this.contact = contact; }
    public String getContact() { return contact; }

    public void setPhone(String phone) { this.phone = phone; }
    public String getPhone() { return phone; }

    public void setAddress(String address) { this.address = address; }
    public String getAddress() { return address; }

    public void setStatus(String status) { this.status = status; }
    public String getStatus() { return status; }
}

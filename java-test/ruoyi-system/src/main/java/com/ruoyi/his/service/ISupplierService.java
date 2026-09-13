package com.ruoyi.his.service;

import com.ruoyi.his.domain.Supplier;
import java.util.List;

/**
 * 供应商Service接口
 */
public interface ISupplierService
{
    public Supplier selectSupplierBySupplierId(Long supplierId);

    public List<Supplier> selectSupplierList(Supplier supplier);

    public int insertSupplier(Supplier supplier);

    public int updateSupplier(Supplier supplier);

    public int deleteSupplierBySupplierIds(Long[] supplierIds);

    public int deleteSupplierBySupplierId(Long supplierId);
}

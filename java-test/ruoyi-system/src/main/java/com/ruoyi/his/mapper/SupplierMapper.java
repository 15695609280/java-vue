package com.ruoyi.his.mapper;

import com.ruoyi.his.domain.Supplier;
import java.util.List;

/**
 * 供应商Mapper接口
 */
public interface SupplierMapper
{
    public Supplier selectSupplierBySupplierId(Long supplierId);

    public List<Supplier> selectSupplierList(Supplier supplier);

    public int insertSupplier(Supplier supplier);

    public int updateSupplier(Supplier supplier);

    public int deleteSupplierBySupplierId(Long supplierId);

    public int deleteSupplierBySupplierIds(Long[] supplierIds);
}

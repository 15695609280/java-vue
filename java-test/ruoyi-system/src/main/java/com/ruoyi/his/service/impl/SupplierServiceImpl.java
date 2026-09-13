package com.ruoyi.his.service.impl;

import com.ruoyi.his.domain.Supplier;
import com.ruoyi.his.mapper.SupplierMapper;
import com.ruoyi.his.service.ISupplierService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 供应商Service业务层处理
 */
@Service
public class SupplierServiceImpl implements ISupplierService
{
    @Autowired
    private SupplierMapper supplierMapper;

    @Override
    public Supplier selectSupplierBySupplierId(Long supplierId)
    {
        return supplierMapper.selectSupplierBySupplierId(supplierId);
    }

    @Override
    public List<Supplier> selectSupplierList(Supplier supplier)
    {
        return supplierMapper.selectSupplierList(supplier);
    }

    @Override
    public int insertSupplier(Supplier supplier)
    {
        supplier.setCreateTime(com.ruoyi.common.utils.DateUtils.getNowDate());
        int rows = supplierMapper.insertSupplier(supplier);
        return rows;
    }

    @Override
    public int updateSupplier(Supplier supplier)
    {
        supplier.setUpdateTime(com.ruoyi.common.utils.DateUtils.getNowDate());
        return supplierMapper.updateSupplier(supplier);
    }

    @Override
    public int deleteSupplierBySupplierIds(Long[] supplierIds)
    {
        return supplierMapper.deleteSupplierBySupplierIds(supplierIds);
    }

    @Override
    public int deleteSupplierBySupplierId(Long supplierId)
    {
        return supplierMapper.deleteSupplierBySupplierId(supplierId);
    }
}

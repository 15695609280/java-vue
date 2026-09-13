package com.ruoyi.his.service.impl;

import com.ruoyi.his.domain.Drug;
import com.ruoyi.his.mapper.DrugMapper;
import com.ruoyi.his.service.IDrugService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 药品Service业务层处理
 */
@Service
public class DrugServiceImpl implements IDrugService
{
    @Autowired
    private DrugMapper drugMapper;

    @Override
    public Drug selectDrugByDrugId(Long drugId)
    {
        return drugMapper.selectDrugByDrugId(drugId);
    }

    @Override
    public List<Drug> selectDrugList(Drug drug)
    {
        return drugMapper.selectDrugList(drug);
    }

    @Override
    public int insertDrug(Drug drug)
    {
        drug.setCreateTime(com.ruoyi.common.utils.DateUtils.getNowDate());
        int rows = drugMapper.insertDrug(drug);
        return rows;
    }

    @Override
    public int updateDrug(Drug drug)
    {
        drug.setUpdateTime(com.ruoyi.common.utils.DateUtils.getNowDate());
        return drugMapper.updateDrug(drug);
    }

    @Override
    public int deleteDrugByDrugIds(Long[] drugIds)
    {
        return drugMapper.deleteDrugByDrugIds(drugIds);
    }

    @Override
    public int deleteDrugByDrugId(Long drugId)
    {
        return drugMapper.deleteDrugByDrugId(drugId);
    }

    @Override
    public List<Drug> selectWarnList()
    {
        return drugMapper.selectWarnList();
    }
}

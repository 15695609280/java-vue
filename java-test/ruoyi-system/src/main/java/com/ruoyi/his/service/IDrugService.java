package com.ruoyi.his.service;

import com.ruoyi.his.domain.Drug;
import java.util.List;

/**
 * 药品Service接口
 */
public interface IDrugService
{
    public Drug selectDrugByDrugId(Long drugId);

    public List<Drug> selectDrugList(Drug drug);

    public int insertDrug(Drug drug);

    public int updateDrug(Drug drug);

    public int deleteDrugByDrugIds(Long[] drugIds);

    public int deleteDrugByDrugId(Long drugId);

    /** 库存预警列表 */
    public List<Drug> selectWarnList();
}

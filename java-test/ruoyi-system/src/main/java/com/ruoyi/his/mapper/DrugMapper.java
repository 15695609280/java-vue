package com.ruoyi.his.mapper;

import com.ruoyi.his.domain.Drug;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * 药品Mapper接口
 */
public interface DrugMapper
{
    public Drug selectDrugByDrugId(Long drugId);

    public List<Drug> selectDrugList(Drug drug);

    public int insertDrug(Drug drug);

    public int updateDrug(Drug drug);

    public int deleteDrugByDrugId(Long drugId);

    public int deleteDrugByDrugIds(Long[] drugIds);

    /** 扣减库存(库存>=数量才成功) */
    public int deductStock(@Param("drugId") Long drugId, @Param("qty") Integer qty);

    /** 增加库存 */
    public int addStock(@Param("drugId") Long drugId, @Param("qty") Integer qty);

    /** 库存预警列表 */
    public List<Drug> selectWarnList();
}

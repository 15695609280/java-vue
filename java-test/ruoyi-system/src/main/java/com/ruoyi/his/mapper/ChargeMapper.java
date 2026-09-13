package com.ruoyi.his.mapper;

import com.ruoyi.his.domain.Charge;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * 费用单Mapper接口
 */
public interface ChargeMapper
{
    public Charge selectChargeByChargeId(Long chargeId);

    public List<Charge> selectChargeList(Charge charge);

    public int insertCharge(Charge charge);

    public int updateCharge(Charge charge);

    public int deleteChargeByChargeId(Long chargeId);

    public int deleteChargeByChargeIds(Long[] chargeIds);

    /** 按来源作废未收费用单 */
    public int cancelBySource(@Param("sourceId") Long sourceId, @Param("sourceType") String sourceType);

    /** 按来源退费已收费用单 */
    public int refundBySource(@Param("sourceId") Long sourceId, @Param("sourceType") String sourceType);
}

package com.ruoyi.his.service;

import com.ruoyi.his.domain.Charge;
import java.util.List;

/**
 * 费用单Service接口
 */
public interface IChargeService
{
    public Charge selectChargeByChargeId(Long chargeId);

    public List<Charge> selectChargeList(Charge charge);

    public int insertCharge(Charge charge);

    public int updateCharge(Charge charge);

    public int deleteChargeByChargeIds(Long[] chargeIds);

    public int deleteChargeByChargeId(Long chargeId);

    /** 批量结算 */
    public int settle(Long[] chargeIds, String payType);

    /** 退费 */
    public int refund(Long chargeId);
}

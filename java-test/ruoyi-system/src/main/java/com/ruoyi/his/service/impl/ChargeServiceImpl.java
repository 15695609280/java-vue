package com.ruoyi.his.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.his.domain.Charge;
import com.ruoyi.his.mapper.ChargeMapper;
import com.ruoyi.his.mapper.PrescriptionMapper;
import com.ruoyi.his.mapper.RegistrationMapper;
import com.ruoyi.his.service.IChargeService;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 费用单Service业务层处理
 */
@Service
public class ChargeServiceImpl implements IChargeService
{
    @Autowired
    private ChargeMapper chargeMapper;

    @Autowired
    private PrescriptionMapper prescriptionMapper;

    @Autowired
    private RegistrationMapper registrationMapper;

    @Override
    public Charge selectChargeByChargeId(Long chargeId)
    {
        return chargeMapper.selectChargeByChargeId(chargeId);
    }

    @Override
    public List<Charge> selectChargeList(Charge charge)
    {
        return chargeMapper.selectChargeList(charge);
    }

    @Override
    public int insertCharge(Charge charge)
    {
        charge.setCreateTime(com.ruoyi.common.utils.DateUtils.getNowDate());
        int rows = chargeMapper.insertCharge(charge);
        return rows;
    }

    @Override
    public int updateCharge(Charge charge)
    {
        charge.setUpdateTime(com.ruoyi.common.utils.DateUtils.getNowDate());
        return chargeMapper.updateCharge(charge);
    }

    @Override
    public int deleteChargeByChargeIds(Long[] chargeIds)
    {
        return chargeMapper.deleteChargeByChargeIds(chargeIds);
    }

    @Override
    public int deleteChargeByChargeId(Long chargeId)
    {
        return chargeMapper.deleteChargeByChargeId(chargeId);
    }

    /** 批量结算：置已收费，并联动更新来源单据状态(挂号->已缴费 处方->已收费) */
    @Override
    @Transactional
    public int settle(Long[] chargeIds, String payType)
    {
        int rows = 0;
        for (Long chargeId : chargeIds)
        {
            Charge charge = chargeMapper.selectChargeByChargeId(chargeId);
            if (charge == null || !"0".equals(charge.getChargeStatus())) { continue; }
            Charge upd = new Charge();
            upd.setChargeId(chargeId);
            upd.setChargeStatus("1");
            upd.setPayType(payType);
            upd.setSettleTime(new Date());
            upd.setOperator(SecurityUtils.getUsername());
            rows += chargeMapper.updateCharge(upd);
            if ("1".equals(charge.getSourceType()) && charge.getSourceId() != null)
            {
                prescriptionMapper.updateStatus(charge.getSourceId(), "1");
            }
            else if ("0".equals(charge.getSourceType()) && charge.getSourceId() != null)
            {
                registrationMapper.updatePayStatus(charge.getSourceId(), "1");
            }
        }
        return rows;
    }

    /** 退费：置已退费，并回滚来源单据状态 */
    @Override
    @Transactional
    public int refund(Long chargeId)
    {
        Charge charge = chargeMapper.selectChargeByChargeId(chargeId);
        if (charge == null) { throw new ServiceException("费用单不存在"); }
        if (!"1".equals(charge.getChargeStatus())) { throw new ServiceException("只有已收费单据才能退费"); }
        if ("1".equals(charge.getSourceType()) && charge.getSourceId() != null)
        {
            prescriptionMapper.updateStatus(charge.getSourceId(), "0");
        }
        else if ("0".equals(charge.getSourceType()) && charge.getSourceId() != null)
        {
            registrationMapper.updatePayStatus(charge.getSourceId(), "0");
        }
        Charge upd = new Charge();
        upd.setChargeId(chargeId);
        upd.setChargeStatus("2");
        upd.setSettleTime(new Date());
        upd.setOperator(SecurityUtils.getUsername());
        return chargeMapper.updateCharge(upd);
    }
}

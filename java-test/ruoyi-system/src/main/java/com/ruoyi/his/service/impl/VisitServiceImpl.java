package com.ruoyi.his.service.impl;

import com.ruoyi.his.domain.Visit;
import com.ruoyi.his.mapper.RegistrationMapper;
import com.ruoyi.his.mapper.VisitMapper;
import com.ruoyi.his.service.IVisitService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 就诊记录Service业务层处理
 */
@Service
public class VisitServiceImpl implements IVisitService
{
    @Autowired
    private VisitMapper visitMapper;

    @Autowired
    private RegistrationMapper registrationMapper;

    @Override
    public Visit selectVisitByVisitId(Long visitId)
    {
        return visitMapper.selectVisitByVisitId(visitId);
    }

    @Override
    public List<Visit> selectVisitList(Visit visit)
    {
        return visitMapper.selectVisitList(visit);
    }

    @Override
    public int insertVisit(Visit visit)
    {
        visit.setCreateTime(com.ruoyi.common.utils.DateUtils.getNowDate());
        int rows = visitMapper.insertVisit(visit);
        return rows;
    }

    @Override
    public int updateVisit(Visit visit)
    {
        visit.setUpdateTime(com.ruoyi.common.utils.DateUtils.getNowDate());
        return visitMapper.updateVisit(visit);
    }

    @Override
    public int deleteVisitByVisitIds(Long[] visitIds)
    {
        return visitMapper.deleteVisitByVisitIds(visitIds);
    }

    @Override
    public int deleteVisitByVisitId(Long visitId)
    {
        return visitMapper.deleteVisitByVisitId(visitId);
    }

    /** 完成就诊：病历状态置完诊，同时更新挂号单为已诊毕 */
    @Override
    @Transactional
    public int finishVisit(Long visitId)
    {
        Visit visit = visitMapper.selectVisitByVisitId(visitId);
        if (visit == null) { return 0; }
        Visit upd = new Visit();
        upd.setVisitId(visitId);
        upd.setStatus("1");
        int rows = visitMapper.updateVisit(upd);
        if (visit.getRegId() != null)
        {
            registrationMapper.updateVisitStatus(visit.getRegId(), "2");
        }
        return rows;
    }
}

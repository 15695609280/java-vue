package com.ruoyi.his.service;

import com.ruoyi.his.domain.Visit;
import java.util.List;

/**
 * 就诊记录Service接口
 */
public interface IVisitService
{
    public Visit selectVisitByVisitId(Long visitId);

    public List<Visit> selectVisitList(Visit visit);

    public int insertVisit(Visit visit);

    public int updateVisit(Visit visit);

    public int deleteVisitByVisitIds(Long[] visitIds);

    public int deleteVisitByVisitId(Long visitId);

    /** 完成就诊 */
    public int finishVisit(Long visitId);
}

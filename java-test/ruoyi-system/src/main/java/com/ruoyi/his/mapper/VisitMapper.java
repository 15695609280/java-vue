package com.ruoyi.his.mapper;

import com.ruoyi.his.domain.Visit;
import java.util.List;

/**
 * 就诊记录Mapper接口
 */
public interface VisitMapper
{
    public Visit selectVisitByVisitId(Long visitId);

    public List<Visit> selectVisitList(Visit visit);

    public int insertVisit(Visit visit);

    public int updateVisit(Visit visit);

    public int deleteVisitByVisitId(Long visitId);

    public int deleteVisitByVisitIds(Long[] visitIds);
}

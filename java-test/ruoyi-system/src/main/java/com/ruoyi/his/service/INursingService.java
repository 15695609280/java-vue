package com.ruoyi.his.service;

import com.ruoyi.his.domain.Nursing;
import java.util.List;

/**
 * 护理记录Service接口
 */
public interface INursingService
{
    public Nursing selectNursingByRecordId(Long recordId);

    public List<Nursing> selectNursingList(Nursing nursing);

    public int insertNursing(Nursing nursing);

    public int updateNursing(Nursing nursing);

    public int deleteNursingByRecordIds(Long[] recordIds);

    public int deleteNursingByRecordId(Long recordId);
}

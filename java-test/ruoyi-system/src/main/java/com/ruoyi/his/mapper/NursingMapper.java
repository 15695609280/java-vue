package com.ruoyi.his.mapper;

import com.ruoyi.his.domain.Nursing;
import java.util.List;

/**
 * 护理记录Mapper接口
 */
public interface NursingMapper
{
    public Nursing selectNursingByRecordId(Long recordId);

    public List<Nursing> selectNursingList(Nursing nursing);

    public int insertNursing(Nursing nursing);

    public int updateNursing(Nursing nursing);

    public int deleteNursingByRecordId(Long recordId);

    public int deleteNursingByRecordIds(Long[] recordIds);
}

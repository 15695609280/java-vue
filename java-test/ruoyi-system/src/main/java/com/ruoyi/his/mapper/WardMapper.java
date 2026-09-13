package com.ruoyi.his.mapper;

import com.ruoyi.his.domain.Ward;
import java.util.List;

/**
 * 病区Mapper接口
 */
public interface WardMapper
{
    public Ward selectWardByWardId(Long wardId);

    public List<Ward> selectWardList(Ward ward);

    public int insertWard(Ward ward);

    public int updateWard(Ward ward);

    public int deleteWardByWardId(Long wardId);

    public int deleteWardByWardIds(Long[] wardIds);
}

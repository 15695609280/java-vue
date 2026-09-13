package com.ruoyi.his.service;

import com.ruoyi.his.domain.Ward;
import java.util.List;

/**
 * 病区Service接口
 */
public interface IWardService
{
    public Ward selectWardByWardId(Long wardId);

    public List<Ward> selectWardList(Ward ward);

    public int insertWard(Ward ward);

    public int updateWard(Ward ward);

    public int deleteWardByWardIds(Long[] wardIds);

    public int deleteWardByWardId(Long wardId);
}

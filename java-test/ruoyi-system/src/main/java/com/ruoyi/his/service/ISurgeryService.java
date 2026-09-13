package com.ruoyi.his.service;

import com.ruoyi.his.domain.Surgery;
import java.util.List;

/**
 * 手术安排Service接口
 */
public interface ISurgeryService
{
    public Surgery selectSurgeryBySurgeryId(Long surgeryId);

    public List<Surgery> selectSurgeryList(Surgery surgery);

    public int insertSurgery(Surgery surgery);

    public int updateSurgery(Surgery surgery);

    public int deleteSurgeryBySurgeryIds(Long[] surgeryIds);

    public int deleteSurgeryBySurgeryId(Long surgeryId);
}

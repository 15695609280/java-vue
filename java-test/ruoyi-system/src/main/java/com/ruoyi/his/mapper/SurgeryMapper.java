package com.ruoyi.his.mapper;

import com.ruoyi.his.domain.Surgery;
import java.util.List;

/**
 * 手术安排Mapper接口
 */
public interface SurgeryMapper
{
    public Surgery selectSurgeryBySurgeryId(Long surgeryId);

    public List<Surgery> selectSurgeryList(Surgery surgery);

    public int insertSurgery(Surgery surgery);

    public int updateSurgery(Surgery surgery);

    public int deleteSurgeryBySurgeryId(Long surgeryId);

    public int deleteSurgeryBySurgeryIds(Long[] surgeryIds);
}

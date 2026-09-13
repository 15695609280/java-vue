package com.ruoyi.his.mapper;

import com.ruoyi.his.domain.Bed;
import java.util.List;

/**
 * 床位Mapper接口
 */
public interface BedMapper
{
    public Bed selectBedByBedId(Long bedId);

    public List<Bed> selectBedList(Bed bed);

    public int insertBed(Bed bed);

    public int updateBed(Bed bed);

    public int deleteBedByBedId(Long bedId);

    public int deleteBedByBedIds(Long[] bedIds);

    /** 查询病区下空闲床位 */
    public List<Bed> selectFreeBeds(Long wardId);
}

package com.ruoyi.his.mapper;

import com.ruoyi.his.domain.Schedule;
import java.util.List;

/**
 * 排班Mapper接口
 */
public interface ScheduleMapper
{
    public Schedule selectScheduleByScheduleId(Long scheduleId);

    public List<Schedule> selectScheduleList(Schedule schedule);

    public int insertSchedule(Schedule schedule);

    public int updateSchedule(Schedule schedule);

    public int deleteScheduleByScheduleId(Long scheduleId);

    public int deleteScheduleByScheduleIds(Long[] scheduleIds);

    /** 扣减号源(剩余号源-1，剩号>0才成功) */
    public int decrQuota(Long scheduleId);

    /** 恢复号源 */
    public int incrQuota(Long scheduleId);

    /** 查询可挂号排班(剩余>0) */
    public List<Schedule> selectAvailable(Schedule schedule);
}

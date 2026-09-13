package com.ruoyi.his.service;

import com.ruoyi.his.domain.Schedule;
import java.util.List;

/**
 * 排班Service接口
 */
public interface IScheduleService
{
    public Schedule selectScheduleByScheduleId(Long scheduleId);

    public List<Schedule> selectScheduleList(Schedule schedule);

    public int insertSchedule(Schedule schedule);

    public int updateSchedule(Schedule schedule);

    public int deleteScheduleByScheduleIds(Long[] scheduleIds);

    public int deleteScheduleByScheduleId(Long scheduleId);

    /** 查询可挂号排班 */
    public List<Schedule> selectAvailable(Schedule schedule);
}

package com.ruoyi.his.service.impl;

import com.ruoyi.his.domain.Schedule;
import com.ruoyi.his.mapper.ScheduleMapper;
import com.ruoyi.his.service.IScheduleService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 排班Service业务层处理
 */
@Service
public class ScheduleServiceImpl implements IScheduleService
{
    @Autowired
    private ScheduleMapper scheduleMapper;

    @Override
    public Schedule selectScheduleByScheduleId(Long scheduleId)
    {
        return scheduleMapper.selectScheduleByScheduleId(scheduleId);
    }

    @Override
    public List<Schedule> selectScheduleList(Schedule schedule)
    {
        return scheduleMapper.selectScheduleList(schedule);
    }

    @Override
    public int insertSchedule(Schedule schedule)
    {
        schedule.setCreateTime(com.ruoyi.common.utils.DateUtils.getNowDate());
        int rows = scheduleMapper.insertSchedule(schedule);
        return rows;
    }

    @Override
    public int updateSchedule(Schedule schedule)
    {
        schedule.setUpdateTime(com.ruoyi.common.utils.DateUtils.getNowDate());
        return scheduleMapper.updateSchedule(schedule);
    }

    @Override
    public int deleteScheduleByScheduleIds(Long[] scheduleIds)
    {
        return scheduleMapper.deleteScheduleByScheduleIds(scheduleIds);
    }

    @Override
    public int deleteScheduleByScheduleId(Long scheduleId)
    {
        return scheduleMapper.deleteScheduleByScheduleId(scheduleId);
    }

    @Override
    public List<Schedule> selectAvailable(Schedule schedule) { return scheduleMapper.selectAvailable(schedule); }
}

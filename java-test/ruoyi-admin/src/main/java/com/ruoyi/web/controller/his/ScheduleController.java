package com.ruoyi.web.controller.his;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.his.domain.Schedule;
import com.ruoyi.his.service.IScheduleService;

/**
 * 排班Controller
 */
@RestController
@RequestMapping("/his/schedule")
public class ScheduleController extends BaseController
{
    @Autowired
    private IScheduleService scheduleService;

    /** 查询排班列表 */
    @PreAuthorize("@ss.hasPermi('his:schedule:list')")
    @GetMapping("/list")
    public AjaxResult list(Schedule schedule)
    {
        startPage();
        List<Schedule> list = scheduleService.selectScheduleList(schedule);
        return toAjaxTable(list);
    }

    /** 导出排班列表 */
    @PreAuthorize("@ss.hasPermi('his:schedule:export')")
    @Log(title = "排班", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Schedule schedule)
    {
        List<Schedule> list = scheduleService.selectScheduleList(schedule);
        ExcelUtil<Schedule> util = new ExcelUtil<Schedule>(Schedule.class);
        util.exportExcel(response, list, "排班数据");
    }

    /** 获取排班详细信息 */
    @PreAuthorize("@ss.hasPermi('his:schedule:query')")
    @GetMapping(value = "/{scheduleId}")
    public AjaxResult getInfo(@PathVariable("scheduleId") Long scheduleId)
    {
        return success(scheduleService.selectScheduleByScheduleId(scheduleId));
    }

    /** 新增排班 */
    @PreAuthorize("@ss.hasPermi('his:schedule:add')")
    @Log(title = "排班", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Schedule schedule)
    {
        return toAjax(scheduleService.insertSchedule(schedule));
    }

    /** 修改排班 */
    @PreAuthorize("@ss.hasPermi('his:schedule:edit')")
    @Log(title = "排班", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Schedule schedule)
    {
        return toAjax(scheduleService.updateSchedule(schedule));
    }

    /** 删除排班 */
    @PreAuthorize("@ss.hasPermi('his:schedule:remove')")
    @Log(title = "排班", businessType = BusinessType.DELETE)
    @DeleteMapping("/{scheduleIds}")
    public AjaxResult remove(@PathVariable Long[] scheduleIds)
    {
        return toAjax(scheduleService.deleteScheduleByScheduleIds(scheduleIds));
    }

    /** 查询可挂号排班(挂号页联动用) */
    @GetMapping("/available")
    public AjaxResult available(Schedule schedule)
    {
        List<Schedule> list = scheduleService.selectAvailable(schedule);
        return toAjaxTable(list);
    }
}

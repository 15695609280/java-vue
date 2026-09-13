package com.ruoyi.web.controller.his;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.his.mapper.StatisticsMapper;

/**
 * 医疗统计Controller(工作台/经营报表)
 */
@RestController
@RequestMapping("/his/statistics")
public class StatisticsController extends BaseController
{
    @Autowired
    private StatisticsMapper statisticsMapper;

    /** 工作台总览 */
    @PreAuthorize("@ss.hasPermi('his:dashboard:list') or @ss.hasPermi('his:statistics:list')")
    @GetMapping("/overview")
    public AjaxResult overview()
    {
        return success(statisticsMapper.overview());
    }

    /** 近14天挂号趋势 */
    @GetMapping("/regTrend")
    public AjaxResult regTrend()
    {
        return success(statisticsMapper.regTrend());
    }

    /** 近30天收入趋势 */
    @GetMapping("/revenueTrend")
    public AjaxResult revenueTrend()
    {
        return success(statisticsMapper.revenueTrend());
    }

    /** 科室挂号分布 */
    @GetMapping("/deptReg")
    public AjaxResult deptReg()
    {
        return success(statisticsMapper.deptReg());
    }

    /** 药品消耗TOP10 */
    @GetMapping("/topDrugs")
    public AjaxResult topDrugs()
    {
        return success(statisticsMapper.topDrugs());
    }

    /** 病区床位使用率 */
    @GetMapping("/bedUsage")
    public AjaxResult bedUsage()
    {
        return success(statisticsMapper.bedUsage());
    }

    /** 费用类型占比 */
    @GetMapping("/chargeType")
    public AjaxResult chargeType()
    {
        return success(statisticsMapper.chargeType());
    }

    /** 医生工作量TOP10 */
    @GetMapping("/doctorWorkload")
    public AjaxResult doctorWorkload()
    {
        return success(statisticsMapper.doctorWorkload());
    }

    /** 近14天出入院趋势 */
    @GetMapping("/admTrend")
    public AjaxResult admTrend()
    {
        return success(statisticsMapper.admTrend());
    }

    /** 挂号时段分布 */
    @GetMapping("/slotDist")
    public AjaxResult slotDist()
    {
        return success(statisticsMapper.slotDist());
    }

    /** 科室收入分布 */
    @GetMapping("/deptRevenue")
    public AjaxResult deptRevenue()
    {
        return success(statisticsMapper.deptRevenue());
    }

    /** 本月经营汇总 */
    @GetMapping("/monthSummary")
    public AjaxResult monthSummary()
    {
        return success(statisticsMapper.monthSummary());
    }
}

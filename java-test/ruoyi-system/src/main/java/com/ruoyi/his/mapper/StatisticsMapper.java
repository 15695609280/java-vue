package com.ruoyi.his.mapper;

import java.util.List;
import java.util.Map;

/**
 * 医疗统计Mapper接口(返回Map供图表使用)
 */
public interface StatisticsMapper
{
    /** 工作台总览卡片 */
    public Map<String, Object> overview();

    /** 近14天挂号趋势 */
    public List<Map<String, Object>> regTrend();

    /** 近30天收入趋势 */
    public List<Map<String, Object>> revenueTrend();

    /** 科室挂号分布(饼图) */
    public List<Map<String, Object>> deptReg();

    /** 药品消耗TOP10(柱状图) */
    public List<Map<String, Object>> topDrugs();

    /** 病区床位使用率 */
    public List<Map<String, Object>> bedUsage();

    /** 费用类型占比 */
    public List<Map<String, Object>> chargeType();

    /** 医生工作量TOP(近30天接诊数) */
    public List<Map<String, Object>> doctorWorkload();

    /** 近30天出入院趋势 */
    public List<Map<String, Object>> admTrend();

    /** 挂号时段分布 */
    public List<Map<String, Object>> slotDist();

    /** 科室收入分布(已收费用按来源科室归属) */
    public List<Map<String, Object>> deptRevenue();

    /** 月度汇总卡片(本月挂号/收入/入院/出院) */
    public Map<String, Object> monthSummary();
}

package com.ruoyi.web.service.ai;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * AI 助手可用的系统菜单目录。
 * path 与前端路由 / sys_menu 中配置保持一致，actions 为页面上真实存在的按钮文字，
 * 用于约束大模型输出，避免编造不存在的路径与控件。
 */
public final class AiMenuCatalog
{
    public record MenuInfo(String path, String title, String group, String desc, String actions) {}

    public static final List<MenuInfo> MENUS = List.of(
        // ---------- 智慧医疗 ----------
        new MenuInfo("/his/dashboard", "医疗工作台", "智慧医疗", "查看今日挂号量、候诊人数、在院患者等运营总览", ""),
        new MenuInfo("/his/base/dept", "科室管理", "智慧医疗>基础数据", "维护医院科室信息", "新增、修改、删除、导出、搜索、重置"),
        new MenuInfo("/his/base/doctor", "医生管理", "智慧医疗>基础数据", "维护医生档案、职称、所属科室", "新增、修改、删除、导出、搜索、重置"),
        new MenuInfo("/his/base/patient", "患者档案", "智慧医疗>基础数据", "患者建档与档案维护（就诊卡号、联系方式）", "新增、修改、删除、导出、搜索、重置"),
        new MenuInfo("/his/base/drug", "药品信息", "智慧医疗>基础数据", "维护药品目录（名称、规格、单价）", "新增、修改、删除、导出、搜索、重置"),
        new MenuInfo("/his/base/supplier", "供应商管理", "智慧医疗>基础数据", "维护药品供应商，可查看采购记录", "新增供应商、采购记录、导出"),
        new MenuInfo("/his/base/feeitem", "收费项目", "智慧医疗>基础数据", "维护收费项目与价格", "新增、修改、删除、导出"),
        new MenuInfo("/his/base/ward", "病区管理", "智慧医疗>基础数据", "维护病区信息", "新增病区、编辑、删除、导出"),
        new MenuInfo("/his/base/bed", "床位管理", "智慧医疗>基础数据", "维护床位及占用状态", "新增、修改、删除、搜索"),

        new MenuInfo("/his/outpatient/schedule", "医生排班", "智慧医疗>门诊管理", "按周为医生排班并生成号源（时段与限额）", "本周、删除排班、确 定"),
        new MenuInfo("/his/outpatient/registration", "挂号管理", "智慧医疗>门诊管理", "门诊挂号：选择患者->科室->排班三步完成；支持退号、过号、恢复待诊、快速建档", "门诊挂号、确认挂号、没有档案？快速建档、退号、过号、恢复待诊、搜索"),
        new MenuInfo("/his/outpatient/queue", "候诊队列", "智慧医疗>门诊管理", "查看实时候诊队列，执行叫号、过号、恢复", "刷新队列、叫号、过号、恢复"),
        new MenuInfo("/his/outpatient/workstation", "医生工作站", "智慧医疗>门诊管理", "医生接诊入口：接诊->保存病历->开处方->申请检验/检查->完诊", "接诊、完诊、保存病历、添加药品、提交处方(生成待缴费)、申请检验、申请检查"),
        new MenuInfo("/his/outpatient/emr", "电子病历", "智慧医疗>门诊管理", "就诊记录列表，查看/修改病历并完诊", "查看病历、完诊、保存修改、搜索"),
        new MenuInfo("/his/outpatient/prescription", "处方管理", "智慧医疗>门诊管理", "处方列表与手动开方、作废", "手动开方、添加药品、提交处方、作废"),
        new MenuInfo("/his/outpatient/charge", "门诊收费", "智慧医疗>门诊管理", "勾选待缴费项目进行收费结算，已缴可退费", "收费结算、退费"),
        new MenuInfo("/his/outpatient/dispense", "药房发药", "智慧医疗>门诊管理", "对已缴费处方审核发药", "刷新、发药"),

        new MenuInfo("/his/inpatient/admission", "住院登记", "智慧医疗>住院管理", "入院登记：选择患者、病区、床位；可办理出院", "住院登记、确认登记、办理出院、搜索"),
        new MenuInfo("/his/inpatient/order", "医嘱管理", "智慧医疗>住院管理", "为在院患者开立医嘱，执行时自动计费，可停止", "开立医嘱、执行(自动计费)、停止、开立"),
        new MenuInfo("/his/inpatient/nursing", "护理记录", "智慧医疗>住院管理", "录入患者体征（体温、脉搏、血压等）", "录入体征、保存"),
        new MenuInfo("/his/inpatient/discharge", "出院结算", "智慧医疗>住院管理", "汇总住院费用并办理出院结算", "出院结算、确认出院结算"),

        new MenuInfo("/his/medtech/lab", "检验申请", "智慧医疗>医技管理", "检验流程：采样->开始检验->录入结果->出具报告", "采样、开始检验、录入结果、出具报告、添加项目、刷新"),
        new MenuInfo("/his/medtech/exam", "检查申请", "智慧医疗>医技管理", "检查流程：登记执行->出具报告->查看报告", "执行、出具报告、查看报告、刷新"),
        new MenuInfo("/his/medtech/surgery", "手术安排", "智慧医疗>医技管理", "手术申请->开始手术->完成", "手术申请、提交申请、开始手术、完成、取消"),

        new MenuInfo("/his/pharmacy/stock", "库存查询", "智慧医疗>药库管理", "查看药品库存数量与效期", "搜索、重置、导出"),
        new MenuInfo("/his/pharmacy/purchase", "采购入库", "智慧医疗>药库管理", "新建采购单->添加药品->创建采购单->确认入库", "新建采购单、添加药品、创建采购单、确认入库、取消"),
        new MenuInfo("/his/pharmacy/stockrecord", "出入库记录", "智慧医疗>药库管理", "库存出入库流水查询与导出", "查询、导出"),

        new MenuInfo("/his/report/statistics", "运营统计", "智慧医疗>统计报表", "收入、工作量等运营统计报表", ""),

        // ---------- 系统管理 ----------
        new MenuInfo("/system/user", "用户管理", "系统管理", "维护系统用户、分配角色、重置密码", "新增、修改、删除、导入、导出、重置密码、分配角色"),
        new MenuInfo("/system/role", "角色管理", "系统管理", "维护角色权限与数据范围", "新增、修改、删除、分配数据权限、分配用户"),
        new MenuInfo("/system/menu", "菜单管理", "系统管理", "维护菜单与按钮权限", "新增、修改、删除"),
        new MenuInfo("/system/dept", "部门管理", "系统管理", "维护组织架构", "新增、修改、删除"),
        new MenuInfo("/system/post", "岗位管理", "系统管理", "维护岗位信息", "新增、修改、删除、导出"),
        new MenuInfo("/system/dict", "字典管理", "系统管理", "维护字典类型与字典数据", "新增、修改、删除、导出、刷新缓存"),
        new MenuInfo("/system/config", "参数设置", "系统管理", "维护系统参数配置", "新增、修改、删除、刷新缓存"),
        new MenuInfo("/system/notice", "通知公告", "系统管理", "发布系统公告", "新增、修改、删除"),
        new MenuInfo("/system/log/operlog", "操作日志", "系统管理>日志管理", "查询用户操作日志", "删除、清空、导出、解锁"),
        new MenuInfo("/system/log/logininfor", "登录日志", "系统管理>日志管理", "查询登录日志", "删除、清空、导出、解锁"),

        // ---------- 系统监控 / 系统工具 ----------
        new MenuInfo("/monitor/online", "在线用户", "系统监控", "查看并强退在线会话", "强退"),
        new MenuInfo("/monitor/job", "定时任务", "系统监控", "维护调度任务", "新增、修改、删除、执行一次、调度日志"),
        new MenuInfo("/monitor/druid", "数据监控", "系统监控", "Druid 数据源监控", ""),
        new MenuInfo("/monitor/server", "服务监控", "系统监控", "服务器 CPU/内存/磁盘监控", ""),
        new MenuInfo("/monitor/cache", "缓存监控", "系统监控", "Redis 缓存监控", ""),
        new MenuInfo("/monitor/cacheList", "缓存列表", "系统监控", "Redis 缓存明细查询", "刷新、删除、清空"),
        new MenuInfo("/tool/build", "表单构建", "系统工具", "拖拽式表单设计", ""),
        new MenuInfo("/tool/gen", "代码生成", "系统工具", "根据表结构生成前后端代码", "导入、预览、生成代码、下载、同步"),
        new MenuInfo("/tool/swagger", "系统接口", "系统工具", "Swagger 接口文档", ""),
        new MenuInfo("/product", "商品管理", "", "演示模块：商品 CRUD", "新增、修改、删除、导出、搜索"),
        new MenuInfo("/index", "首页", "", "系统首页", "")
    );

    public static final Set<String> PATHS = MENUS.stream().map(MenuInfo::path).collect(Collectors.toSet());

    private static final Map<String, MenuInfo> BY_PATH =
        MENUS.stream().collect(Collectors.toMap(MenuInfo::path, m -> m, (a, b) -> a, LinkedHashMap::new));

    public static MenuInfo of(String path)
    {
        return BY_PATH.get(path);
    }

    /** 供系统提示词使用的目录描述文本 */
    public static String toPromptText()
    {
        StringBuilder sb = new StringBuilder();
        for (MenuInfo m : MENUS)
        {
            sb.append("- path=").append(m.path())
              .append(" | 菜单=").append(m.title());
            if (m.group() != null && !m.group().isEmpty())
            {
                sb.append(" | 分组=").append(m.group());
            }
            sb.append(" | 功能=").append(m.desc());
            if (m.actions() != null && !m.actions().isEmpty())
            {
                sb.append(" | 页面按钮=[").append(m.actions()).append("]");
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}

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
import com.ruoyi.his.domain.Visit;
import com.ruoyi.his.service.IVisitService;

/**
 * 就诊记录Controller
 */
@RestController
@RequestMapping("/his/visit")
public class VisitController extends BaseController
{
    @Autowired
    private IVisitService visitService;

    /** 查询就诊记录列表 */
    @PreAuthorize("@ss.hasPermi('his:visit:list')")
    @GetMapping("/list")
    public AjaxResult list(Visit visit)
    {
        startPage();
        List<Visit> list = visitService.selectVisitList(visit);
        return toAjaxTable(list);
    }

    /** 导出就诊记录列表 */
    @PreAuthorize("@ss.hasPermi('his:visit:export')")
    @Log(title = "就诊记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Visit visit)
    {
        List<Visit> list = visitService.selectVisitList(visit);
        ExcelUtil<Visit> util = new ExcelUtil<Visit>(Visit.class);
        util.exportExcel(response, list, "就诊记录数据");
    }

    /** 获取就诊记录详细信息 */
    @PreAuthorize("@ss.hasPermi('his:visit:query')")
    @GetMapping(value = "/{visitId}")
    public AjaxResult getInfo(@PathVariable("visitId") Long visitId)
    {
        return success(visitService.selectVisitByVisitId(visitId));
    }

    /** 新增就诊记录 */
    @PreAuthorize("@ss.hasPermi('his:visit:add')")
    @Log(title = "就诊记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Visit visit)
    {
        return toAjax(visitService.insertVisit(visit));
    }

    /** 修改就诊记录 */
    @PreAuthorize("@ss.hasPermi('his:visit:edit')")
    @Log(title = "就诊记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Visit visit)
    {
        return toAjax(visitService.updateVisit(visit));
    }

    /** 删除就诊记录 */
    @PreAuthorize("@ss.hasPermi('his:visit:remove')")
    @Log(title = "就诊记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{visitIds}")
    public AjaxResult remove(@PathVariable Long[] visitIds)
    {
        return toAjax(visitService.deleteVisitByVisitIds(visitIds));
    }

    /** 完成就诊(病历完诊 -> 挂号状态置为已诊毕) */
    @PreAuthorize("@ss.hasPermi('his:visit:edit')")
    @Log(title = "就诊记录", businessType = BusinessType.UPDATE)
    @PutMapping("/finish/{visitId}")
    public AjaxResult finish(@PathVariable("visitId") Long visitId)
    {
        return toAjax(visitService.finishVisit(visitId));
    }
}

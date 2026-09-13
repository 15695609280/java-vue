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
import com.ruoyi.his.domain.Surgery;
import com.ruoyi.his.service.ISurgeryService;

/**
 * 手术安排Controller
 */
@RestController
@RequestMapping("/his/surgery")
public class SurgeryController extends BaseController
{
    @Autowired
    private ISurgeryService surgeryService;

    /** 查询手术安排列表 */
    @PreAuthorize("@ss.hasPermi('his:surgery:list')")
    @GetMapping("/list")
    public AjaxResult list(Surgery surgery)
    {
        startPage();
        List<Surgery> list = surgeryService.selectSurgeryList(surgery);
        return toAjaxTable(list);
    }

    /** 导出手术安排列表 */
    @PreAuthorize("@ss.hasPermi('his:surgery:export')")
    @Log(title = "手术安排", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Surgery surgery)
    {
        List<Surgery> list = surgeryService.selectSurgeryList(surgery);
        ExcelUtil<Surgery> util = new ExcelUtil<Surgery>(Surgery.class);
        util.exportExcel(response, list, "手术安排数据");
    }

    /** 获取手术安排详细信息 */
    @PreAuthorize("@ss.hasPermi('his:surgery:query')")
    @GetMapping(value = "/{surgeryId}")
    public AjaxResult getInfo(@PathVariable("surgeryId") Long surgeryId)
    {
        return success(surgeryService.selectSurgeryBySurgeryId(surgeryId));
    }

    /** 新增手术安排 */
    @PreAuthorize("@ss.hasPermi('his:surgery:add')")
    @Log(title = "手术安排", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Surgery surgery)
    {
        return toAjax(surgeryService.insertSurgery(surgery));
    }

    /** 修改手术安排 */
    @PreAuthorize("@ss.hasPermi('his:surgery:edit')")
    @Log(title = "手术安排", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Surgery surgery)
    {
        return toAjax(surgeryService.updateSurgery(surgery));
    }

    /** 删除手术安排 */
    @PreAuthorize("@ss.hasPermi('his:surgery:remove')")
    @Log(title = "手术安排", businessType = BusinessType.DELETE)
    @DeleteMapping("/{surgeryIds}")
    public AjaxResult remove(@PathVariable Long[] surgeryIds)
    {
        return toAjax(surgeryService.deleteSurgeryBySurgeryIds(surgeryIds));
    }

    /** 手术状态流转 */
    @PreAuthorize("@ss.hasPermi('his:surgery:edit')")
    @Log(title = "手术安排", businessType = BusinessType.UPDATE)
    @PutMapping("/status/{surgeryId}/{status}")
    public AjaxResult changeStatus(@PathVariable("surgeryId") Long surgeryId, @PathVariable("status") String status)
    {
        Surgery s = new Surgery();
        s.setSurgeryId(surgeryId);
        s.setStatus(status);
        if ("1".equals(status)) { s.setStartTime(new java.util.Date()); }
        if ("2".equals(status)) { s.setEndTime(new java.util.Date()); }
        return toAjax(surgeryService.updateSurgery(s));
    }
}

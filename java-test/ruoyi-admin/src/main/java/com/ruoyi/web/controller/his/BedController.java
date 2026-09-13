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
import com.ruoyi.his.domain.Bed;
import com.ruoyi.his.service.IBedService;

/**
 * 床位Controller
 */
@RestController
@RequestMapping("/his/bed")
public class BedController extends BaseController
{
    @Autowired
    private IBedService bedService;

    /** 查询床位列表 */
    @PreAuthorize("@ss.hasPermi('his:bed:list')")
    @GetMapping("/list")
    public AjaxResult list(Bed bed)
    {
        startPage();
        List<Bed> list = bedService.selectBedList(bed);
        return toAjaxTable(list);
    }

    /** 导出床位列表 */
    @PreAuthorize("@ss.hasPermi('his:bed:export')")
    @Log(title = "床位", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Bed bed)
    {
        List<Bed> list = bedService.selectBedList(bed);
        ExcelUtil<Bed> util = new ExcelUtil<Bed>(Bed.class);
        util.exportExcel(response, list, "床位数据");
    }

    /** 获取床位详细信息 */
    @PreAuthorize("@ss.hasPermi('his:bed:query')")
    @GetMapping(value = "/{bedId}")
    public AjaxResult getInfo(@PathVariable("bedId") Long bedId)
    {
        return success(bedService.selectBedByBedId(bedId));
    }

    /** 新增床位 */
    @PreAuthorize("@ss.hasPermi('his:bed:add')")
    @Log(title = "床位", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Bed bed)
    {
        return toAjax(bedService.insertBed(bed));
    }

    /** 修改床位 */
    @PreAuthorize("@ss.hasPermi('his:bed:edit')")
    @Log(title = "床位", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Bed bed)
    {
        return toAjax(bedService.updateBed(bed));
    }

    /** 删除床位 */
    @PreAuthorize("@ss.hasPermi('his:bed:remove')")
    @Log(title = "床位", businessType = BusinessType.DELETE)
    @DeleteMapping("/{bedIds}")
    public AjaxResult remove(@PathVariable Long[] bedIds)
    {
        return toAjax(bedService.deleteBedByBedIds(bedIds));
    }

    /** 查询病区空闲床位 */
    @GetMapping("/free/{wardId}")
    public AjaxResult freeBeds(@PathVariable("wardId") Long wardId)
    {
        return success(bedService.selectFreeBeds(wardId));
    }

    /** 修改床位状态 */
    @PreAuthorize("@ss.hasPermi('his:bed:edit')")
    @Log(title = "床位", businessType = BusinessType.UPDATE)
    @PutMapping("/status/{bedId}/{status}")
    public AjaxResult changeStatus(@PathVariable("bedId") Long bedId, @PathVariable("status") String status)
    {
        Bed bed = new Bed();
        bed.setBedId(bedId);
        bed.setStatus(status);
        return toAjax(bedService.updateBed(bed));
    }
}

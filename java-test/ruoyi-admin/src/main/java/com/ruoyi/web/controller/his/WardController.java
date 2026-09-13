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
import com.ruoyi.his.domain.Ward;
import com.ruoyi.his.service.IWardService;

/**
 * 病区Controller
 */
@RestController
@RequestMapping("/his/ward")
public class WardController extends BaseController
{
    @Autowired
    private IWardService wardService;

    /** 查询病区列表 */
    @PreAuthorize("@ss.hasPermi('his:ward:list')")
    @GetMapping("/list")
    public AjaxResult list(Ward ward)
    {
        startPage();
        List<Ward> list = wardService.selectWardList(ward);
        return toAjaxTable(list);
    }

    /** 导出病区列表 */
    @PreAuthorize("@ss.hasPermi('his:ward:export')")
    @Log(title = "病区", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Ward ward)
    {
        List<Ward> list = wardService.selectWardList(ward);
        ExcelUtil<Ward> util = new ExcelUtil<Ward>(Ward.class);
        util.exportExcel(response, list, "病区数据");
    }

    /** 获取病区详细信息 */
    @PreAuthorize("@ss.hasPermi('his:ward:query')")
    @GetMapping(value = "/{wardId}")
    public AjaxResult getInfo(@PathVariable("wardId") Long wardId)
    {
        return success(wardService.selectWardByWardId(wardId));
    }

    /** 新增病区 */
    @PreAuthorize("@ss.hasPermi('his:ward:add')")
    @Log(title = "病区", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Ward ward)
    {
        return toAjax(wardService.insertWard(ward));
    }

    /** 修改病区 */
    @PreAuthorize("@ss.hasPermi('his:ward:edit')")
    @Log(title = "病区", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Ward ward)
    {
        return toAjax(wardService.updateWard(ward));
    }

    /** 删除病区 */
    @PreAuthorize("@ss.hasPermi('his:ward:remove')")
    @Log(title = "病区", businessType = BusinessType.DELETE)
    @DeleteMapping("/{wardIds}")
    public AjaxResult remove(@PathVariable Long[] wardIds)
    {
        return toAjax(wardService.deleteWardByWardIds(wardIds));
    }
}

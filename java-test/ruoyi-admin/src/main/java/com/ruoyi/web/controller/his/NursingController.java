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
import com.ruoyi.his.domain.Nursing;
import com.ruoyi.his.service.INursingService;

/**
 * 护理记录Controller
 */
@RestController
@RequestMapping("/his/nursing")
public class NursingController extends BaseController
{
    @Autowired
    private INursingService nursingService;

    /** 查询护理记录列表 */
    @PreAuthorize("@ss.hasPermi('his:nursing:list')")
    @GetMapping("/list")
    public AjaxResult list(Nursing nursing)
    {
        startPage();
        List<Nursing> list = nursingService.selectNursingList(nursing);
        return toAjaxTable(list);
    }

    /** 导出护理记录列表 */
    @PreAuthorize("@ss.hasPermi('his:nursing:export')")
    @Log(title = "护理记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Nursing nursing)
    {
        List<Nursing> list = nursingService.selectNursingList(nursing);
        ExcelUtil<Nursing> util = new ExcelUtil<Nursing>(Nursing.class);
        util.exportExcel(response, list, "护理记录数据");
    }

    /** 获取护理记录详细信息 */
    @PreAuthorize("@ss.hasPermi('his:nursing:query')")
    @GetMapping(value = "/{recordId}")
    public AjaxResult getInfo(@PathVariable("recordId") Long recordId)
    {
        return success(nursingService.selectNursingByRecordId(recordId));
    }

    /** 新增护理记录 */
    @PreAuthorize("@ss.hasPermi('his:nursing:add')")
    @Log(title = "护理记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Nursing nursing)
    {
        return toAjax(nursingService.insertNursing(nursing));
    }

    /** 修改护理记录 */
    @PreAuthorize("@ss.hasPermi('his:nursing:edit')")
    @Log(title = "护理记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Nursing nursing)
    {
        return toAjax(nursingService.updateNursing(nursing));
    }

    /** 删除护理记录 */
    @PreAuthorize("@ss.hasPermi('his:nursing:remove')")
    @Log(title = "护理记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{recordIds}")
    public AjaxResult remove(@PathVariable Long[] recordIds)
    {
        return toAjax(nursingService.deleteNursingByRecordIds(recordIds));
    }

    /** 按住院查询护理记录(体温单曲线用，时间正序) */
    @GetMapping("/byAdm/{admId}")
    public AjaxResult byAdm(@PathVariable("admId") Long admId)
    {
        Nursing q = new Nursing();
        q.setAdmId(admId);
        List<Nursing> list = nursingService.selectNursingList(q);
        list.sort(java.util.Comparator.comparing(Nursing::getRecordTime, java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder())));
        return success(list);
    }
}

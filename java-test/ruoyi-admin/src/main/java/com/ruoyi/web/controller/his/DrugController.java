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
import com.ruoyi.his.domain.Drug;
import com.ruoyi.his.service.IDrugService;

/**
 * 药品Controller
 */
@RestController
@RequestMapping("/his/drug")
public class DrugController extends BaseController
{
    @Autowired
    private IDrugService drugService;

    /** 查询药品列表 */
    @PreAuthorize("@ss.hasPermi('his:drug:list')")
    @GetMapping("/list")
    public AjaxResult list(Drug drug)
    {
        startPage();
        List<Drug> list = drugService.selectDrugList(drug);
        return toAjaxTable(list);
    }

    /** 导出药品列表 */
    @PreAuthorize("@ss.hasPermi('his:drug:export')")
    @Log(title = "药品", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Drug drug)
    {
        List<Drug> list = drugService.selectDrugList(drug);
        ExcelUtil<Drug> util = new ExcelUtil<Drug>(Drug.class);
        util.exportExcel(response, list, "药品数据");
    }

    /** 获取药品详细信息 */
    @PreAuthorize("@ss.hasPermi('his:drug:query')")
    @GetMapping(value = "/{drugId}")
    public AjaxResult getInfo(@PathVariable("drugId") Long drugId)
    {
        return success(drugService.selectDrugByDrugId(drugId));
    }

    /** 新增药品 */
    @PreAuthorize("@ss.hasPermi('his:drug:add')")
    @Log(title = "药品", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Drug drug)
    {
        return toAjax(drugService.insertDrug(drug));
    }

    /** 修改药品 */
    @PreAuthorize("@ss.hasPermi('his:drug:edit')")
    @Log(title = "药品", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Drug drug)
    {
        return toAjax(drugService.updateDrug(drug));
    }

    /** 删除药品 */
    @PreAuthorize("@ss.hasPermi('his:drug:remove')")
    @Log(title = "药品", businessType = BusinessType.DELETE)
    @DeleteMapping("/{drugIds}")
    public AjaxResult remove(@PathVariable Long[] drugIds)
    {
        return toAjax(drugService.deleteDrugByDrugIds(drugIds));
    }

    /** 库存预警 */
    @GetMapping("/warnList")
    public AjaxResult warnList()
    {
        List<Drug> list = drugService.selectWarnList();
        return toAjaxTable(list);
    }
}

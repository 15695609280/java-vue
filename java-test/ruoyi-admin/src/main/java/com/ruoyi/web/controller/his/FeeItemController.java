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
import com.ruoyi.his.domain.FeeItem;
import com.ruoyi.his.service.IFeeItemService;

/**
 * 收费项目Controller
 */
@RestController
@RequestMapping("/his/feeitem")
public class FeeItemController extends BaseController
{
    @Autowired
    private IFeeItemService feeItemService;

    /** 查询收费项目列表 */
    @PreAuthorize("@ss.hasPermi('his:feeitem:list')")
    @GetMapping("/list")
    public AjaxResult list(FeeItem feeItem)
    {
        startPage();
        List<FeeItem> list = feeItemService.selectFeeItemList(feeItem);
        return toAjaxTable(list);
    }

    /** 导出收费项目列表 */
    @PreAuthorize("@ss.hasPermi('his:feeitem:export')")
    @Log(title = "收费项目", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FeeItem feeItem)
    {
        List<FeeItem> list = feeItemService.selectFeeItemList(feeItem);
        ExcelUtil<FeeItem> util = new ExcelUtil<FeeItem>(FeeItem.class);
        util.exportExcel(response, list, "收费项目数据");
    }

    /** 获取收费项目详细信息 */
    @PreAuthorize("@ss.hasPermi('his:feeitem:query')")
    @GetMapping(value = "/{itemId}")
    public AjaxResult getInfo(@PathVariable("itemId") Long itemId)
    {
        return success(feeItemService.selectFeeItemByItemId(itemId));
    }

    /** 新增收费项目 */
    @PreAuthorize("@ss.hasPermi('his:feeitem:add')")
    @Log(title = "收费项目", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FeeItem feeItem)
    {
        return toAjax(feeItemService.insertFeeItem(feeItem));
    }

    /** 修改收费项目 */
    @PreAuthorize("@ss.hasPermi('his:feeitem:edit')")
    @Log(title = "收费项目", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FeeItem feeItem)
    {
        return toAjax(feeItemService.updateFeeItem(feeItem));
    }

    /** 删除收费项目 */
    @PreAuthorize("@ss.hasPermi('his:feeitem:remove')")
    @Log(title = "收费项目", businessType = BusinessType.DELETE)
    @DeleteMapping("/{itemIds}")
    public AjaxResult remove(@PathVariable Long[] itemIds)
    {
        return toAjax(feeItemService.deleteFeeItemByItemIds(itemIds));
    }
}

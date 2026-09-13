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
import com.ruoyi.his.domain.Purchase;
import com.ruoyi.his.service.IPurchaseService;

/**
 * 采购单Controller
 */
@RestController
@RequestMapping("/his/purchase")
public class PurchaseController extends BaseController
{
    @Autowired
    private IPurchaseService purchaseService;

    /** 查询采购单列表 */
    @PreAuthorize("@ss.hasPermi('his:purchase:list')")
    @GetMapping("/list")
    public AjaxResult list(Purchase purchase)
    {
        startPage();
        List<Purchase> list = purchaseService.selectPurchaseList(purchase);
        return toAjaxTable(list);
    }

    /** 导出采购单列表 */
    @PreAuthorize("@ss.hasPermi('his:purchase:export')")
    @Log(title = "采购单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Purchase purchase)
    {
        List<Purchase> list = purchaseService.selectPurchaseList(purchase);
        ExcelUtil<Purchase> util = new ExcelUtil<Purchase>(Purchase.class);
        util.exportExcel(response, list, "采购单数据");
    }

    /** 获取采购单详细信息 */
    @PreAuthorize("@ss.hasPermi('his:purchase:query')")
    @GetMapping(value = "/{purchaseId}")
    public AjaxResult getInfo(@PathVariable("purchaseId") Long purchaseId)
    {
        return success(purchaseService.selectPurchaseByPurchaseId(purchaseId));
    }

    /** 新增采购单 */
    @PreAuthorize("@ss.hasPermi('his:purchase:add')")
    @Log(title = "采购单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Purchase purchase)
    {
        return toAjax(purchaseService.insertPurchase(purchase));
    }

    /** 修改采购单 */
    @PreAuthorize("@ss.hasPermi('his:purchase:edit')")
    @Log(title = "采购单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Purchase purchase)
    {
        return toAjax(purchaseService.updatePurchase(purchase));
    }

    /** 删除采购单 */
    @PreAuthorize("@ss.hasPermi('his:purchase:remove')")
    @Log(title = "采购单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{purchaseIds}")
    public AjaxResult remove(@PathVariable Long[] purchaseIds)
    {
        return toAjax(purchaseService.deletePurchaseByPurchaseIds(purchaseIds));
    }

    /** 新建采购单(含明细) */
    @PreAuthorize("@ss.hasPermi('his:purchase:add')")
    @Log(title = "采购单", businessType = BusinessType.INSERT)
    @PostMapping("/create")
    public AjaxResult create(@RequestBody Purchase purchase)
    {
        Long id = purchaseService.createPurchase(purchase);
        return success(purchaseService.selectPurchaseByPurchaseId(id));
    }

    /** 入库审核 */
    @PreAuthorize("@ss.hasPermi('his:purchase:inbound')")
    @Log(title = "采购入库", businessType = BusinessType.UPDATE)
    @PutMapping("/inbound/{purchaseId}")
    public AjaxResult inbound(@PathVariable("purchaseId") Long purchaseId)
    {
        return toAjax(purchaseService.inbound(purchaseId));
    }

    /** 采购明细 */
    @GetMapping("/items/{purchaseId}")
    public AjaxResult items(@PathVariable("purchaseId") Long purchaseId)
    {
        return success(purchaseService.selectItemList(purchaseId));
    }
}

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
import com.ruoyi.his.domain.Charge;
import com.ruoyi.his.service.IChargeService;

/**
 * 费用单Controller
 */
@RestController
@RequestMapping("/his/charge")
public class ChargeController extends BaseController
{
    @Autowired
    private IChargeService chargeService;

    /** 查询费用单列表 */
    @PreAuthorize("@ss.hasPermi('his:charge:list')")
    @GetMapping("/list")
    public AjaxResult list(Charge charge)
    {
        startPage();
        List<Charge> list = chargeService.selectChargeList(charge);
        return toAjaxTable(list);
    }

    /** 导出费用单列表 */
    @PreAuthorize("@ss.hasPermi('his:charge:export')")
    @Log(title = "费用单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Charge charge)
    {
        List<Charge> list = chargeService.selectChargeList(charge);
        ExcelUtil<Charge> util = new ExcelUtil<Charge>(Charge.class);
        util.exportExcel(response, list, "费用单数据");
    }

    /** 获取费用单详细信息 */
    @PreAuthorize("@ss.hasPermi('his:charge:query')")
    @GetMapping(value = "/{chargeId}")
    public AjaxResult getInfo(@PathVariable("chargeId") Long chargeId)
    {
        return success(chargeService.selectChargeByChargeId(chargeId));
    }

    /** 新增费用单 */
    @PreAuthorize("@ss.hasPermi('his:charge:add')")
    @Log(title = "费用单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Charge charge)
    {
        return toAjax(chargeService.insertCharge(charge));
    }

    /** 修改费用单 */
    @PreAuthorize("@ss.hasPermi('his:charge:edit')")
    @Log(title = "费用单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Charge charge)
    {
        return toAjax(chargeService.updateCharge(charge));
    }

    /** 删除费用单 */
    @PreAuthorize("@ss.hasPermi('his:charge:remove')")
    @Log(title = "费用单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{chargeIds}")
    public AjaxResult remove(@PathVariable Long[] chargeIds)
    {
        return toAjax(chargeService.deleteChargeByChargeIds(chargeIds));
    }

    /** 批量结算 */
    @PreAuthorize("@ss.hasPermi('his:charge:settle') or @ss.hasPermi('his:discharge:settle')")
    @Log(title = "收费结算", businessType = BusinessType.UPDATE)
    @PutMapping("/settle")
    public AjaxResult settle(@RequestBody Charge charge)
    {
        return toAjax(chargeService.settle(charge.getChargeIds(), charge.getPayType()));
    }

    /** 退费 */
    @PreAuthorize("@ss.hasPermi('his:charge:refund')")
    @Log(title = "退费", businessType = BusinessType.UPDATE)
    @PutMapping("/refund/{chargeId}")
    public AjaxResult refund(@PathVariable("chargeId") Long chargeId)
    {
        return toAjax(chargeService.refund(chargeId));
    }
}

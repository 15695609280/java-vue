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
import com.ruoyi.his.domain.MedicalOrder;
import com.ruoyi.his.service.IMedicalOrderService;

/**
 * 医嘱Controller
 */
@RestController
@RequestMapping("/his/order")
public class MedicalOrderController extends BaseController
{
    @Autowired
    private IMedicalOrderService medicalOrderService;

    /** 查询医嘱列表 */
    @PreAuthorize("@ss.hasPermi('his:order:list')")
    @GetMapping("/list")
    public AjaxResult list(MedicalOrder medicalOrder)
    {
        startPage();
        List<MedicalOrder> list = medicalOrderService.selectMedicalOrderList(medicalOrder);
        return toAjaxTable(list);
    }

    /** 导出医嘱列表 */
    @PreAuthorize("@ss.hasPermi('his:order:export')")
    @Log(title = "医嘱", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, MedicalOrder medicalOrder)
    {
        List<MedicalOrder> list = medicalOrderService.selectMedicalOrderList(medicalOrder);
        ExcelUtil<MedicalOrder> util = new ExcelUtil<MedicalOrder>(MedicalOrder.class);
        util.exportExcel(response, list, "医嘱数据");
    }

    /** 获取医嘱详细信息 */
    @PreAuthorize("@ss.hasPermi('his:order:query')")
    @GetMapping(value = "/{orderId}")
    public AjaxResult getInfo(@PathVariable("orderId") Long orderId)
    {
        return success(medicalOrderService.selectMedicalOrderByOrderId(orderId));
    }

    /** 新增医嘱 */
    @PreAuthorize("@ss.hasPermi('his:order:add')")
    @Log(title = "医嘱", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody MedicalOrder medicalOrder)
    {
        return toAjax(medicalOrderService.insertMedicalOrder(medicalOrder));
    }

    /** 修改医嘱 */
    @PreAuthorize("@ss.hasPermi('his:order:edit')")
    @Log(title = "医嘱", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody MedicalOrder medicalOrder)
    {
        return toAjax(medicalOrderService.updateMedicalOrder(medicalOrder));
    }

    /** 删除医嘱 */
    @PreAuthorize("@ss.hasPermi('his:order:remove')")
    @Log(title = "医嘱", businessType = BusinessType.DELETE)
    @DeleteMapping("/{orderIds}")
    public AjaxResult remove(@PathVariable Long[] orderIds)
    {
        return toAjax(medicalOrderService.deleteMedicalOrderByOrderIds(orderIds));
    }

    /** 执行医嘱 */
    @PreAuthorize("@ss.hasPermi('his:order:exec')")
    @Log(title = "医嘱执行", businessType = BusinessType.UPDATE)
    @PutMapping("/execute/{orderId}")
    public AjaxResult execute(@PathVariable("orderId") Long orderId)
    {
        return toAjax(medicalOrderService.execute(orderId));
    }

    /** 停止医嘱 */
    @PreAuthorize("@ss.hasPermi('his:order:stop')")
    @Log(title = "医嘱停止", businessType = BusinessType.UPDATE)
    @PutMapping("/stop/{orderId}")
    public AjaxResult stop(@PathVariable("orderId") Long orderId)
    {
        return toAjax(medicalOrderService.stop(orderId));
    }
}

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
import com.ruoyi.his.domain.Prescription;
import com.ruoyi.his.service.IPrescriptionService;

/**
 * 处方Controller
 */
@RestController
@RequestMapping("/his/prescription")
public class PrescriptionController extends BaseController
{
    @Autowired
    private IPrescriptionService prescriptionService;

    /** 查询处方列表 */
    @PreAuthorize("@ss.hasPermi('his:prescription:list')")
    @GetMapping("/list")
    public AjaxResult list(Prescription prescription)
    {
        startPage();
        List<Prescription> list = prescriptionService.selectPrescriptionList(prescription);
        return toAjaxTable(list);
    }

    /** 导出处方列表 */
    @PreAuthorize("@ss.hasPermi('his:prescription:export')")
    @Log(title = "处方", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Prescription prescription)
    {
        List<Prescription> list = prescriptionService.selectPrescriptionList(prescription);
        ExcelUtil<Prescription> util = new ExcelUtil<Prescription>(Prescription.class);
        util.exportExcel(response, list, "处方数据");
    }

    /** 获取处方详细信息 */
    @PreAuthorize("@ss.hasPermi('his:prescription:query')")
    @GetMapping(value = "/{rxId}")
    public AjaxResult getInfo(@PathVariable("rxId") Long rxId)
    {
        return success(prescriptionService.selectPrescriptionByRxId(rxId));
    }

    /** 新增处方 */
    @PreAuthorize("@ss.hasPermi('his:prescription:add')")
    @Log(title = "处方", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Prescription prescription)
    {
        return toAjax(prescriptionService.insertPrescription(prescription));
    }

    /** 修改处方 */
    @PreAuthorize("@ss.hasPermi('his:prescription:edit')")
    @Log(title = "处方", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Prescription prescription)
    {
        return toAjax(prescriptionService.updatePrescription(prescription));
    }

    /** 删除处方 */
    @PreAuthorize("@ss.hasPermi('his:prescription:remove')")
    @Log(title = "处方", businessType = BusinessType.DELETE)
    @DeleteMapping("/{rxIds}")
    public AjaxResult remove(@PathVariable Long[] rxIds)
    {
        return toAjax(prescriptionService.deletePrescriptionByRxIds(rxIds));
    }

    /** 开具处方(含明细) */
    @PreAuthorize("@ss.hasPermi('his:prescription:add') or @ss.hasPermi('his:workstation:rx')")
    @Log(title = "处方", businessType = BusinessType.INSERT)
    @PostMapping("/create")
    public AjaxResult create(@RequestBody Prescription prescription)
    {
        Long rxId = prescriptionService.createPrescription(prescription);
        return success(prescriptionService.selectPrescriptionByRxId(rxId));
    }

    /** 待发药处方(已收费) */
    @GetMapping("/paid")
    public AjaxResult paid(Prescription prescription)
    {
        prescription.setStatus("1");
        List<Prescription> list = prescriptionService.selectPrescriptionList(prescription);
        return toAjaxTable(list);
    }

    /** 发药 */
    @PreAuthorize("@ss.hasPermi('his:dispense:dispense')")
    @Log(title = "处方发药", businessType = BusinessType.UPDATE)
    @PutMapping("/dispense/{rxId}")
    public AjaxResult dispense(@PathVariable("rxId") Long rxId)
    {
        return toAjax(prescriptionService.dispense(rxId));
    }

    /** 作废处方 */
    @PreAuthorize("@ss.hasPermi('his:prescription:cancel')")
    @Log(title = "处方作废", businessType = BusinessType.UPDATE)
    @PutMapping("/cancel/{rxId}")
    public AjaxResult cancel(@PathVariable("rxId") Long rxId)
    {
        return toAjax(prescriptionService.cancel(rxId));
    }

    /** 处方明细 */
    @GetMapping("/items/{rxId}")
    public AjaxResult items(@PathVariable("rxId") Long rxId)
    {
        return success(prescriptionService.selectItemList(rxId));
    }
}

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
import com.ruoyi.his.domain.Patient;
import com.ruoyi.his.service.IPatientService;

/**
 * 患者Controller
 */
@RestController
@RequestMapping("/his/patient")
public class PatientController extends BaseController
{
    @Autowired
    private IPatientService patientService;

    /** 查询患者列表 */
    @PreAuthorize("@ss.hasPermi('his:patient:list')")
    @GetMapping("/list")
    public AjaxResult list(Patient patient)
    {
        startPage();
        List<Patient> list = patientService.selectPatientList(patient);
        return toAjaxTable(list);
    }

    /** 导出患者列表 */
    @PreAuthorize("@ss.hasPermi('his:patient:export')")
    @Log(title = "患者", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Patient patient)
    {
        List<Patient> list = patientService.selectPatientList(patient);
        ExcelUtil<Patient> util = new ExcelUtil<Patient>(Patient.class);
        util.exportExcel(response, list, "患者数据");
    }

    /** 获取患者详细信息 */
    @PreAuthorize("@ss.hasPermi('his:patient:query')")
    @GetMapping(value = "/{patientId}")
    public AjaxResult getInfo(@PathVariable("patientId") Long patientId)
    {
        return success(patientService.selectPatientByPatientId(patientId));
    }

    /** 新增患者 */
    @PreAuthorize("@ss.hasPermi('his:patient:add')")
    @Log(title = "患者", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Patient patient)
    {
        return toAjax(patientService.insertPatient(patient));
    }

    /** 修改患者 */
    @PreAuthorize("@ss.hasPermi('his:patient:edit')")
    @Log(title = "患者", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Patient patient)
    {
        return toAjax(patientService.updatePatient(patient));
    }

    /** 删除患者 */
    @PreAuthorize("@ss.hasPermi('his:patient:remove')")
    @Log(title = "患者", businessType = BusinessType.DELETE)
    @DeleteMapping("/{patientIds}")
    public AjaxResult remove(@PathVariable Long[] patientIds)
    {
        return toAjax(patientService.deletePatientByPatientIds(patientIds));
    }
}

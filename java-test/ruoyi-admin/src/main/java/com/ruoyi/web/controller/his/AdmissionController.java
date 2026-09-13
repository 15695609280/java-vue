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
import com.ruoyi.his.domain.Admission;
import com.ruoyi.his.service.IAdmissionService;

/**
 * 住院登记Controller
 */
@RestController
@RequestMapping("/his/admission")
public class AdmissionController extends BaseController
{
    @Autowired
    private IAdmissionService admissionService;

    /** 查询住院登记列表 */
    @PreAuthorize("@ss.hasPermi('his:admission:list')")
    @GetMapping("/list")
    public AjaxResult list(Admission admission)
    {
        startPage();
        List<Admission> list = admissionService.selectAdmissionList(admission);
        return toAjaxTable(list);
    }

    /** 导出住院登记列表 */
    @PreAuthorize("@ss.hasPermi('his:admission:export')")
    @Log(title = "住院登记", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Admission admission)
    {
        List<Admission> list = admissionService.selectAdmissionList(admission);
        ExcelUtil<Admission> util = new ExcelUtil<Admission>(Admission.class);
        util.exportExcel(response, list, "住院登记数据");
    }

    /** 获取住院登记详细信息 */
    @PreAuthorize("@ss.hasPermi('his:admission:query')")
    @GetMapping(value = "/{admId}")
    public AjaxResult getInfo(@PathVariable("admId") Long admId)
    {
        return success(admissionService.selectAdmissionByAdmId(admId));
    }

    /** 新增住院登记 */
    @PreAuthorize("@ss.hasPermi('his:admission:add')")
    @Log(title = "住院登记", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Admission admission)
    {
        return toAjax(admissionService.insertAdmission(admission));
    }

    /** 修改住院登记 */
    @PreAuthorize("@ss.hasPermi('his:admission:edit')")
    @Log(title = "住院登记", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Admission admission)
    {
        return toAjax(admissionService.updateAdmission(admission));
    }

    /** 删除住院登记 */
    @PreAuthorize("@ss.hasPermi('his:admission:remove')")
    @Log(title = "住院登记", businessType = BusinessType.DELETE)
    @DeleteMapping("/{admIds}")
    public AjaxResult remove(@PathVariable Long[] admIds)
    {
        return toAjax(admissionService.deleteAdmissionByAdmIds(admIds));
    }

    /** 住院登记(占用床位) */
    @PreAuthorize("@ss.hasPermi('his:admission:add') or @ss.hasPermi('his:bed:edit')")
    @Log(title = "住院登记", businessType = BusinessType.INSERT)
    @PostMapping("/admit")
    public AjaxResult admit(@RequestBody Admission admission)
    {
        Long admId = admissionService.admit(admission);
        return success(admissionService.selectAdmissionByAdmId(admId));
    }

    /** 出院(释放床位+生成床位费) */
    @PreAuthorize("@ss.hasPermi('his:discharge:settle') or @ss.hasPermi('his:admission:edit')")
    @Log(title = "出院结算", businessType = BusinessType.UPDATE)
    @PutMapping("/discharge/{admId}")
    public AjaxResult discharge(@PathVariable("admId") Long admId)
    {
        return toAjax(admissionService.discharge(admId));
    }

    /** 查询患者在院记录 */
    @GetMapping("/inHospital/{patientId}")
    public AjaxResult inHospital(@PathVariable("patientId") Long patientId)
    {
        return success(admissionService.selectInHospitalByPatient(patientId));
    }
}

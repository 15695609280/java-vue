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
import com.ruoyi.his.domain.Department;
import com.ruoyi.his.service.IDepartmentService;

/**
 * 科室Controller
 */
@RestController
@RequestMapping("/his/dept")
public class DepartmentController extends BaseController
{
    @Autowired
    private IDepartmentService departmentService;

    /** 查询科室列表 */
    @PreAuthorize("@ss.hasPermi('his:dept:list')")
    @GetMapping("/list")
    public AjaxResult list(Department department)
    {
        startPage();
        List<Department> list = departmentService.selectDepartmentList(department);
        return toAjaxTable(list);
    }

    /** 导出科室列表 */
    @PreAuthorize("@ss.hasPermi('his:dept:export')")
    @Log(title = "科室", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Department department)
    {
        List<Department> list = departmentService.selectDepartmentList(department);
        ExcelUtil<Department> util = new ExcelUtil<Department>(Department.class);
        util.exportExcel(response, list, "科室数据");
    }

    /** 获取科室详细信息 */
    @PreAuthorize("@ss.hasPermi('his:dept:query')")
    @GetMapping(value = "/{deptId}")
    public AjaxResult getInfo(@PathVariable("deptId") Long deptId)
    {
        return success(departmentService.selectDepartmentByDeptId(deptId));
    }

    /** 新增科室 */
    @PreAuthorize("@ss.hasPermi('his:dept:add')")
    @Log(title = "科室", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Department department)
    {
        return toAjax(departmentService.insertDepartment(department));
    }

    /** 修改科室 */
    @PreAuthorize("@ss.hasPermi('his:dept:edit')")
    @Log(title = "科室", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Department department)
    {
        return toAjax(departmentService.updateDepartment(department));
    }

    /** 删除科室 */
    @PreAuthorize("@ss.hasPermi('his:dept:remove')")
    @Log(title = "科室", businessType = BusinessType.DELETE)
    @DeleteMapping("/{deptIds}")
    public AjaxResult remove(@PathVariable Long[] deptIds)
    {
        return toAjax(departmentService.deleteDepartmentByDeptIds(deptIds));
    }
}

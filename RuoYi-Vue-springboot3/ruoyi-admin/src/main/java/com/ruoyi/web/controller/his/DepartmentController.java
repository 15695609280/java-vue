package com.ruoyi.web.controller.his;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.his.domain.Department;

import jakarta.servlet.http.HttpServletResponse;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;

@RestController 
@RequestMapping("/his/dept")
public class DepartmentController extends BaseController{
    @Autowired 
    private com.ruoyi.his.service.IDepartmentService departmentService;
    @PreAuthorize("@ss.hasPermi('his:dept:list')")
    @RequestMapping("/list")
    public TableDataInfo list(Department department){
	 startPage();
         
          java.util.List<Department> list = departmentService.selectDepartmentList(department);
          return getDataTable(list);
     }


     @PreAuthorize ("@ss.hasPermi('his:dept:export')")
     @Log(title = "科室", businessType = BusinessType.EXPORT)
     @RequestMapping("/export")
    public void export(HttpServletResponse response ,Department department){
        List<Department> list = departmentService.selectDepartmentList(department);
        ExcelUtil<Department> util = new ExcelUtil<Department>(Department.class);
        util.exportExcel(response, list, "科室数据");
     }
}

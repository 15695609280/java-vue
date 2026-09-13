package com.ruoyi.his.service;

import com.ruoyi.his.domain.Department;
import java.util.List;

/**
 * 科室Service接口
 */
public interface IDepartmentService
{
    public Department selectDepartmentByDeptId(Long deptId);

    public List<Department> selectDepartmentList(Department department);

    public int insertDepartment(Department department);

    public int updateDepartment(Department department);

    public int deleteDepartmentByDeptIds(Long[] deptIds);

    public int deleteDepartmentByDeptId(Long deptId);
}

package com.ruoyi.his.mapper;

import com.ruoyi.his.domain.Department;
import java.util.List;

/**
 * 科室Mapper接口
 */
public interface DepartmentMapper
{
    public Department selectDepartmentByDeptId(Long deptId);

    public List<Department> selectDepartmentList(Department department);

    public int insertDepartment(Department department);

    public int updateDepartment(Department department);

    public int deleteDepartmentByDeptId(Long deptId);

    public int deleteDepartmentByDeptIds(Long[] deptIds);
}

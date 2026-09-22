package com.ruoyi.his.mapper;

import java.util.List;

import com.ruoyi.his.domain.Department;

/**
 * 科室管理 数据层
 */
public interface DepartmentMapper {

    public Department selectDepartmentByDeptId(Long deptId);

    public List<Department> selectDepartmentList(Department department);

    public int insertDepartment(Department department);

    public int updateDepartment(Department department);

    public int deleteDepartmentByIds(String[] ids);

    public int deleteDepartmentById(Long deptId);

    public int deleteDepartmentByDeptIds(Long[] deptIds);

    public int deleteDepartmentByDeptId(Long deptId);
}

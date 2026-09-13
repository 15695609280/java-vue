package com.ruoyi.his.service.impl;

import com.ruoyi.his.domain.Department;
import com.ruoyi.his.mapper.DepartmentMapper;
import com.ruoyi.his.service.IDepartmentService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 科室Service业务层处理
 */
@Service
public class DepartmentServiceImpl implements IDepartmentService
{
    @Autowired
    private DepartmentMapper departmentMapper;

    @Override
    public Department selectDepartmentByDeptId(Long deptId)
    {
        return departmentMapper.selectDepartmentByDeptId(deptId);
    }

    @Override
    public List<Department> selectDepartmentList(Department department)
    {
        return departmentMapper.selectDepartmentList(department);
    }

    @Override
    public int insertDepartment(Department department)
    {
        department.setCreateTime(com.ruoyi.common.utils.DateUtils.getNowDate());
        int rows = departmentMapper.insertDepartment(department);
        return rows;
    }

    @Override
    public int updateDepartment(Department department)
    {
        department.setUpdateTime(com.ruoyi.common.utils.DateUtils.getNowDate());
        return departmentMapper.updateDepartment(department);
    }

    @Override
    public int deleteDepartmentByDeptIds(Long[] deptIds)
    {
        return departmentMapper.deleteDepartmentByDeptIds(deptIds);
    }

    @Override
    public int deleteDepartmentByDeptId(Long deptId)
    {
        return departmentMapper.deleteDepartmentByDeptId(deptId);
    }
}

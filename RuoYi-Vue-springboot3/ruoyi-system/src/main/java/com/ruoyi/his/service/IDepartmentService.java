package com.ruoyi.his.service;

import java.util.List;

import com.ruoyi.his.domain.Department;

public interface IDepartmentService {
   public Department selectDepartmentByDeptId(Long deptId);

   public List<Department> selectDepartmentList(Department department);

   public int insertDepartment(Department department);

   public int updateDepartment(Department department);

   public int deleteDepartmentByIds(Long[] ids);

   public int deleteDepartmentById(Long deptId);

   int deleteDepartmentByIds(String[] ids);

   int deleteDepartmentByDeptIds(Long[] deptIds);

   int deleteDepartmentByDeptId(Long deptId);


    
}  

package com.ruoyi.his.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

public class Department extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private Long deptId;
    private Long parentId;
    
    @Excel (name = "科室名称")
    private String deptName;

    @Excel (name="科室编码")
    private String deptCode;

    @Excel (name= "科室类型")
    private String deptType;
       /** 位置 */
    private String location;

    /** 联系电话 */
    private String phone;

    /** 科室简介 */
    private String intro;

    /** 排序 */
    private Integer orderNum;

    /** 状态 */
    private String status;

    public void setDeptId(Long deptId){this.deptId = deptId;}
    public Long getDebtId(){return deptId;}

    public void setParentId(Long parentId){this.parentId = parentId;}
    public Long getParentId(){return parentId;}

    public void setDeptName(String deptName){this.deptName = deptName;}

    public String getDeptName(){return deptName;}

     public void setDeptCode(String deptCode) { this.deptCode = deptCode; }
    public String getDeptCode() { return deptCode; }

    public void setDeptType(String deptType) { this.deptType = deptType; }
    public String getDeptType() { return deptType; }

    public void setLocation(String location) { this.location = location; }
    public String getLocation() { return location; }

    public void setPhone(String phone) { this.phone = phone; }
    public String getPhone() { return phone; }

    public void setIntro(String intro) { this.intro = intro; }
    public String getIntro() { return intro; }

    public void setOrderNum(Integer orderNum) { this.orderNum = orderNum; }
    public Integer getOrderNum() { return orderNum; }

    public void setStatus(String status) { this.status = status; }
    public String getStatus() { return status; }

}

package com.ruoyi.his.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 病区对象 his_ward
 */
public class Ward extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 病区ID */
    private Long wardId;

    /** 病区名称 */
    @Excel(name = "病区名称")
    private String wardName;

    /** 所属科室 */
    private Long deptId;

    /** 所属科室 */
    private String deptName;

    /** 位置 */
    private String location;

    /** 护士人数 */
    private Integer nurseCount;

    /** 状态 */
    private String status;


    public void setWardId(Long wardId) { this.wardId = wardId; }
    public Long getWardId() { return wardId; }

    public void setWardName(String wardName) { this.wardName = wardName; }
    public String getWardName() { return wardName; }

    public void setDeptId(Long deptId) { this.deptId = deptId; }
    public Long getDeptId() { return deptId; }

    public void setDeptName(String deptName) { this.deptName = deptName; }
    public String getDeptName() { return deptName; }

    public void setLocation(String location) { this.location = location; }
    public String getLocation() { return location; }

    public void setNurseCount(Integer nurseCount) { this.nurseCount = nurseCount; }
    public Integer getNurseCount() { return nurseCount; }

    public void setStatus(String status) { this.status = status; }
    public String getStatus() { return status; }
}

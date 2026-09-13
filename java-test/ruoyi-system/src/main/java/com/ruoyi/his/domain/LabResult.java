package com.ruoyi.his.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 检验结果对象 his_lab_result
 */
public class LabResult extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 结果ID */
    private Long resultId;

    /** 检验ID */
    private Long testId;

    /** 项目名称 */
    private String itemName;

    /** 结果值 */
    private String resultValue;

    /** 单位 */
    private String unit;

    /** 参考范围 */
    private String refRange;

    /** 结果标识 */
    private String flag;


    public void setResultId(Long resultId) { this.resultId = resultId; }
    public Long getResultId() { return resultId; }

    public void setTestId(Long testId) { this.testId = testId; }
    public Long getTestId() { return testId; }

    public void setItemName(String itemName) { this.itemName = itemName; }
    public String getItemName() { return itemName; }

    public void setResultValue(String resultValue) { this.resultValue = resultValue; }
    public String getResultValue() { return resultValue; }

    public void setUnit(String unit) { this.unit = unit; }
    public String getUnit() { return unit; }

    public void setRefRange(String refRange) { this.refRange = refRange; }
    public String getRefRange() { return refRange; }

    public void setFlag(String flag) { this.flag = flag; }
    public String getFlag() { return flag; }
}

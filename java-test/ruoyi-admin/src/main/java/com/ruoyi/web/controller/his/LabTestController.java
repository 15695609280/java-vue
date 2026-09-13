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
import com.ruoyi.his.domain.LabTest;
import com.ruoyi.his.service.ILabTestService;

/**
 * 检验单Controller
 */
@RestController
@RequestMapping("/his/lab")
public class LabTestController extends BaseController
{
    @Autowired
    private ILabTestService labTestService;

    /** 查询检验单列表 */
    @PreAuthorize("@ss.hasPermi('his:lab:list')")
    @GetMapping("/list")
    public AjaxResult list(LabTest labTest)
    {
        startPage();
        List<LabTest> list = labTestService.selectLabTestList(labTest);
        return toAjaxTable(list);
    }

    /** 导出检验单列表 */
    @PreAuthorize("@ss.hasPermi('his:lab:export')")
    @Log(title = "检验单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, LabTest labTest)
    {
        List<LabTest> list = labTestService.selectLabTestList(labTest);
        ExcelUtil<LabTest> util = new ExcelUtil<LabTest>(LabTest.class);
        util.exportExcel(response, list, "检验单数据");
    }

    /** 获取检验单详细信息 */
    @PreAuthorize("@ss.hasPermi('his:lab:query')")
    @GetMapping(value = "/{testId}")
    public AjaxResult getInfo(@PathVariable("testId") Long testId)
    {
        return success(labTestService.selectLabTestByTestId(testId));
    }

    /** 新增检验单 */
    @PreAuthorize("@ss.hasPermi('his:lab:add')")
    @Log(title = "检验单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LabTest labTest)
    {
        return toAjax(labTestService.insertLabTest(labTest));
    }

    /** 修改检验单 */
    @PreAuthorize("@ss.hasPermi('his:lab:edit')")
    @Log(title = "检验单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LabTest labTest)
    {
        return toAjax(labTestService.updateLabTest(labTest));
    }

    /** 删除检验单 */
    @PreAuthorize("@ss.hasPermi('his:lab:remove')")
    @Log(title = "检验单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{testIds}")
    public AjaxResult remove(@PathVariable Long[] testIds)
    {
        return toAjax(labTestService.deleteLabTestByTestIds(testIds));
    }

    /** 检验状态流转 */
    @PreAuthorize("@ss.hasPermi('his:lab:flow')")
    @Log(title = "检验流程", businessType = BusinessType.UPDATE)
    @PutMapping("/flow/{testId}/{status}")
    public AjaxResult flow(@PathVariable("testId") Long testId, @PathVariable("status") String status)
    {
        return toAjax(labTestService.flow(testId, status));
    }

    /** 出报告(含结果明细) */
    @PreAuthorize("@ss.hasPermi('his:lab:flow')")
    @Log(title = "检验报告", businessType = BusinessType.UPDATE)
    @PutMapping("/report")
    public AjaxResult report(@RequestBody LabTest labTest)
    {
        return toAjax(labTestService.report(labTest));
    }

    /** 检验结果明细 */
    @GetMapping("/results/{testId}")
    public AjaxResult results(@PathVariable("testId") Long testId)
    {
        return success(labTestService.selectResultList(testId));
    }
}

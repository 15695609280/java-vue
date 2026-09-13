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
import com.ruoyi.his.domain.Exam;
import com.ruoyi.his.service.IExamService;

/**
 * 检查单Controller
 */
@RestController
@RequestMapping("/his/exam")
public class ExamController extends BaseController
{
    @Autowired
    private IExamService examService;

    /** 查询检查单列表 */
    @PreAuthorize("@ss.hasPermi('his:exam:list')")
    @GetMapping("/list")
    public AjaxResult list(Exam exam)
    {
        startPage();
        List<Exam> list = examService.selectExamList(exam);
        return toAjaxTable(list);
    }

    /** 导出检查单列表 */
    @PreAuthorize("@ss.hasPermi('his:exam:export')")
    @Log(title = "检查单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Exam exam)
    {
        List<Exam> list = examService.selectExamList(exam);
        ExcelUtil<Exam> util = new ExcelUtil<Exam>(Exam.class);
        util.exportExcel(response, list, "检查单数据");
    }

    /** 获取检查单详细信息 */
    @PreAuthorize("@ss.hasPermi('his:exam:query')")
    @GetMapping(value = "/{examId}")
    public AjaxResult getInfo(@PathVariable("examId") Long examId)
    {
        return success(examService.selectExamByExamId(examId));
    }

    /** 新增检查单 */
    @PreAuthorize("@ss.hasPermi('his:exam:add')")
    @Log(title = "检查单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Exam exam)
    {
        return toAjax(examService.insertExam(exam));
    }

    /** 修改检查单 */
    @PreAuthorize("@ss.hasPermi('his:exam:edit')")
    @Log(title = "检查单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Exam exam)
    {
        return toAjax(examService.updateExam(exam));
    }

    /** 删除检查单 */
    @PreAuthorize("@ss.hasPermi('his:exam:remove')")
    @Log(title = "检查单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{examIds}")
    public AjaxResult remove(@PathVariable Long[] examIds)
    {
        return toAjax(examService.deleteExamByExamIds(examIds));
    }

    /** 检查状态流转 */
    @PreAuthorize("@ss.hasPermi('his:exam:flow')")
    @Log(title = "检查流程", businessType = BusinessType.UPDATE)
    @PutMapping("/flow/{examId}/{status}")
    public AjaxResult flow(@PathVariable("examId") Long examId, @PathVariable("status") String status)
    {
        return toAjax(examService.flow(examId, status));
    }

    /** 出报告 */
    @PreAuthorize("@ss.hasPermi('his:exam:flow')")
    @Log(title = "检查报告", businessType = BusinessType.UPDATE)
    @PutMapping("/report")
    public AjaxResult report(@RequestBody Exam exam)
    {
        return toAjax(examService.report(exam));
    }
}

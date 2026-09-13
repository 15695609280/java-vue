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
import com.ruoyi.his.domain.StockRecord;
import com.ruoyi.his.service.IStockRecordService;

/**
 * 出入库记录Controller
 */
@RestController
@RequestMapping("/his/stockrecord")
public class StockRecordController extends BaseController
{
    @Autowired
    private IStockRecordService stockRecordService;

    /** 查询出入库记录列表 */
    @PreAuthorize("@ss.hasPermi('his:stockrecord:list')")
    @GetMapping("/list")
    public AjaxResult list(StockRecord stockRecord)
    {
        startPage();
        List<StockRecord> list = stockRecordService.selectStockRecordList(stockRecord);
        return toAjaxTable(list);
    }

    /** 导出出入库记录列表 */
    @PreAuthorize("@ss.hasPermi('his:stockrecord:export')")
    @Log(title = "出入库记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, StockRecord stockRecord)
    {
        List<StockRecord> list = stockRecordService.selectStockRecordList(stockRecord);
        ExcelUtil<StockRecord> util = new ExcelUtil<StockRecord>(StockRecord.class);
        util.exportExcel(response, list, "出入库记录数据");
    }

    /** 获取出入库记录详细信息 */
    @PreAuthorize("@ss.hasPermi('his:stockrecord:query')")
    @GetMapping(value = "/{recordId}")
    public AjaxResult getInfo(@PathVariable("recordId") Long recordId)
    {
        return success(stockRecordService.selectStockRecordByRecordId(recordId));
    }

    /** 新增出入库记录 */
    @PreAuthorize("@ss.hasPermi('his:stockrecord:add')")
    @Log(title = "出入库记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody StockRecord stockRecord)
    {
        return toAjax(stockRecordService.insertStockRecord(stockRecord));
    }

    /** 修改出入库记录 */
    @PreAuthorize("@ss.hasPermi('his:stockrecord:edit')")
    @Log(title = "出入库记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody StockRecord stockRecord)
    {
        return toAjax(stockRecordService.updateStockRecord(stockRecord));
    }

    /** 删除出入库记录 */
    @PreAuthorize("@ss.hasPermi('his:stockrecord:remove')")
    @Log(title = "出入库记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{recordIds}")
    public AjaxResult remove(@PathVariable Long[] recordIds)
    {
        return toAjax(stockRecordService.deleteStockRecordByRecordIds(recordIds));
    }
}

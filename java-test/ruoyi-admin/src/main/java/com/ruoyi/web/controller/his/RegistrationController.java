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
import com.ruoyi.his.domain.Registration;
import com.ruoyi.his.service.IRegistrationService;

/**
 * 挂号Controller
 */
@RestController
@RequestMapping("/his/registration")
public class RegistrationController extends BaseController
{
    @Autowired
    private IRegistrationService registrationService;

    /** 查询挂号列表 */
    @PreAuthorize("@ss.hasPermi('his:registration:list')")
    @GetMapping("/list")
    public AjaxResult list(Registration registration)
    {
        startPage();
        List<Registration> list = registrationService.selectRegistrationList(registration);
        return toAjaxTable(list);
    }

    /** 导出挂号列表 */
    @PreAuthorize("@ss.hasPermi('his:registration:export')")
    @Log(title = "挂号", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Registration registration)
    {
        List<Registration> list = registrationService.selectRegistrationList(registration);
        ExcelUtil<Registration> util = new ExcelUtil<Registration>(Registration.class);
        util.exportExcel(response, list, "挂号数据");
    }

    /** 获取挂号详细信息 */
    @PreAuthorize("@ss.hasPermi('his:registration:query')")
    @GetMapping(value = "/{regId}")
    public AjaxResult getInfo(@PathVariable("regId") Long regId)
    {
        return success(registrationService.selectRegistrationByRegId(regId));
    }

    /** 新增挂号 */
    @PreAuthorize("@ss.hasPermi('his:registration:add')")
    @Log(title = "挂号", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Registration registration)
    {
        return toAjax(registrationService.insertRegistration(registration));
    }

    /** 修改挂号 */
    @PreAuthorize("@ss.hasPermi('his:registration:edit')")
    @Log(title = "挂号", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Registration registration)
    {
        return toAjax(registrationService.updateRegistration(registration));
    }

    /** 删除挂号 */
    @PreAuthorize("@ss.hasPermi('his:registration:remove')")
    @Log(title = "挂号", businessType = BusinessType.DELETE)
    @DeleteMapping("/{regIds}")
    public AjaxResult remove(@PathVariable Long[] regIds)
    {
        return toAjax(registrationService.deleteRegistrationByRegIds(regIds));
    }

    /** 挂号 */
    @PreAuthorize("@ss.hasPermi('his:registration:add')")
    @Log(title = "挂号", businessType = BusinessType.INSERT)
    @PostMapping("/register")
    public AjaxResult register(@RequestBody Registration registration)
    {
        Long regId = registrationService.register(registration);
        return success(registrationService.selectRegistrationByRegId(regId));
    }

    /** 状态流转: 1叫号就诊 3退号 4过号 */
    @PreAuthorize("@ss.hasPermi('his:registration:refund') or @ss.hasPermi('his:queue:call') or @ss.hasPermi('his:workstation:visit')")
    @Log(title = "挂号状态流转", businessType = BusinessType.UPDATE)
    @PutMapping("/status/{regId}/{status}")
    public AjaxResult changeStatus(@PathVariable("regId") Long regId, @PathVariable("status") String status)
    {
        return toAjax(registrationService.changeStatus(regId, status));
    }

    /** 候诊队列 */
    @GetMapping("/queue")
    public AjaxResult queue(Registration registration)
    {
        List<Registration> list = registrationService.selectQueueList(registration);
        return toAjaxTable(list);
    }
}

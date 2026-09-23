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
import com.ruoyi.his.domain.Department;
import com.ruoyi.his.service.IDepartmentService;

/**
 * 科室管理 Controller
 *
 * <p>HIS 基础数据模块的 HTTP 入口，对应前端科室页、以及医生/入院/手术等页面的科室下拉数据源。
 * 本类只负责接请求、鉴权、记日志、调用 Service，不写 SQL。
 *
 * <ul>
 *   <li>基础路径：{@code /his/dept}（前端 axios 一般再拼网关前缀，如 {@code /dev-api/his/dept}）</li>
 *   <li>数据表：{@code his_department}，实体：{@link Department}</li>
 *   <li>权限字统一前缀：{@code his:dept:*}，需在系统菜单中配置后才会放行</li>
 * </ul>
 */
@RestController
@RequestMapping("/his/dept")
public class DepartmentController extends BaseController
{
    /** 科室业务层：真正的增删改查、补创建/更新时间都在这里完成 */
    @Autowired
    private IDepartmentService departmentService;

    /**
     * 分页查询科室列表
     *
     * <p>前端：{@code GET /his/dept/list?pageNum=1&pageSize=10&deptName=内科}
     * <p>入参是 Query 参数（不是 JSON）。{@code pageNum}/{@code pageSize} 由 {@link #startPage()} 读取；
     * {@code deptName} 等字段绑定到 {@link Department}，当前 SQL 仅按科室名称模糊查询。
     *
     * @param department 查询条件，常用字段：deptName（模糊）
     * @return {@code { code, msg, data: 当前页列表, total: 总条数 }}
     */
    @PreAuthorize("@ss.hasPermi('his:dept:list')")
    @GetMapping("/list")
    public AjaxResult list(Department department)
    {
        // 从请求中取出 pageNum、pageSize，交给 PageHelper 做分页
        startPage();
        List<Department> list = departmentService.selectDepartmentList(department);
        // data=本页数据，total=符合条件的总条数（供表格分页使用）
        return toAjaxTable(list);
    }

    /**
     * 导出科室 Excel
     *
     * <p>前端：{@code POST /his/dept/export}，筛选条件与列表接口相同，但<strong>不分页</strong>，导出当前条件下的全部数据。
     * 列名来自 {@link Department} 上的 {@code @Excel} 注解（科室名称、编码、类型）。
     * 文件流直接写入 response，浏览器会下载「科室数据.xlsx」。
     *
     * @param response   HTTP 响应，用于写出 Excel 二进制流
     * @param department 导出筛选条件，可为空表示全部
     */
    @PreAuthorize("@ss.hasPermi('his:dept:export')")
    @Log(title = "科室", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Department department)
    {
        // 导出不去分页，查出筛选条件下的全量数据
        List<Department> list = departmentService.selectDepartmentList(department);
        ExcelUtil<Department> util = new ExcelUtil<Department>(Department.class);
        util.exportExcel(response, list, "科室数据");
    }

    /**
     * 按主键查询单条科室（编辑弹窗回填表单时使用）
     *
     * <p>前端：{@code GET /his/dept/{deptId}}，例如 {@code GET /his/dept/3}
     *
     * @param deptId 科室主键
     * @return {@code { code, msg, data: 科室对象 }}，查不到时 data 为 null
     */
    @PreAuthorize("@ss.hasPermi('his:dept:query')")
    @GetMapping(value = "/{deptId}")
    public AjaxResult getInfo(@PathVariable("deptId") Long deptId)
    {
        return success(departmentService.selectDepartmentByDeptId(deptId));
    }

    /**
     * 新增科室
     *
     * <p>前端：{@code POST /his/dept}，Body 为 JSON。
     * 必填建议：deptName、deptCode、deptType；status 一般传 {@code "0"}（正常）。
     * Service 会自动写入 createTime，数据库自增回填 deptId。
     *
     * @param department 科室表单，JSON 请求体
     * @return 影响行数 &gt; 0 为成功，否则失败
     */
    @PreAuthorize("@ss.hasPermi('his:dept:add')")
    @Log(title = "科室", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Department department)
    {
        return toAjax(departmentService.insertDepartment(department));
    }

    /**
     * 修改科室
     *
     * <p>前端：{@code PUT /his/dept}，Body 为 JSON，必须带 {@code deptId}。
     * Service 会自动写入 updateTime。Mapper 对空字符串字段不会更新（动态 SQL）。
     *
     * @param department 待更新的科室对象，必须包含 deptId
     * @return 影响行数 &gt; 0 为成功，否则失败
     */
    @PreAuthorize("@ss.hasPermi('his:dept:edit')")
    @Log(title = "科室", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Department department)
    {
        return toAjax(departmentService.updateDepartment(department));
    }

    /**
     * 删除科室（支持单个或批量，物理删除）
     *
     * <p>前端：{@code DELETE /his/dept/{deptIds}}
     * <ul>
     *   <li>单个：{@code DELETE /his/dept/3}</li>
     *   <li>批量：{@code DELETE /his/dept/1,2,3}，逗号分隔，Spring 会拆成数组</li>
     * </ul>
     * 注意：当前实现不会校验该科室下是否还有医生、病区、床位，删除后关联数据可能变成孤儿。
     *
     * @param deptIds 一个或多个科室主键
     * @return 影响行数 &gt; 0 为成功，否则失败
     */
    @PreAuthorize("@ss.hasPermi('his:dept:remove')")
    @Log(title = "科室", businessType = BusinessType.DELETE)
    @DeleteMapping("/{deptIds}")
    public AjaxResult remove(@PathVariable Long[] deptIds)
    {
        return toAjax(departmentService.deleteDepartmentByDeptIds(deptIds));
    }
}

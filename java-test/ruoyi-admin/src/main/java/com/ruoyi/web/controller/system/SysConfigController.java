package com.ruoyi.web.controller.system;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.service.ISysConfigService;

/**
 * 参数配置控制器。
 *
 * <p>该控制器负责处理系统参数配置相关的 HTTP 请求，包括参数列表查询、
 * 参数导出、单条参数查询、新增、修改、删除以及参数缓存刷新。</p>
 *
 * <p>控制器本身主要负责请求接收、权限校验、分页处理和统一响应封装，
 * 具体的数据查询、保存、删除及缓存操作由 {@link ISysConfigService} 完成。</p>
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/config")
public class SysConfigController extends BaseController
{
    /**
     * 参数配置业务服务。
     *
     * <p>通过 Spring 自动注入服务接口的实现类，控制器不直接操作数据库，
     * 以便分离 Web 层和业务层职责。</p>
     */
    @Autowired
    private ISysConfigService configService;

    /**
     * 获取参数配置分页列表。
     *
     * <p>前端传入的查询条件会自动封装到 {@link SysConfig} 对象中。
     * {@link #startPage()} 会根据请求中的 pageNum、pageSize 等参数启动分页，
     * 服务层查询到的结果随后通过 {@link #getDataTable(List)} 封装成统一的分页响应。</p>
     *
     * @param config 参数配置查询条件，例如参数名称、参数键名和参数类型
     * @return 包含总记录数和当前页数据的表格分页结果
     */
    @PreAuthorize("@ss.hasPermi('system:config:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysConfig config)
    {
        // 初始化分页参数，后续服务层查询会被分页插件拦截。
        startPage();
        List<SysConfig> list = configService.selectConfigList(config);
        // 将查询结果转换为前端表格组件所需的统一响应结构。
        return getDataTable(list);
    }

    /**
     * 导出参数配置数据。
     *
     * <p>根据请求中的查询条件查询参数列表，并使用 ExcelUtil 生成 Excel 文件
     * 写入 HTTP 响应流，供浏览器下载。导出操作会记录业务日志。</p>
     *
     * @param response HTTP 响应对象，用于写入 Excel 文件内容
     * @param config 参数配置查询条件
     */
    @Log(title = "参数管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:config:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysConfig config)
    {
        List<SysConfig> list = configService.selectConfigList(config);
        // 根据 SysConfig 的字段和注解生成 Excel 列，并将文件写入响应。
        ExcelUtil<SysConfig> util = new ExcelUtil<SysConfig>(SysConfig.class);
        util.exportExcel(response, list, "参数数据");
    }

    /**
     * 根据参数编号获取参数详细信息。
     *
     * <p>路径中的 configId 会被 Spring MVC 转换为 Long，并交由服务层查询。
     * 查询结果通过 BaseController.success 统一包装后返回。</p>
     *
     * @param configId 参数主键编号
     * @return 参数详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:config:query')")
    @GetMapping(value = "/{configId}")
    public AjaxResult getInfo(@PathVariable Long configId)
    {
        return success(configService.selectConfigById(configId));
    }

    /**
     * 根据参数键名查询参数值。
     *
     * <p>该接口通常用于业务模块按固定键名读取系统参数。与后台管理页面的
     * 详情查询不同，本接口只需要参数键名即可获取对应的参数值。</p>
     *
     * @param configKey 参数键名
     * @return 参数值；具体返回结构由统一响应对象封装
     */
    @GetMapping(value = "/configKey/{configKey}")
    public AjaxResult getConfigKey(@PathVariable String configKey)
    {
        return success(configService.selectConfigByKey(configKey));
    }

    /**
     * 新增参数配置。
     *
     * <p>{@code @Validated} 会触发 {@link SysConfig} 上声明的参数校验，
     * {@code @RequestBody} 则将请求体 JSON 转换为参数配置对象。保存前先校验
     * 参数键名唯一性，避免同一个键名对应多条配置；创建人从当前登录用户中获取，
     * 不信任前端直接传入的创建人信息。</p>
     *
     * @param config 待新增的参数配置
     * @return 新增操作结果
     */
    @PreAuthorize("@ss.hasPermi('system:config:add')")
    @Log(title = "参数管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysConfig config)
    {
        // 参数键名通常作为业务查询条件，必须保证其唯一。
        if (!configService.checkConfigKeyUnique(config))
        {
            return error("新增参数'" + config.getConfigName() + "'失败，参数键名已存在");
        }
        // 创建人由服务端根据当前会话确定，防止客户端伪造操作人。
        config.setCreateBy(getUsername());
        return toAjax(configService.insertConfig(config));
    }

    /**
     * 修改参数配置。
     *
     * <p>修改流程与新增类似，同样需要校验参数键名唯一性。唯一性校验会排除
     * 当前正在修改的记录，因此允许参数记录保留原有键名。更新人由当前登录用户
     * 自动填充，最后将业务层返回的影响行数转换为统一响应。</p>
     *
     * @param config 包含主键和修改后内容的参数配置
     * @return 修改操作结果
     */
    @PreAuthorize("@ss.hasPermi('system:config:edit')")
    @Log(title = "参数管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysConfig config)
    {
        // 防止修改后与其他参数记录产生重复键名。
        if (!configService.checkConfigKeyUnique(config))
        {
            return error("修改参数'" + config.getConfigName() + "'失败，参数键名已存在");
        }
        // 更新人由服务端设置，确保审计信息来自当前登录用户。
        config.setUpdateBy(getUsername());
        return toAjax(configService.updateConfig(config));
    }

    /**
     * 批量删除参数配置。
     *
     * <p>请求路径中的逗号分隔 ID 会由 Spring MVC 转换为 {@code Long[]}，
     * 服务层负责执行批量删除以及相关的缓存处理。</p>
     *
     * @param configIds 待删除的参数主键数组
     * @return 删除操作结果
     */
    @PreAuthorize("@ss.hasPermi('system:config:remove')")
    @Log(title = "参数管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{configIds}")
    public AjaxResult remove(@PathVariable Long[] configIds)
    {
        configService.deleteConfigByIds(configIds);
        return success();
    }

    /**
     * 刷新参数缓存。
     *
     * <p>当参数数据在数据库中发生变更，或管理员希望立即使缓存与数据库同步时，
     * 可调用该接口重置参数缓存。接口复用了参数删除权限，避免普通用户执行缓存
     * 管理操作。</p>
     *
     * @return 缓存刷新操作结果
     */
    @PreAuthorize("@ss.hasPermi('system:config:remove')")
    @Log(title = "参数管理", businessType = BusinessType.CLEAN)
    @DeleteMapping("/refreshCache")
    public AjaxResult refreshCache()
    {
        configService.resetConfigCache();
        return success();
    }
}

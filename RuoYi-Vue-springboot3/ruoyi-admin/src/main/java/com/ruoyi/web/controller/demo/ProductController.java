package com.ruoyi.web.controller.demo;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.Product;
import com.ruoyi.system.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 商品Controller
 *
 * 【练习目标】掌握 Controller 层编写（最核心的练习）
 * - 继承 BaseController（含 startPage() 分页、getDataTable() 封装）
 * - @RestController = @Controller + @ResponseBody
 * - @RequestMapping("/demo/product") 定义基础路径
 * - @PreAuthorize 权限控制（对应菜单权限标识）
 * - @Log 操作日志注解（businessType = 增/删/改/导出）
 * - GET 查询列表/详情、POST 新增、PUT 修改、DELETE 删除
 *
 * 【对着敲练习】参考原项目 ruoyi-admin/web/controller/system/SysPostController.java
 * 对照着把每个方法敲一遍，理解每一行的作用
 */
@RestController
@RequestMapping("/demo/product")
public class ProductController extends BaseController
{
    @Autowired
    private IProductService productService;

    /**
     * 查询商品列表
     * 权限标识：demo:product:list
     */
    @PreAuthorize("@ss.hasPermi('demo:product:list')")
    @GetMapping("/list")
    public TableDataInfo list(Product product)
    {
        // startPage() 来自 BaseController，配合 PageHelper 自动分页
        startPage();
        List<Product> list = productService.selectProductList(product);
        // getDataTable() 封装成 TableDataInfo（含 total、rows、code、msg）
        return getDataTable(list);
    }

    /**
     * 导出商品列表
     */
    @PreAuthorize("@ss.hasPermi('demo:product:export')")
    @Log(title = "商品", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Product product)
    {
        List<Product> list = productService.selectProductList(product);
        ExcelUtil<Product> util = new ExcelUtil<Product>(Product.class);
        util.exportExcel(response, list, "商品数据");
    }

    /**
     * 获取商品详细信息
     */
    @PreAuthorize("@ss.hasPermi('demo:product:query')")
    @GetMapping(value = "/{productId}")
    public AjaxResult getInfo(@PathVariable("productId") Long productId)
    {
        return success(productService.selectProductById(productId));
    }

    /**
     * 新增商品
     */
    @PreAuthorize("@ss.hasPermi('demo:product:add')")
    @Log(title = "商品", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Product product)
    {
        return toAjax(productService.insertProduct(product));
    }

    /**
     * 修改商品
     */
    @PreAuthorize("@ss.hasPermi('demo:product:edit')")
    @Log(title = "商品", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Product product)
    {
        return toAjax(productService.updateProduct(product));
    }

    /**
     * 删除商品
     */
    @PreAuthorize("@ss.hasPermi('demo:product:remove')")
    @Log(title = "商品", businessType = BusinessType.DELETE)
    @DeleteMapping("/{productIds}")
    public AjaxResult remove(@PathVariable Long[] productIds)
    {
        return toAjax(productService.deleteProductByIds(productIds));
    }
}

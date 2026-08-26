package com.ruoyi.system.service;

import com.ruoyi.system.domain.Product;
import java.util.List;

/**
 * 商品Service接口
 *
 * 【练习目标】掌握 Service 层接口设计
 * - 接口定义业务方法，Impl 中实现
 * - 方法命名：selectXxx / insertXxx / updateXxx / deleteXxx
 * - 分页查询返回 List，配合 Controller 中的 startPage()
 *
 * 【对着敲练习】参考 ruoyi-system/service/ISysPostService.java
 */
public interface IProductService
{
    /**
     * 查询商品
     */
    public Product selectProductById(Long productId);

    /**
     * 查询商品列表
     */
    public List<Product> selectProductList(Product product);

    /**
     * 新增商品
     */
    public int insertProduct(Product product);

    /**
     * 修改商品
     */
    public int updateProduct(Product product);

    /**
     * 批量删除商品
     */
    public int deleteProductByIds(Long[] productIds);

    /**
     * 删除商品信息
     */
    public int deleteProductById(Long productId);
}

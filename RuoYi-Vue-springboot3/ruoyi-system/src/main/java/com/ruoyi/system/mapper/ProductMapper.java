package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.Product;
import java.util.List;

/**
 * 商品Mapper接口
 *
 * 【练习目标】掌握 Mapper 接口编写规范
 * - 接口方法名与 XML 中 SQL 的 id 一一对应
 * - 查询返回 List<T> 或 T
 * - 增删改返回 int（影响行数）
 *
 * 【对着敲练习】参考 ruoyi-system/mapper/SysPostMapper.java
 */
public interface ProductMapper
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
     * 删除商品
     */
    public int deleteProductById(Long productId);

    /**
     * 批量删除商品
     */
    public int deleteProductByIds(Long[] productIds);
}

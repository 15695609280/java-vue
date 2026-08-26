package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.Product;
import com.ruoyi.system.mapper.ProductMapper;
import com.ruoyi.system.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品Service业务层处理
 *
 * 【练习目标】掌握 Service 实现类编写
 * - @Service 注解标记为 Spring Bean
 * - @Autowired 注入 Mapper
 * - 方法体调用 Mapper 对应方法
 * - 复杂业务逻辑写在这里（如校验、事务）
 *
 * 【对着敲练习】参考 ruoyi-system/service/impl/SysPostServiceImpl.java
 * 把下面每个方法的 TODO 替换为真实的 Mapper 调用
 */
@Service
public class ProductServiceImpl implements IProductService
{
    @Autowired
    private ProductMapper productMapper;

    /**
     * 查询商品
     */
    @Override
    public Product selectProductById(Long productId)
    {
        // TODO: 调用 productMapper.selectProductById(productId)
        return productMapper.selectProductById(productId);
    }

    /**
     * 查询商品列表
     */
    @Override
    public List<Product> selectProductList(Product product)
    {
        // TODO: 调用 productMapper.selectProductList(product)
        return productMapper.selectProductList(product);
    }

    /**
     * 新增商品
     */
    @Override
    public int insertProduct(Product product)
    {
        // TODO: 调用 productMapper.insertProduct(product)
        return productMapper.insertProduct(product);
    }

    /**
     * 修改商品
     */
    @Override
    public int updateProduct(Product product)
    {
        // TODO: 调用 productMapper.updateProduct(product)
        return productMapper.updateProduct(product);
    }

    /**
     * 批量删除商品
     */
    @Override
    public int deleteProductByIds(Long[] productIds)
    {
        // TODO: 调用 productMapper.deleteProductByIds(productIds)
        return productMapper.deleteProductByIds(productIds);
    }

    /**
     * 删除商品信息
     */
    @Override
    public int deleteProductById(Long productId)
    {
        // TODO: 调用 productMapper.deleteProductById(productId)
        return productMapper.deleteProductById(productId);
    }
}

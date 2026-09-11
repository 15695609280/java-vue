package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.Product;
import com.ruoyi.system.mapper.ProductMapper;
import com.ruoyi.system.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements IProductService {
    
    @Autowired
    private ProductMapper productMapper;


    @Override
    public Product selectProductById(Long productId){
        return productMapper.selectProductById(productId);
    }

    @Override
    public List<Product> selectProductList(Product product){
        return productMapper.selectProductList(product);
    }

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

package com.ruoyi.system.service;


import com.ruoyi.system.domain.Product;

import java.util.List;

public interface IProductService {
    public Product selectProductById(Long id);

    public List<Product> selectProductList(Product product);

    public int insertProduct(Product product);

    public int updateProduct(Product product);

    public int deleteProductIds(long[] productIds);

    public int deleteProductById(long productId);

    int deleteProductByIds(Long[] productIds);

    int deleteProductById(Long productId);
}
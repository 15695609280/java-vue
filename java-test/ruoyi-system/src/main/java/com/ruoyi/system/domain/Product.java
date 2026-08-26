package com.ruoyi.system.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import java.math.BigDecimal;

/**
 * 商品对象 product
 *
 * 【练习目标】掌握实体类编写规范
 * - 继承 BaseEntity（含 createBy/createTime/updateBy/updateTime/remark 等公共字段）
 * - 私有字段 + getter/setter
 * - 用 @Excel 注解标记可导出字段（参考 SysUser 写法）
 *
 * 【对着敲练习】参考原项目 ruoyi-system/domain/SysPost.java 的写法，
 * 把下面字段补全 getter/setter 和 toString
 */
public class Product extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 商品ID */
    private Long productId;

    /** 商品名称 */
    private String productName;

    /** 商品价格 */
    private BigDecimal price;

    /** 库存数量 */
    private Integer stock;

    /** 状态（0正常 1停用） */
    private String status;

    // TODO: 生成 getter/setter 方法
    // 提示：IDEA 中按 Alt+Insert 自动生成，或手动敲

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString()
    {
        return "Product{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", price=" + price +
                ", stock=" + stock +
                ", status='" + status + '\'' +
                '}';
    }
}

package com.ruoyi.system.domain;

import com.ruoyi.common.core.domain.BaseEntity;

import java.math.BigDecimal;

public class Product extends BaseEntity {
   private static final long serialVersionUID = 1L;
   private Long productId;
   private String productName;
   private BigDecimal price;
   private Integer stock;
   private String status;


   public Long getProductId() {
         return productId;
   }
   public void setProductId(Long productId) {
      this.productId = productId;
   }

   public String getProductName() {
      return productName;
   }
   public void setProductName(String productName) {
      this.productName = productName;
   }

   public BigDecimal getPrice() {
      return price;
   }
   public void setPrice(BigDecimal price) {
      this.price = price;
   }

   public Integer getStock() { return stock; }
   public void setStock(Integer stock) { this.stock = stock; }

   public String getStatus() { return status; }
   public void setStatus(String status) { this.status = status; }

   @Override
   public String toString() {
      return "Product{" +
              "productId=" + productId +
              ", productName='" + productName + '\'' +
              ", price=" + price +
              ", stock=" + stock +
              ", status='" + status + '\'' +
              '}';
   }

}

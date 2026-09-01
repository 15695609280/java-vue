-- ----------------------------
-- 商品表（练习用）
-- 练习模块 Product 的建表脚本，配合 ProductController/ProductService/ProductMapper 使用
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `product_id`   BIGINT(20)    NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `product_name` VARCHAR(100)  NOT NULL                COMMENT '商品名称',
  `price`        DECIMAL(10,2) DEFAULT NULL            COMMENT '商品价格',
  `stock`        INT(11)       DEFAULT 0               COMMENT '库存数量',
  `status`       CHAR(1)       DEFAULT '0'             COMMENT '状态（0正常 1停用）',
  `create_by`    VARCHAR(64)   DEFAULT ''              COMMENT '创建者',
  `create_time`  DATETIME                              COMMENT '创建时间',
  `update_by`    VARCHAR(64)   DEFAULT ''              COMMENT '更新者',
  `update_time`  DATETIME                              COMMENT '更新时间',
  `remark`       VARCHAR(500)  DEFAULT NULL            COMMENT '备注',
  PRIMARY KEY (`product_id`)
) ENGINE=INNODB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='商品表';


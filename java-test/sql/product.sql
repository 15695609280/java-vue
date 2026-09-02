-- ----------------------------
-- 商品表（练习用）
-- 练习模块 Product 的建表脚本，配合 ProductController/ProductService/ProductMapper 使用
-- ----------------------------
 CREATE TABLE `product` (
                            `product_id`   bigint(20)      NOT NULL AUTO_INCREMENT  COMMENT '商品ID',
                            `product_name` varchar(100)    NOT NULL                 COMMENT '商品名称',
                            `price`        decimal(10, 2)  DEFAULT NULL             COMMENT '商品价格',
                            `stock`        int(11)         DEFAULT 0                COMMENT '库存数量',
                            `status`       char(1)         DEFAULT '0'              COMMENT '状态（0正常 1停用）',
                            `create_by`    varchar(64)     DEFAULT ''               COMMENT '创建者',
                            `create_time`  datetime        DEFAULT NULL             COMMENT '创建时间',
                            `update_by`    varchar(64)     DEFAULT ''               COMMENT '更新者',
                            `update_time`  datetime        DEFAULT NULL             COMMENT '更新时间',
                            `remark`       varchar(500)    DEFAULT NULL             COMMENT '备注',
                            PRIMARY KEY (`product_id`)
                        )  ENGINE=InnoDB AUTO_INCREMENT=1  COMMENT='商品表';

-- ----------------------------
-- 商品表（练习用）
-- 【对着敲练习】参考 sql/ry_20260417.sql 中的 sys_post 表结构
-- ----------------------------
DROP TABLE IF EXISTS `product`;
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
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='商品表';

-- ----------------------------
-- 初始化菜单数据（可选，用于权限控制）
-- 菜单ID需要根据实际情况调整，避免与现有菜单冲突
-- ----------------------------
-- INSERT INTO sys_menu VALUES (1000, '商品管理', 0, 2, 'product', 'demo/product/index', '', 1, 0, 'C', '0', '0', 'demo:product:list', 'shopping', 'admin', sysdate(), '', null, '商品管理菜单');
-- INSERT INTO sys_menu VALUES (1001, '商品查询', 1000, 1, '', '', '', 1, 0, 'F', '0', '0', 'demo:product:query', '#', 'admin', sysdate(), '', null, '');
-- INSERT INTO sys_menu VALUES (1002, '商品新增', 1000, 2, '', '', '', 1, 0, 'F', '0', '0', 'demo:product:add', '#', 'admin', sysdate(), '', null, '');
-- INSERT INTO sys_menu VALUES (1003, '商品修改', 1000, 3, '', '', '', 1, 0, 'F', '0', '0', 'demo:product:edit', '#', 'admin', sysdate(), '', null, '');
-- INSERT INTO sys_menu VALUES (1004, '商品删除', 1000, 4, '', '', '', 1, 0, 'F', '0', '0', 'demo:product:remove', '#', 'admin', sysdate(), '', null, '');
-- INSERT INTO sys_menu VALUES (1005, '商品导出', 1000, 5, '', '', '', 1, 0, 'F', '0', '0', 'demo:product:export', '#', 'admin', sysdate(), '', null, '');

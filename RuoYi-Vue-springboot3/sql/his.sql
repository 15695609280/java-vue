--科室表
CREATE TABBLE IF NOT EXISTS `his_department` (
    `dept_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '科室ID',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '上级科室',
  `dept_name` varchar(50) NOT NULL COMMENT '科室名称',
  `dept_code` varchar(30) NOT NULL COMMENT '科室编码',
  `dept_type` char(1) NOT NULL COMMENT '科室类型',
  `location` varchar(100) DEFAULT NULL COMMENT '位置',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `intro` varchar(500) DEFAULT NULL COMMENT '科室简介',
  `order_num` int(4) DEFAULT NULL COMMENT '排序',
  `status` char(1) DEFAULT NULL COMMENT '状态',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (dept_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='科室表';
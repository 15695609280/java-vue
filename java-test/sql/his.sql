-- ============================================================
-- 医院信息系统(HIS) 一键初始化脚本
-- 包含: 24张业务表 + 字典 + 菜单 + 演示数据
-- 执行: mysql -uroot -p ry-vue < sql/his.sql
-- ============================================================

-- ----------------------------
-- 科室表 his_department
-- ----------------------------
DROP TABLE IF EXISTS `his_department`;
CREATE TABLE `his_department` (
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
  PRIMARY KEY (`dept_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='科室表';

-- ----------------------------
-- 医生表 his_doctor
-- ----------------------------
DROP TABLE IF EXISTS `his_doctor`;
CREATE TABLE `his_doctor` (
  `doctor_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '医生ID',
  `doctor_name` varchar(50) NOT NULL COMMENT '医生姓名',
  `dept_id` bigint(20) NOT NULL COMMENT '所属科室',
  `title` char(1) NOT NULL COMMENT '职称',
  `gender` char(1) DEFAULT NULL COMMENT '性别',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `specialty` varchar(200) DEFAULT NULL COMMENT '擅长领域',
  `reg_fee` decimal(10,2) NOT NULL COMMENT '挂号费(元)',
  `intro` varchar(500) DEFAULT NULL COMMENT '个人简介',
  `status` char(1) DEFAULT NULL COMMENT '状态',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`doctor_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='医生表';

-- ----------------------------
-- 患者表 his_patient
-- ----------------------------
DROP TABLE IF EXISTS `his_patient`;
CREATE TABLE `his_patient` (
  `patient_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '患者ID',
  `patient_no` varchar(20) NOT NULL COMMENT '就诊卡号',
  `patient_name` varchar(50) NOT NULL COMMENT '患者姓名',
  `gender` char(1) NOT NULL COMMENT '性别',
  `birth_date` date DEFAULT NULL COMMENT '出生日期',
  `age` int(4) DEFAULT NULL COMMENT '年龄',
  `id_card` varchar(18) DEFAULT NULL COMMENT '身份证号',
  `phone` varchar(20) NOT NULL COMMENT '联系电话',
  `address` varchar(200) DEFAULT NULL COMMENT '住址',
  `blood_type` char(2) DEFAULT NULL COMMENT '血型',
  `insurance_type` char(1) DEFAULT NULL COMMENT '医保类型',
  `allergy` varchar(200) DEFAULT NULL COMMENT '过敏史',
  `status` char(1) DEFAULT NULL COMMENT '状态',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`patient_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='患者表';

-- ----------------------------
-- 药品表 his_drug
-- ----------------------------
DROP TABLE IF EXISTS `his_drug`;
CREATE TABLE `his_drug` (
  `drug_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '药品ID',
  `drug_code` varchar(30) NOT NULL COMMENT '药品编码',
  `drug_name` varchar(100) NOT NULL COMMENT '药品名称',
  `category` char(1) NOT NULL COMMENT '药品分类',
  `specification` varchar(50) NOT NULL COMMENT '规格',
  `unit` varchar(10) NOT NULL COMMENT '单位',
  `price` decimal(10,2) NOT NULL COMMENT '单价(元)',
  `stock` int(11) NOT NULL COMMENT '库存',
  `stock_warn` int(11) DEFAULT NULL COMMENT '预警库存',
  `manufacturer` varchar(100) DEFAULT NULL COMMENT '生产厂家',
  `approval_no` varchar(50) DEFAULT NULL COMMENT '批准文号',
  `is_prescription` char(1) DEFAULT NULL COMMENT '处方药',
  `status` char(1) DEFAULT NULL COMMENT '状态',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`drug_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='药品表';

-- ----------------------------
-- 供应商表 his_supplier
-- ----------------------------
DROP TABLE IF EXISTS `his_supplier`;
CREATE TABLE `his_supplier` (
  `supplier_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '供应商ID',
  `supplier_name` varchar(100) NOT NULL COMMENT '供应商名称',
  `contact` varchar(50) DEFAULT NULL COMMENT '联系人',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `address` varchar(200) DEFAULT NULL COMMENT '地址',
  `status` char(1) DEFAULT NULL COMMENT '状态',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`supplier_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='供应商表';

-- ----------------------------
-- 收费项目表 his_fee_item
-- ----------------------------
DROP TABLE IF EXISTS `his_fee_item`;
CREATE TABLE `his_fee_item` (
  `item_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '项目ID',
  `item_name` varchar(100) NOT NULL COMMENT '项目名称',
  `category` char(1) NOT NULL COMMENT '项目类别',
  `price` decimal(10,2) NOT NULL COMMENT '单价(元)',
  `unit` varchar(10) DEFAULT NULL COMMENT '单位',
  `status` char(1) DEFAULT NULL COMMENT '状态',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`item_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='收费项目表';

-- ----------------------------
-- 病区表 his_ward
-- ----------------------------
DROP TABLE IF EXISTS `his_ward`;
CREATE TABLE `his_ward` (
  `ward_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '病区ID',
  `ward_name` varchar(50) NOT NULL COMMENT '病区名称',
  `dept_id` bigint(20) NOT NULL COMMENT '所属科室',
  `location` varchar(100) DEFAULT NULL COMMENT '位置',
  `nurse_count` int(4) DEFAULT NULL COMMENT '护士人数',
  `status` char(1) DEFAULT NULL COMMENT '状态',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`ward_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='病区表';

-- ----------------------------
-- 床位表 his_bed
-- ----------------------------
DROP TABLE IF EXISTS `his_bed`;
CREATE TABLE `his_bed` (
  `bed_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '床位ID',
  `ward_id` bigint(20) NOT NULL COMMENT '所属病区',
  `bed_no` varchar(20) NOT NULL COMMENT '床号',
  `bed_type` char(1) DEFAULT NULL COMMENT '床位类型',
  `price_per_day` decimal(10,2) DEFAULT NULL COMMENT '每日价格(元)',
  `status` char(1) DEFAULT NULL COMMENT '床位状态',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`bed_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='床位表';

-- ----------------------------
-- 排班表 his_schedule
-- ----------------------------
DROP TABLE IF EXISTS `his_schedule`;
CREATE TABLE `his_schedule` (
  `schedule_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '排班ID',
  `dept_id` bigint(20) NOT NULL COMMENT '科室',
  `doctor_id` bigint(20) NOT NULL COMMENT '医生',
  `work_date` date NOT NULL COMMENT '出诊日期',
  `time_slot` char(1) NOT NULL COMMENT '时段',
  `quota` int(4) NOT NULL COMMENT '总号源',
  `remain_quota` int(4) NOT NULL COMMENT '剩余号源',
  `status` char(1) DEFAULT NULL COMMENT '状态',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`schedule_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='排班表';

-- ----------------------------
-- 挂号表 his_registration
-- ----------------------------
DROP TABLE IF EXISTS `his_registration`;
CREATE TABLE `his_registration` (
  `reg_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '挂号ID',
  `reg_no` varchar(30) DEFAULT NULL COMMENT '挂号单号',
  `patient_id` bigint(20) NOT NULL COMMENT '患者',
  `dept_id` bigint(20) NOT NULL COMMENT '科室',
  `doctor_id` bigint(20) NOT NULL COMMENT '医生',
  `schedule_id` bigint(20) DEFAULT NULL COMMENT '排班ID',
  `reg_date` date DEFAULT NULL COMMENT '就诊日期',
  `time_slot` char(1) DEFAULT NULL COMMENT '时段',
  `queue_no` int(4) DEFAULT NULL COMMENT '排队号',
  `reg_fee` decimal(10,2) DEFAULT NULL COMMENT '挂号费(元)',
  `visit_status` char(1) DEFAULT NULL COMMENT '状态',
  `pay_status` char(1) DEFAULT NULL COMMENT '缴费状态',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`reg_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='挂号表';

-- ----------------------------
-- 就诊记录表 his_visit
-- ----------------------------
DROP TABLE IF EXISTS `his_visit`;
CREATE TABLE `his_visit` (
  `visit_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '就诊ID',
  `visit_no` varchar(30) DEFAULT NULL COMMENT '就诊编号',
  `reg_id` bigint(20) DEFAULT NULL COMMENT '挂号ID',
  `patient_id` bigint(20) DEFAULT NULL COMMENT '患者',
  `doctor_id` bigint(20) DEFAULT NULL COMMENT '医生',
  `dept_id` bigint(20) DEFAULT NULL COMMENT '科室',
  `visit_time` datetime DEFAULT NULL COMMENT '就诊时间',
  `chief_complaint` varchar(500) DEFAULT NULL COMMENT '主诉',
  `present_illness` varchar(1000) DEFAULT NULL COMMENT '现病史',
  `past_illness` varchar(500) DEFAULT NULL COMMENT '既往史',
  `diagnosis` varchar(500) DEFAULT NULL COMMENT '诊断结果',
  `treatment` varchar(500) DEFAULT NULL COMMENT '处理意见',
  `status` char(1) DEFAULT NULL COMMENT '就诊状态',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`visit_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='就诊记录表';

-- ----------------------------
-- 处方表 his_prescription
-- ----------------------------
DROP TABLE IF EXISTS `his_prescription`;
CREATE TABLE `his_prescription` (
  `rx_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '处方ID',
  `rx_no` varchar(30) DEFAULT NULL COMMENT '处方号',
  `visit_id` bigint(20) DEFAULT NULL COMMENT '就诊ID',
  `patient_id` bigint(20) DEFAULT NULL COMMENT '患者',
  `doctor_id` bigint(20) DEFAULT NULL COMMENT '医生',
  `rx_type` char(1) DEFAULT NULL COMMENT '处方类型',
  `total_amount` decimal(10,2) DEFAULT NULL COMMENT '合计金额(元)',
  `status` char(1) DEFAULT NULL COMMENT '状态',
  `dispense_by` varchar(64) DEFAULT NULL COMMENT '发药药师',
  `dispense_time` datetime DEFAULT NULL COMMENT '发药时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`rx_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='处方表';

-- ----------------------------
-- 处方明细表 his_prescription_item
-- ----------------------------
DROP TABLE IF EXISTS `his_prescription_item`;
CREATE TABLE `his_prescription_item` (
  `item_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `rx_id` bigint(20) DEFAULT NULL COMMENT '处方ID',
  `drug_id` bigint(20) DEFAULT NULL COMMENT '药品',
  `drug_name` varchar(100) DEFAULT NULL COMMENT '药品名称',
  `specification` varchar(50) DEFAULT NULL COMMENT '规格',
  `unit` varchar(10) DEFAULT NULL COMMENT '单位',
  `price` decimal(10,2) DEFAULT NULL COMMENT '单价(元)',
  `quantity` int(11) DEFAULT NULL COMMENT '数量',
  `amount` decimal(10,2) DEFAULT NULL COMMENT '金额(元)',
  `usage_dose` varchar(100) DEFAULT NULL COMMENT '用法用量',
  `frequency` varchar(50) DEFAULT NULL COMMENT '频次',
  `days` int(4) DEFAULT NULL COMMENT '天数',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`item_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='处方明细表';

-- ----------------------------
-- 费用单表 his_charge
-- ----------------------------
DROP TABLE IF EXISTS `his_charge`;
CREATE TABLE `his_charge` (
  `charge_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '费用ID',
  `charge_no` varchar(30) DEFAULT NULL COMMENT '单据号',
  `patient_id` bigint(20) DEFAULT NULL COMMENT '患者',
  `source_type` char(1) DEFAULT NULL COMMENT '费用类型',
  `source_id` bigint(20) DEFAULT NULL COMMENT '来源单据ID',
  `item_name` varchar(100) DEFAULT NULL COMMENT '项目名称',
  `price` decimal(10,2) DEFAULT NULL COMMENT '单价(元)',
  `quantity` int(11) DEFAULT NULL COMMENT '数量',
  `amount` decimal(10,2) DEFAULT NULL COMMENT '金额(元)',
  `charge_status` char(1) DEFAULT NULL COMMENT '收费状态',
  `pay_type` char(1) DEFAULT NULL COMMENT '支付方式',
  `settle_time` datetime DEFAULT NULL COMMENT '结算时间',
  `operator` varchar(64) DEFAULT NULL COMMENT '收费员',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`charge_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='费用单表';

-- ----------------------------
-- 住院登记表 his_admission
-- ----------------------------
DROP TABLE IF EXISTS `his_admission`;
CREATE TABLE `his_admission` (
  `adm_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '住院ID',
  `adm_no` varchar(30) DEFAULT NULL COMMENT '住院号',
  `patient_id` bigint(20) DEFAULT NULL COMMENT '患者',
  `dept_id` bigint(20) DEFAULT NULL COMMENT '科室',
  `ward_id` bigint(20) DEFAULT NULL COMMENT '病区',
  `bed_id` bigint(20) DEFAULT NULL COMMENT '床位',
  `doctor_id` bigint(20) DEFAULT NULL COMMENT '主治医生',
  `in_date` datetime DEFAULT NULL COMMENT '入院时间',
  `out_date` datetime DEFAULT NULL COMMENT '出院时间',
  `deposit` decimal(10,2) DEFAULT NULL COMMENT '预缴押金(元)',
  `adm_status` char(1) DEFAULT NULL COMMENT '住院状态',
  `diagnosis_in` varchar(500) DEFAULT NULL COMMENT '入院诊断',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`adm_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='住院登记表';

-- ----------------------------
-- 医嘱表 his_medical_order
-- ----------------------------
DROP TABLE IF EXISTS `his_medical_order`;
CREATE TABLE `his_medical_order` (
  `order_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '医嘱ID',
  `adm_id` bigint(20) DEFAULT NULL COMMENT '住院ID',
  `patient_id` bigint(20) DEFAULT NULL COMMENT '患者',
  `doctor_id` bigint(20) DEFAULT NULL COMMENT '开立医生',
  `order_type` char(1) NOT NULL COMMENT '医嘱类型',
  `content` varchar(500) NOT NULL COMMENT '医嘱内容',
  `drug_id` bigint(20) DEFAULT NULL COMMENT '关联药品',
  `dose` varchar(50) DEFAULT NULL COMMENT '单次剂量',
  `frequency` varchar(50) DEFAULT NULL COMMENT '频次',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '停止时间',
  `order_status` char(1) DEFAULT NULL COMMENT '状态',
  `exec_by` varchar(64) DEFAULT NULL COMMENT '执行护士',
  `exec_time` datetime DEFAULT NULL COMMENT '执行时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='医嘱表';

-- ----------------------------
-- 护理记录表 his_nursing
-- ----------------------------
DROP TABLE IF EXISTS `his_nursing`;
CREATE TABLE `his_nursing` (
  `record_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `adm_id` bigint(20) DEFAULT NULL COMMENT '住院ID',
  `patient_id` bigint(20) DEFAULT NULL COMMENT '患者',
  `nurse_name` varchar(50) DEFAULT NULL COMMENT '护士',
  `record_time` datetime DEFAULT NULL COMMENT '记录时间',
  `temperature` decimal(4,1) DEFAULT NULL COMMENT '体温(℃)',
  `pulse` int(4) DEFAULT NULL COMMENT '脉搏(次/分)',
  `breath` int(4) DEFAULT NULL COMMENT '呼吸(次/分)',
  `bp_high` int(4) DEFAULT NULL COMMENT '收缩压(mmHg)',
  `bp_low` int(4) DEFAULT NULL COMMENT '舒张压(mmHg)',
  `spo2` int(4) DEFAULT NULL COMMENT '血氧(%)',
  `content` varchar(500) DEFAULT NULL COMMENT '护理内容',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`record_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='护理记录表';

-- ----------------------------
-- 检验单表 his_lab_test
-- ----------------------------
DROP TABLE IF EXISTS `his_lab_test`;
CREATE TABLE `his_lab_test` (
  `test_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '检验ID',
  `test_no` varchar(30) DEFAULT NULL COMMENT '检验单号',
  `patient_id` bigint(20) DEFAULT NULL COMMENT '患者',
  `visit_id` bigint(20) DEFAULT NULL COMMENT '门诊就诊ID',
  `adm_id` bigint(20) DEFAULT NULL COMMENT '住院ID',
  `doctor_id` bigint(20) DEFAULT NULL COMMENT '申请医生',
  `test_item` varchar(200) NOT NULL COMMENT '检验项目',
  `sample_type` char(1) DEFAULT NULL COMMENT '标本类型',
  `status` char(1) DEFAULT NULL COMMENT '状态',
  `apply_time` datetime DEFAULT NULL COMMENT '申请时间',
  `sample_time` datetime DEFAULT NULL COMMENT '采样时间',
  `report_time` datetime DEFAULT NULL COMMENT '报告时间',
  `result_summary` varchar(1000) DEFAULT NULL COMMENT '检验结论',
  `report_by` varchar(64) DEFAULT NULL COMMENT '报告人',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`test_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='检验单表';

-- ----------------------------
-- 检验结果表 his_lab_result
-- ----------------------------
DROP TABLE IF EXISTS `his_lab_result`;
CREATE TABLE `his_lab_result` (
  `result_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '结果ID',
  `test_id` bigint(20) DEFAULT NULL COMMENT '检验ID',
  `item_name` varchar(100) DEFAULT NULL COMMENT '项目名称',
  `result_value` varchar(50) DEFAULT NULL COMMENT '结果值',
  `unit` varchar(20) DEFAULT NULL COMMENT '单位',
  `ref_range` varchar(50) DEFAULT NULL COMMENT '参考范围',
  `flag` char(1) DEFAULT NULL COMMENT '结果标识',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`result_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='检验结果表';

-- ----------------------------
-- 检查单表 his_exam
-- ----------------------------
DROP TABLE IF EXISTS `his_exam`;
CREATE TABLE `his_exam` (
  `exam_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '检查ID',
  `exam_no` varchar(30) DEFAULT NULL COMMENT '检查单号',
  `patient_id` bigint(20) DEFAULT NULL COMMENT '患者',
  `doctor_id` bigint(20) DEFAULT NULL COMMENT '申请医生',
  `exam_type` char(1) NOT NULL COMMENT '检查类型',
  `body_part` varchar(100) NOT NULL COMMENT '检查部位',
  `purpose` varchar(500) DEFAULT NULL COMMENT '检查目的',
  `status` char(1) DEFAULT NULL COMMENT '状态',
  `apply_time` datetime DEFAULT NULL COMMENT '申请时间',
  `report_time` datetime DEFAULT NULL COMMENT '报告时间',
  `finding` varchar(1000) DEFAULT NULL COMMENT '影像所见',
  `conclusion` varchar(500) DEFAULT NULL COMMENT '诊断结论',
  `report_by` varchar(64) DEFAULT NULL COMMENT '报告人',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`exam_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='检查单表';

-- ----------------------------
-- 手术安排表 his_surgery
-- ----------------------------
DROP TABLE IF EXISTS `his_surgery`;
CREATE TABLE `his_surgery` (
  `surgery_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '手术ID',
  `surgery_no` varchar(30) DEFAULT NULL COMMENT '手术编号',
  `surgery_name` varchar(100) NOT NULL COMMENT '手术名称',
  `patient_id` bigint(20) NOT NULL COMMENT '患者',
  `dept_id` bigint(20) DEFAULT NULL COMMENT '科室',
  `surgeon_id` bigint(20) DEFAULT NULL COMMENT '主刀医生',
  `anesthesia` char(1) DEFAULT NULL COMMENT '麻醉方式',
  `room_no` varchar(20) DEFAULT NULL COMMENT '手术间',
  `level` char(1) DEFAULT NULL COMMENT '手术级别',
  `plan_time` datetime DEFAULT NULL COMMENT '计划时间',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `status` char(1) DEFAULT NULL COMMENT '状态',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`surgery_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='手术安排表';

-- ----------------------------
-- 采购单表 his_purchase
-- ----------------------------
DROP TABLE IF EXISTS `his_purchase`;
CREATE TABLE `his_purchase` (
  `purchase_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '采购ID',
  `purchase_no` varchar(30) DEFAULT NULL COMMENT '采购单号',
  `supplier_id` bigint(20) NOT NULL COMMENT '供应商',
  `total_amount` decimal(12,2) DEFAULT NULL COMMENT '合计金额(元)',
  `status` char(1) DEFAULT NULL COMMENT '状态',
  `in_time` datetime DEFAULT NULL COMMENT '入库时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`purchase_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='采购单表';

-- ----------------------------
-- 采购明细表 his_purchase_item
-- ----------------------------
DROP TABLE IF EXISTS `his_purchase_item`;
CREATE TABLE `his_purchase_item` (
  `item_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `purchase_id` bigint(20) DEFAULT NULL COMMENT '采购ID',
  `drug_id` bigint(20) DEFAULT NULL COMMENT '药品',
  `quantity` int(11) DEFAULT NULL COMMENT '数量',
  `price` decimal(10,2) DEFAULT NULL COMMENT '采购单价(元)',
  `amount` decimal(10,2) DEFAULT NULL COMMENT '金额(元)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`item_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='采购明细表';

-- ----------------------------
-- 出入库记录表 his_stock_record
-- ----------------------------
DROP TABLE IF EXISTS `his_stock_record`;
CREATE TABLE `his_stock_record` (
  `record_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `record_no` varchar(30) DEFAULT NULL COMMENT '单据号',
  `record_type` char(1) DEFAULT NULL COMMENT '变动类型',
  `drug_id` bigint(20) DEFAULT NULL COMMENT '药品',
  `quantity` int(11) DEFAULT NULL COMMENT '变动数量',
  `before_stock` int(11) DEFAULT NULL COMMENT '变动前库存',
  `after_stock` int(11) DEFAULT NULL COMMENT '变动后库存',
  `source_id` bigint(20) DEFAULT NULL COMMENT '来源单据ID',
  `operator` varchar(64) DEFAULT NULL COMMENT '经办人',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`record_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='出入库记录表';

-- ----------------------------
-- HIS 字典(可重复执行: 先清理旧数据)
-- ----------------------------
delete from sys_dict_data where dict_type like 'his_%';
delete from sys_dict_type where dict_type like 'his_%';

insert into sys_dict_type(dict_id, dict_name, dict_type, status, create_by, create_time, remark) values (200, '科室类型', 'his_dept_type', '0', 'admin', now(), '科室类型');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2000, 1, '门诊科室', '0', 'his_dept_type', '', 'primary', 'Y', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2001, 2, '住院病区', '1', 'his_dept_type', '', 'success', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2002, 3, '医技科室', '2', 'his_dept_type', '', 'warning', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2003, 4, '行政后勤', '3', 'his_dept_type', '', 'info', 'N', '0', 'admin', now(), '');

insert into sys_dict_type(dict_id, dict_name, dict_type, status, create_by, create_time, remark) values (201, '医生职称', 'his_doctor_title', '0', 'admin', now(), '医生职称');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2004, 1, '主任医师', '0', 'his_doctor_title', '', 'danger', 'Y', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2005, 2, '副主任医师', '1', 'his_doctor_title', '', 'warning', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2006, 3, '主治医师', '2', 'his_doctor_title', '', 'primary', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2007, 4, '住院医师', '3', 'his_doctor_title', '', 'info', 'N', '0', 'admin', now(), '');

insert into sys_dict_type(dict_id, dict_name, dict_type, status, create_by, create_time, remark) values (202, '血型', 'his_blood_type', '0', 'admin', now(), '血型');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2008, 1, 'A型', 'A', 'his_blood_type', '', 'default', 'Y', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2009, 2, 'B型', 'B', 'his_blood_type', '', 'default', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2010, 3, 'O型', 'O', 'his_blood_type', '', 'default', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2011, 4, 'AB型', 'AB', 'his_blood_type', '', 'default', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2012, 5, '不详', 'U', 'his_blood_type', '', 'default', 'N', '0', 'admin', now(), '');

insert into sys_dict_type(dict_id, dict_name, dict_type, status, create_by, create_time, remark) values (203, '医保类型', 'his_insurance_type', '0', 'admin', now(), '医保类型');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2013, 1, '自费', '0', 'his_insurance_type', '', 'info', 'Y', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2014, 2, '医保', '1', 'his_insurance_type', '', 'success', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2015, 3, '新农合', '2', 'his_insurance_type', '', 'primary', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2016, 4, '商业保险', '3', 'his_insurance_type', '', 'warning', 'N', '0', 'admin', now(), '');

insert into sys_dict_type(dict_id, dict_name, dict_type, status, create_by, create_time, remark) values (204, '药品分类', 'his_drug_category', '0', 'admin', now(), '药品分类');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2017, 1, '抗生素', '0', 'his_drug_category', '', 'danger', 'Y', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2018, 2, '心血管', '1', 'his_drug_category', '', 'primary', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2019, 3, '解热镇痛', '2', 'his_drug_category', '', 'warning', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2020, 4, '中成药', '3', 'his_drug_category', '', 'success', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2021, 5, '注射剂', '4', 'his_drug_category', '', 'primary', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2022, 6, '外用药', '5', 'his_drug_category', '', 'info', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2023, 7, '其他', '9', 'his_drug_category', '', 'info', 'N', '0', 'admin', now(), '');

insert into sys_dict_type(dict_id, dict_name, dict_type, status, create_by, create_time, remark) values (205, '处方类型', 'his_rx_type', '0', 'admin', now(), '处方类型');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2024, 1, '西药处方', '0', 'his_rx_type', '', 'primary', 'Y', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2025, 2, '中药处方', '1', 'his_rx_type', '', 'success', 'N', '0', 'admin', now(), '');

insert into sys_dict_type(dict_id, dict_name, dict_type, status, create_by, create_time, remark) values (206, '就诊时段', 'his_time_slot', '0', 'admin', now(), '就诊时段');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2026, 1, '上午', '0', 'his_time_slot', '', 'primary', 'Y', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2027, 2, '下午', '1', 'his_time_slot', '', 'warning', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2028, 3, '晚间', '2', 'his_time_slot', '', 'info', 'N', '0', 'admin', now(), '');

insert into sys_dict_type(dict_id, dict_name, dict_type, status, create_by, create_time, remark) values (207, '挂号状态', 'his_reg_status', '0', 'admin', now(), '挂号状态');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2029, 1, '待就诊', '0', 'his_reg_status', '', 'info', 'Y', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2030, 2, '就诊中', '1', 'his_reg_status', '', 'primary', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2031, 3, '已诊毕', '2', 'his_reg_status', '', 'success', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2032, 4, '已退号', '3', 'his_reg_status', '', 'danger', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2033, 5, '已过号', '4', 'his_reg_status', '', 'warning', 'N', '0', 'admin', now(), '');

insert into sys_dict_type(dict_id, dict_name, dict_type, status, create_by, create_time, remark) values (208, '处方状态', 'his_rx_status', '0', 'admin', now(), '处方状态');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2034, 1, '已开立', '0', 'his_rx_status', '', 'info', 'Y', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2035, 2, '已收费', '1', 'his_rx_status', '', 'primary', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2036, 3, '已发药', '2', 'his_rx_status', '', 'success', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2037, 4, '已作废', '3', 'his_rx_status', '', 'danger', 'N', '0', 'admin', now(), '');

insert into sys_dict_type(dict_id, dict_name, dict_type, status, create_by, create_time, remark) values (209, '床位类型', 'his_bed_type', '0', 'admin', now(), '床位类型');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2038, 1, '普通床位', '0', 'his_bed_type', '', 'info', 'Y', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2039, 2, '重症床位', '1', 'his_bed_type', '', 'danger', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2040, 3, 'VIP床位', '2', 'his_bed_type', '', 'warning', 'N', '0', 'admin', now(), '');

insert into sys_dict_type(dict_id, dict_name, dict_type, status, create_by, create_time, remark) values (210, '床位状态', 'his_bed_status', '0', 'admin', now(), '床位状态');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2041, 1, '空闲', '0', 'his_bed_status', '', 'success', 'Y', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2042, 2, '占用', '1', 'his_bed_status', '', 'danger', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2043, 3, '维修', '2', 'his_bed_status', '', 'warning', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2044, 4, '预留', '3', 'his_bed_status', '', 'info', 'N', '0', 'admin', now(), '');

insert into sys_dict_type(dict_id, dict_name, dict_type, status, create_by, create_time, remark) values (211, '住院状态', 'his_adm_status', '0', 'admin', now(), '住院状态');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2045, 1, '在院', '0', 'his_adm_status', '', 'primary', 'Y', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2046, 2, '已出院', '1', 'his_adm_status', '', 'success', 'N', '0', 'admin', now(), '');

insert into sys_dict_type(dict_id, dict_name, dict_type, status, create_by, create_time, remark) values (212, '医嘱类型', 'his_order_type', '0', 'admin', now(), '医嘱类型');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2047, 1, '长期医嘱', '0', 'his_order_type', '', 'primary', 'Y', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2048, 2, '临时医嘱', '1', 'his_order_type', '', 'warning', 'N', '0', 'admin', now(), '');

insert into sys_dict_type(dict_id, dict_name, dict_type, status, create_by, create_time, remark) values (213, '医嘱状态', 'his_order_status', '0', 'admin', now(), '医嘱状态');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2049, 1, '已开立', '0', 'his_order_status', '', 'info', 'Y', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2050, 2, '已执行', '1', 'his_order_status', '', 'success', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2051, 3, '已停止', '2', 'his_order_status', '', 'danger', 'N', '0', 'admin', now(), '');

insert into sys_dict_type(dict_id, dict_name, dict_type, status, create_by, create_time, remark) values (214, '费用类型', 'his_charge_type', '0', 'admin', now(), '费用类型');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2052, 1, '挂号费', '0', 'his_charge_type', '', 'primary', 'Y', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2053, 2, '处方费', '1', 'his_charge_type', '', 'success', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2054, 3, '检验费', '2', 'his_charge_type', '', 'warning', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2055, 4, '检查费', '3', 'his_charge_type', '', 'warning', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2056, 5, '住院费', '4', 'his_charge_type', '', 'danger', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2057, 6, '手术费', '5', 'his_charge_type', '', 'danger', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2058, 7, '其他', '9', 'his_charge_type', '', 'info', 'N', '0', 'admin', now(), '');

insert into sys_dict_type(dict_id, dict_name, dict_type, status, create_by, create_time, remark) values (215, '收费状态', 'his_charge_status', '0', 'admin', now(), '收费状态');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2059, 1, '未收费', '0', 'his_charge_status', '', 'danger', 'Y', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2060, 2, '已收费', '1', 'his_charge_status', '', 'success', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2061, 3, '已退费', '2', 'his_charge_status', '', 'info', 'N', '0', 'admin', now(), '');

insert into sys_dict_type(dict_id, dict_name, dict_type, status, create_by, create_time, remark) values (216, '支付方式', 'his_pay_type', '0', 'admin', now(), '支付方式');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2062, 1, '现金', '0', 'his_pay_type', '', 'success', 'Y', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2063, 2, '扫码支付', '1', 'his_pay_type', '', 'primary', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2064, 3, '医保', '2', 'his_pay_type', '', 'warning', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2065, 4, '银行卡', '3', 'his_pay_type', '', 'info', 'N', '0', 'admin', now(), '');

insert into sys_dict_type(dict_id, dict_name, dict_type, status, create_by, create_time, remark) values (217, '检验状态', 'his_lab_status', '0', 'admin', now(), '检验状态');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2066, 1, '已申请', '0', 'his_lab_status', '', 'info', 'Y', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2067, 2, '已采样', '1', 'his_lab_status', '', 'primary', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2068, 3, '检验中', '2', 'his_lab_status', '', 'warning', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2069, 4, '已出报告', '3', 'his_lab_status', '', 'success', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2070, 5, '已作废', '4', 'his_lab_status', '', 'danger', 'N', '0', 'admin', now(), '');

insert into sys_dict_type(dict_id, dict_name, dict_type, status, create_by, create_time, remark) values (218, '标本类型', 'his_sample_type', '0', 'admin', now(), '标本类型');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2071, 1, '血液', '0', 'his_sample_type', '', 'danger', 'Y', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2072, 2, '尿液', '1', 'his_sample_type', '', 'warning', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2073, 3, '粪便', '2', 'his_sample_type', '', 'info', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2074, 4, '分泌物', '3', 'his_sample_type', '', 'info', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2075, 5, '其他', '9', 'his_sample_type', '', 'info', 'N', '0', 'admin', now(), '');

insert into sys_dict_type(dict_id, dict_name, dict_type, status, create_by, create_time, remark) values (219, '检验结果标识', 'his_lab_flag', '0', 'admin', now(), '检验结果标识');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2076, 1, '正常', '0', 'his_lab_flag', '', 'success', 'Y', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2077, 2, '偏高', '1', 'his_lab_flag', '', 'danger', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2078, 3, '偏低', '2', 'his_lab_flag', '', 'primary', 'N', '0', 'admin', now(), '');

insert into sys_dict_type(dict_id, dict_name, dict_type, status, create_by, create_time, remark) values (220, '检查类型', 'his_exam_type', '0', 'admin', now(), '检查类型');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2079, 1, 'CT', '0', 'his_exam_type', '', 'primary', 'Y', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2080, 2, 'MRI核磁', '1', 'his_exam_type', '', 'warning', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2081, 3, 'X光', '2', 'his_exam_type', '', 'info', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2082, 4, 'B超', '3', 'his_exam_type', '', 'success', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2083, 5, '心电图', '4', 'his_exam_type', '', 'danger', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2084, 6, '内镜', '5', 'his_exam_type', '', 'warning', 'N', '0', 'admin', now(), '');

insert into sys_dict_type(dict_id, dict_name, dict_type, status, create_by, create_time, remark) values (221, '检查状态', 'his_exam_status', '0', 'admin', now(), '检查状态');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2085, 1, '已申请', '0', 'his_exam_status', '', 'info', 'Y', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2086, 2, '已检查', '1', 'his_exam_status', '', 'primary', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2087, 3, '已出报告', '2', 'his_exam_status', '', 'success', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2088, 4, '已取消', '3', 'his_exam_status', '', 'danger', 'N', '0', 'admin', now(), '');

insert into sys_dict_type(dict_id, dict_name, dict_type, status, create_by, create_time, remark) values (222, '手术级别', 'his_surgery_level', '0', 'admin', now(), '手术级别');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2089, 1, '一级手术', '0', 'his_surgery_level', '', 'info', 'Y', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2090, 2, '二级手术', '1', 'his_surgery_level', '', 'primary', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2091, 3, '三级手术', '2', 'his_surgery_level', '', 'warning', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2092, 4, '四级手术', '3', 'his_surgery_level', '', 'danger', 'N', '0', 'admin', now(), '');

insert into sys_dict_type(dict_id, dict_name, dict_type, status, create_by, create_time, remark) values (223, '手术状态', 'his_surgery_status', '0', 'admin', now(), '手术状态');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2093, 1, '已预约', '0', 'his_surgery_status', '', 'info', 'Y', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2094, 2, '手术中', '1', 'his_surgery_status', '', 'warning', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2095, 3, '已完成', '2', 'his_surgery_status', '', 'success', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2096, 4, '已取消', '3', 'his_surgery_status', '', 'danger', 'N', '0', 'admin', now(), '');

insert into sys_dict_type(dict_id, dict_name, dict_type, status, create_by, create_time, remark) values (224, '麻醉方式', 'his_anesthesia', '0', 'admin', now(), '麻醉方式');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2097, 1, '全身麻醉', '0', 'his_anesthesia', '', 'danger', 'Y', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2098, 2, '局部麻醉', '1', 'his_anesthesia', '', 'success', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2099, 3, '腰椎麻醉', '2', 'his_anesthesia', '', 'warning', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2100, 4, '硬膜外麻醉', '3', 'his_anesthesia', '', 'primary', 'N', '0', 'admin', now(), '');

insert into sys_dict_type(dict_id, dict_name, dict_type, status, create_by, create_time, remark) values (225, '库存变动类型', 'his_stock_type', '0', 'admin', now(), '库存变动类型');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2101, 1, '采购入库', '0', 'his_stock_type', '', 'success', 'Y', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2102, 2, '发药出库', '1', 'his_stock_type', '', 'danger', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2103, 3, '退药入库', '2', 'his_stock_type', '', 'warning', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2104, 4, '盘盈', '3', 'his_stock_type', '', 'success', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2105, 5, '盘亏', '4', 'his_stock_type', '', 'danger', 'N', '0', 'admin', now(), '');

insert into sys_dict_type(dict_id, dict_name, dict_type, status, create_by, create_time, remark) values (226, '采购单状态', 'his_purchase_status', '0', 'admin', now(), '采购单状态');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2106, 1, '待审核', '0', 'his_purchase_status', '', 'warning', 'Y', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2107, 2, '已入库', '1', 'his_purchase_status', '', 'success', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2108, 3, '已取消', '2', 'his_purchase_status', '', 'info', 'N', '0', 'admin', now(), '');

insert into sys_dict_type(dict_id, dict_name, dict_type, status, create_by, create_time, remark) values (227, '收费项目类别', 'his_fee_category', '0', 'admin', now(), '收费项目类别');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2109, 1, '检验费', '0', 'his_fee_category', '', 'warning', 'Y', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2110, 2, '检查费', '1', 'his_fee_category', '', 'primary', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2111, 3, '手术费', '2', 'his_fee_category', '', 'danger', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2112, 4, '床位费', '3', 'his_fee_category', '', 'success', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2113, 5, '护理费', '4', 'his_fee_category', '', 'info', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2114, 6, '诊疗费', '5', 'his_fee_category', '', 'primary', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2115, 7, '其他', '9', 'his_fee_category', '', 'info', 'N', '0', 'admin', now(), '');

insert into sys_dict_type(dict_id, dict_name, dict_type, status, create_by, create_time, remark) values (228, '排班状态', 'his_schedule_status', '0', 'admin', now(), '排班状态');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2116, 1, '开放挂号', '0', 'his_schedule_status', '', 'success', 'Y', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2117, 2, '停诊', '1', 'his_schedule_status', '', 'danger', 'N', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2118, 3, '约满', '2', 'his_schedule_status', '', 'warning', 'N', '0', 'admin', now(), '');

insert into sys_dict_type(dict_id, dict_name, dict_type, status, create_by, create_time, remark) values (229, '就诊状态', 'his_visit_status', '0', 'admin', now(), '就诊状态');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2119, 1, '接诊中', '0', 'his_visit_status', '', 'primary', 'Y', '0', 'admin', now(), '');
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (2120, 2, '已完诊', '1', 'his_visit_status', '', 'success', 'N', '0', 'admin', now(), '');

-- ----------------------------
-- HIS 菜单(可重复执行: 先清理旧菜单)
-- ----------------------------
delete from sys_menu where menu_id >= 3000 and menu_id < 40000;

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (3000, '智慧医疗', 0, 2, 'his', null, '', 1, 0, 'M', '0', '0', '', 'guide', 'admin', now(), '智慧医疗');
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (3001, '医疗工作台', 3000, 0, 'dashboard', 'his/dashboard/index', '', 1, 0, 'C', '0', '0', 'his:dashboard:list', 'dashboard', 'admin', now(), '医疗工作台');
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (3010, '基础数据', 3000, 1, 'base', null, '', 1, 0, 'M', '0', '0', '', 'system', 'admin', now(), '基础数据');
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (3011, '科室管理', 3010, 1, 'dept', 'his/base/dept/index', '', 1, 0, 'C', '0', '0', 'his:dept:list', 'tree', 'admin', now(), '科室管理');
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30111, '查询', 3011, 1, '', null, '', 1, 0, 'F', '0', '0', 'his:dept:query', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30112, '新增', 3011, 2, '', null, '', 1, 0, 'F', '0', '0', 'his:dept:add', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30113, '修改', 3011, 3, '', null, '', 1, 0, 'F', '0', '0', 'his:dept:edit', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30114, '删除', 3011, 4, '', null, '', 1, 0, 'F', '0', '0', 'his:dept:remove', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30115, '导出', 3011, 5, '', null, '', 1, 0, 'F', '0', '0', 'his:dept:export', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (3012, '医生管理', 3010, 2, 'doctor', 'his/base/doctor/index', '', 1, 0, 'C', '0', '0', 'his:doctor:list', 'peoples', 'admin', now(), '医生管理');
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30121, '查询', 3012, 1, '', null, '', 1, 0, 'F', '0', '0', 'his:doctor:query', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30122, '新增', 3012, 2, '', null, '', 1, 0, 'F', '0', '0', 'his:doctor:add', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30123, '修改', 3012, 3, '', null, '', 1, 0, 'F', '0', '0', 'his:doctor:edit', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30124, '删除', 3012, 4, '', null, '', 1, 0, 'F', '0', '0', 'his:doctor:remove', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30125, '导出', 3012, 5, '', null, '', 1, 0, 'F', '0', '0', 'his:doctor:export', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (3013, '患者档案', 3010, 3, 'patient', 'his/base/patient/index', '', 1, 0, 'C', '0', '0', 'his:patient:list', 'user', 'admin', now(), '患者档案');
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30131, '查询', 3013, 1, '', null, '', 1, 0, 'F', '0', '0', 'his:patient:query', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30132, '新增', 3013, 2, '', null, '', 1, 0, 'F', '0', '0', 'his:patient:add', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30133, '修改', 3013, 3, '', null, '', 1, 0, 'F', '0', '0', 'his:patient:edit', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30134, '删除', 3013, 4, '', null, '', 1, 0, 'F', '0', '0', 'his:patient:remove', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30135, '导出', 3013, 5, '', null, '', 1, 0, 'F', '0', '0', 'his:patient:export', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (3014, '药品信息', 3010, 4, 'drug', 'his/base/drug/index', '', 1, 0, 'C', '0', '0', 'his:drug:list', 'shopping', 'admin', now(), '药品信息');
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30141, '查询', 3014, 1, '', null, '', 1, 0, 'F', '0', '0', 'his:drug:query', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30142, '新增', 3014, 2, '', null, '', 1, 0, 'F', '0', '0', 'his:drug:add', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30143, '修改', 3014, 3, '', null, '', 1, 0, 'F', '0', '0', 'his:drug:edit', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30144, '删除', 3014, 4, '', null, '', 1, 0, 'F', '0', '0', 'his:drug:remove', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30145, '导出', 3014, 5, '', null, '', 1, 0, 'F', '0', '0', 'his:drug:export', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (3015, '供应商管理', 3010, 5, 'supplier', 'his/base/supplier/index', '', 1, 0, 'C', '0', '0', 'his:supplier:list', 'link', 'admin', now(), '供应商管理');
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30151, '查询', 3015, 1, '', null, '', 1, 0, 'F', '0', '0', 'his:supplier:query', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30152, '新增', 3015, 2, '', null, '', 1, 0, 'F', '0', '0', 'his:supplier:add', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30153, '修改', 3015, 3, '', null, '', 1, 0, 'F', '0', '0', 'his:supplier:edit', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30154, '删除', 3015, 4, '', null, '', 1, 0, 'F', '0', '0', 'his:supplier:remove', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30155, '导出', 3015, 5, '', null, '', 1, 0, 'F', '0', '0', 'his:supplier:export', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (3016, '收费项目', 3010, 6, 'feeitem', 'his/base/feeitem/index', '', 1, 0, 'C', '0', '0', 'his:feeitem:list', 'money', 'admin', now(), '收费项目');
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30161, '查询', 3016, 1, '', null, '', 1, 0, 'F', '0', '0', 'his:feeitem:query', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30162, '新增', 3016, 2, '', null, '', 1, 0, 'F', '0', '0', 'his:feeitem:add', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30163, '修改', 3016, 3, '', null, '', 1, 0, 'F', '0', '0', 'his:feeitem:edit', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30164, '删除', 3016, 4, '', null, '', 1, 0, 'F', '0', '0', 'his:feeitem:remove', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30165, '导出', 3016, 5, '', null, '', 1, 0, 'F', '0', '0', 'his:feeitem:export', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (3017, '病区管理', 3010, 7, 'ward', 'his/base/ward/index', '', 1, 0, 'C', '0', '0', 'his:ward:list', 'build', 'admin', now(), '病区管理');
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30171, '查询', 3017, 1, '', null, '', 1, 0, 'F', '0', '0', 'his:ward:query', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30172, '新增', 3017, 2, '', null, '', 1, 0, 'F', '0', '0', 'his:ward:add', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30173, '修改', 3017, 3, '', null, '', 1, 0, 'F', '0', '0', 'his:ward:edit', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30174, '删除', 3017, 4, '', null, '', 1, 0, 'F', '0', '0', 'his:ward:remove', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30175, '导出', 3017, 5, '', null, '', 1, 0, 'F', '0', '0', 'his:ward:export', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (3018, '床位管理', 3010, 8, 'bed', 'his/base/bed/index', '', 1, 0, 'C', '0', '0', 'his:bed:list', 'grid', 'admin', now(), '床位管理');
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30181, '查询', 3018, 1, '', null, '', 1, 0, 'F', '0', '0', 'his:bed:query', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30182, '新增', 3018, 2, '', null, '', 1, 0, 'F', '0', '0', 'his:bed:add', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30183, '修改', 3018, 3, '', null, '', 1, 0, 'F', '0', '0', 'his:bed:edit', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30184, '删除', 3018, 4, '', null, '', 1, 0, 'F', '0', '0', 'his:bed:remove', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30185, '导出', 3018, 5, '', null, '', 1, 0, 'F', '0', '0', 'his:bed:export', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (3020, '门诊管理', 3000, 2, 'outpatient', null, '', 1, 0, 'M', '0', '0', '', 'peoples', 'admin', now(), '门诊管理');
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (3021, '医生排班', 3020, 1, 'schedule', 'his/outpatient/schedule/index', '', 1, 0, 'C', '0', '0', 'his:schedule:list', 'date', 'admin', now(), '医生排班');
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30211, '查询', 3021, 1, '', null, '', 1, 0, 'F', '0', '0', 'his:schedule:query', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30212, '新增', 3021, 2, '', null, '', 1, 0, 'F', '0', '0', 'his:schedule:add', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30213, '修改', 3021, 3, '', null, '', 1, 0, 'F', '0', '0', 'his:schedule:edit', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30214, '删除', 3021, 4, '', null, '', 1, 0, 'F', '0', '0', 'his:schedule:remove', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30215, '导出', 3021, 5, '', null, '', 1, 0, 'F', '0', '0', 'his:schedule:export', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (3022, '挂号管理', 3020, 2, 'registration', 'his/outpatient/registration/index', '', 1, 0, 'C', '0', '0', 'his:registration:list', 'form', 'admin', now(), '挂号管理');
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30221, '挂号查询', 3022, 1, '', null, '', 1, 0, 'F', '0', '0', 'his:registration:query', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30222, '新增挂号', 3022, 2, '', null, '', 1, 0, 'F', '0', '0', 'his:registration:add', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30223, '退号', 3022, 3, '', null, '', 1, 0, 'F', '0', '0', 'his:registration:refund', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30224, '导出', 3022, 4, '', null, '', 1, 0, 'F', '0', '0', 'his:registration:export', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (3023, '候诊队列', 3020, 3, 'queue', 'his/outpatient/queue/index', '', 1, 0, 'C', '0', '0', 'his:queue:list', 'list', 'admin', now(), '候诊队列');
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30231, '查询', 3023, 1, '', null, '', 1, 0, 'F', '0', '0', 'his:queue:query', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30232, '叫号', 3023, 2, '', null, '', 1, 0, 'F', '0', '0', 'his:queue:call', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (3024, '医生工作站', 3020, 4, 'workstation', 'his/outpatient/workstation/index', '', 1, 0, 'C', '0', '0', 'his:workstation:list', 'edit', 'admin', now(), '医生工作站');
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30241, '接诊', 3024, 1, '', null, '', 1, 0, 'F', '0', '0', 'his:workstation:visit', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30242, '开处方', 3024, 2, '', null, '', 1, 0, 'F', '0', '0', 'his:workstation:rx', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (3025, '电子病历', 3020, 5, 'emr', 'his/outpatient/emr/index', '', 1, 0, 'C', '0', '0', 'his:visit:list', 'documentation', 'admin', now(), '电子病历');
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30251, '查询', 3025, 1, '', null, '', 1, 0, 'F', '0', '0', 'his:visit:query', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30252, '新增', 3025, 2, '', null, '', 1, 0, 'F', '0', '0', 'his:visit:add', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30253, '修改', 3025, 3, '', null, '', 1, 0, 'F', '0', '0', 'his:visit:edit', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30254, '删除', 3025, 4, '', null, '', 1, 0, 'F', '0', '0', 'his:visit:remove', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30255, '导出', 3025, 5, '', null, '', 1, 0, 'F', '0', '0', 'his:visit:export', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (3026, '处方管理', 3020, 6, 'prescription', 'his/outpatient/prescription/index', '', 1, 0, 'C', '0', '0', 'his:prescription:list', 'clipboard', 'admin', now(), '处方管理');
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30261, '处方查询', 3026, 1, '', null, '', 1, 0, 'F', '0', '0', 'his:prescription:query', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30262, '开具处方', 3026, 2, '', null, '', 1, 0, 'F', '0', '0', 'his:prescription:add', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30263, '作废', 3026, 3, '', null, '', 1, 0, 'F', '0', '0', 'his:prescription:cancel', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30264, '导出', 3026, 4, '', null, '', 1, 0, 'F', '0', '0', 'his:prescription:export', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (3027, '门诊收费', 3020, 7, 'charge', 'his/outpatient/charge/index', '', 1, 0, 'C', '0', '0', 'his:charge:list', 'money', 'admin', now(), '门诊收费');
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30271, '费用查询', 3027, 1, '', null, '', 1, 0, 'F', '0', '0', 'his:charge:query', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30272, '收费结算', 3027, 2, '', null, '', 1, 0, 'F', '0', '0', 'his:charge:settle', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30273, '退费', 3027, 3, '', null, '', 1, 0, 'F', '0', '0', 'his:charge:refund', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30274, '导出', 3027, 4, '', null, '', 1, 0, 'F', '0', '0', 'his:charge:export', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (3028, '药房发药', 3020, 8, 'dispense', 'his/outpatient/dispense/index', '', 1, 0, 'C', '0', '0', 'his:dispense:list', 'shopping', 'admin', now(), '药房发药');
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30281, '查询', 3028, 1, '', null, '', 1, 0, 'F', '0', '0', 'his:dispense:query', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30282, '发药', 3028, 2, '', null, '', 1, 0, 'F', '0', '0', 'his:dispense:dispense', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (3030, '住院管理', 3000, 3, 'inpatient', null, '', 1, 0, 'M', '0', '0', '', 'build', 'admin', now(), '住院管理');
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (3031, '住院登记', 3030, 1, 'admission', 'his/inpatient/admission/index', '', 1, 0, 'C', '0', '0', 'his:admission:list', 'form', 'admin', now(), '住院登记');
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30311, '查询', 3031, 1, '', null, '', 1, 0, 'F', '0', '0', 'his:admission:query', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30312, '新增', 3031, 2, '', null, '', 1, 0, 'F', '0', '0', 'his:admission:add', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30313, '修改', 3031, 3, '', null, '', 1, 0, 'F', '0', '0', 'his:admission:edit', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30314, '删除', 3031, 4, '', null, '', 1, 0, 'F', '0', '0', 'his:admission:remove', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30315, '导出', 3031, 5, '', null, '', 1, 0, 'F', '0', '0', 'his:admission:export', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (3032, '医嘱管理', 3030, 2, 'order', 'his/inpatient/order/index', '', 1, 0, 'C', '0', '0', 'his:order:list', 'list', 'admin', now(), '医嘱管理');
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30321, '医嘱查询', 3032, 1, '', null, '', 1, 0, 'F', '0', '0', 'his:order:query', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30322, '开立医嘱', 3032, 2, '', null, '', 1, 0, 'F', '0', '0', 'his:order:add', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30323, '执行', 3032, 3, '', null, '', 1, 0, 'F', '0', '0', 'his:order:exec', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30324, '停止', 3032, 4, '', null, '', 1, 0, 'F', '0', '0', 'his:order:stop', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30325, '导出', 3032, 5, '', null, '', 1, 0, 'F', '0', '0', 'his:order:export', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (3033, '护理记录', 3030, 3, 'nursing', 'his/inpatient/nursing/index', '', 1, 0, 'C', '0', '0', 'his:nursing:list', 'star', 'admin', now(), '护理记录');
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30331, '查询', 3033, 1, '', null, '', 1, 0, 'F', '0', '0', 'his:nursing:query', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30332, '新增', 3033, 2, '', null, '', 1, 0, 'F', '0', '0', 'his:nursing:add', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30333, '修改', 3033, 3, '', null, '', 1, 0, 'F', '0', '0', 'his:nursing:edit', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30334, '删除', 3033, 4, '', null, '', 1, 0, 'F', '0', '0', 'his:nursing:remove', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30335, '导出', 3033, 5, '', null, '', 1, 0, 'F', '0', '0', 'his:nursing:export', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (3034, '出院结算', 3030, 4, 'discharge', 'his/inpatient/discharge/index', '', 1, 0, 'C', '0', '0', 'his:discharge:list', 'money', 'admin', now(), '出院结算');
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30341, '查询', 3034, 1, '', null, '', 1, 0, 'F', '0', '0', 'his:discharge:query', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30342, '出院结算', 3034, 2, '', null, '', 1, 0, 'F', '0', '0', 'his:discharge:settle', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (3040, '医技管理', 3000, 4, 'medtech', null, '', 1, 0, 'M', '0', '0', '', 'monitor', 'admin', now(), '医技管理');
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (3041, '检验管理', 3040, 1, 'lab', 'his/medtech/lab/index', '', 1, 0, 'C', '0', '0', 'his:lab:list', 'search', 'admin', now(), '检验管理');
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30411, '检验查询', 3041, 1, '', null, '', 1, 0, 'F', '0', '0', 'his:lab:query', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30412, '新增申请', 3041, 2, '', null, '', 1, 0, 'F', '0', '0', 'his:lab:add', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30413, '流程操作', 3041, 3, '', null, '', 1, 0, 'F', '0', '0', 'his:lab:flow', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30414, '导出', 3041, 4, '', null, '', 1, 0, 'F', '0', '0', 'his:lab:export', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (3042, '检查管理', 3040, 2, 'exam', 'his/medtech/exam/index', '', 1, 0, 'C', '0', '0', 'his:exam:list', 'view', 'admin', now(), '检查管理');
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30421, '检查查询', 3042, 1, '', null, '', 1, 0, 'F', '0', '0', 'his:exam:query', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30422, '新增申请', 3042, 2, '', null, '', 1, 0, 'F', '0', '0', 'his:exam:add', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30423, '流程操作', 3042, 3, '', null, '', 1, 0, 'F', '0', '0', 'his:exam:flow', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30424, '导出', 3042, 4, '', null, '', 1, 0, 'F', '0', '0', 'his:exam:export', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (3043, '手术安排', 3040, 3, 'surgery', 'his/medtech/surgery/index', '', 1, 0, 'C', '0', '0', 'his:surgery:list', 'time', 'admin', now(), '手术安排');
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30431, '查询', 3043, 1, '', null, '', 1, 0, 'F', '0', '0', 'his:surgery:query', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30432, '新增', 3043, 2, '', null, '', 1, 0, 'F', '0', '0', 'his:surgery:add', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30433, '修改', 3043, 3, '', null, '', 1, 0, 'F', '0', '0', 'his:surgery:edit', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30434, '删除', 3043, 4, '', null, '', 1, 0, 'F', '0', '0', 'his:surgery:remove', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30435, '导出', 3043, 5, '', null, '', 1, 0, 'F', '0', '0', 'his:surgery:export', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (3050, '药库管理', 3000, 5, 'pharmacy', null, '', 1, 0, 'M', '0', '0', '', 'shopping', 'admin', now(), '药库管理');
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (3051, '库存查询', 3050, 1, 'stock', 'his/pharmacy/stock/index', '', 1, 0, 'C', '0', '0', 'his:stock:list', 'chart', 'admin', now(), '库存查询');
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30511, '查询', 3051, 1, '', null, '', 1, 0, 'F', '0', '0', 'his:stock:query', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30512, '导出', 3051, 2, '', null, '', 1, 0, 'F', '0', '0', 'his:stock:export', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (3052, '采购管理', 3050, 2, 'purchase', 'his/pharmacy/purchase/index', '', 1, 0, 'C', '0', '0', 'his:purchase:list', 'shopping', 'admin', now(), '采购管理');
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30521, '采购查询', 3052, 1, '', null, '', 1, 0, 'F', '0', '0', 'his:purchase:query', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30522, '新建采购', 3052, 2, '', null, '', 1, 0, 'F', '0', '0', 'his:purchase:add', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30523, '入库审核', 3052, 3, '', null, '', 1, 0, 'F', '0', '0', 'his:purchase:inbound', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30524, '导出', 3052, 4, '', null, '', 1, 0, 'F', '0', '0', 'his:purchase:export', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (3053, '出入库记录', 3050, 3, 'stockrecord', 'his/pharmacy/stockrecord/index', '', 1, 0, 'C', '0', '0', 'his:stockrecord:list', 'list', 'admin', now(), '出入库记录');
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30531, '查询', 3053, 1, '', null, '', 1, 0, 'F', '0', '0', 'his:stockrecord:query', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30532, '导出', 3053, 2, '', null, '', 1, 0, 'F', '0', '0', 'his:stockrecord:export', '#', 'admin', now());
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (3060, '统计报表', 3000, 6, 'report', null, '', 1, 0, 'M', '0', '0', '', 'chart', 'admin', now(), '统计报表');
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (3061, '经营统计', 3060, 1, 'statistics', 'his/report/statistics/index', '', 1, 0, 'C', '0', '0', 'his:statistics:list', 'chart', 'admin', now(), '经营统计');
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (30611, '查询', 3061, 1, '', null, '', 1, 0, 'F', '0', '0', 'his:statistics:query', '#', 'admin', now());

-- ----------------------------
-- HIS 演示数据
-- ----------------------------
insert into his_department(dept_id,parent_id,dept_name,dept_code,dept_type,location,phone,intro,order_num,status,create_by,create_time) values ('1','0','内科','NK','0','门诊楼2F','0571-88010001','心血管、呼吸、消化等内科疾病诊治','1','0','admin','2026-01-01 08:00:00');
insert into his_department(dept_id,parent_id,dept_name,dept_code,dept_type,location,phone,intro,order_num,status,create_by,create_time) values ('2','0','外科','WK','0','门诊楼3F','0571-88010002','普外、骨科、神经外科等手术治疗','2','0','admin','2026-01-01 08:00:00');
insert into his_department(dept_id,parent_id,dept_name,dept_code,dept_type,location,phone,intro,order_num,status,create_by,create_time) values ('3','0','儿科','EK','0','门诊楼4F','0571-88010003','儿童常见病、多发病诊治','3','0','admin','2026-01-01 08:00:00');
insert into his_department(dept_id,parent_id,dept_name,dept_code,dept_type,location,phone,intro,order_num,status,create_by,create_time) values ('4','0','妇产科','FCK','0','门诊楼4F','0571-88010004','妇科、产科诊疗与围产保健','4','0','admin','2026-01-01 08:00:00');
insert into his_department(dept_id,parent_id,dept_name,dept_code,dept_type,location,phone,intro,order_num,status,create_by,create_time) values ('5','0','眼科','YK','0','门诊楼5F','0571-88010005','眼科疾病、视光检查','5','0','admin','2026-01-01 08:00:00');
insert into his_department(dept_id,parent_id,dept_name,dept_code,dept_type,location,phone,intro,order_num,status,create_by,create_time) values ('6','0','口腔科','KQK','0','门诊楼5F','0571-88010006','口腔疾病诊治、牙齿修复','6','0','admin','2026-01-01 08:00:00');
insert into his_department(dept_id,parent_id,dept_name,dept_code,dept_type,location,phone,intro,order_num,status,create_by,create_time) values ('7','0','急诊科','JZK','0','急诊楼1F','0571-88010007','24小时急诊急救','7','0','admin','2026-01-01 08:00:00');
insert into his_department(dept_id,parent_id,dept_name,dept_code,dept_type,location,phone,intro,order_num,status,create_by,create_time) values ('8','0','中医科','ZYK','0','门诊楼6F','0571-88010008','中医内科、针灸推拿','8','0','admin','2026-01-01 08:00:00');
insert into his_department(dept_id,parent_id,dept_name,dept_code,dept_type,location,phone,intro,order_num,status,create_by,create_time) values ('9','0','检验科','JYK','2','医技楼2F','0571-88010009','临床检验、生化免疫检测','9','0','admin','2026-01-01 08:00:00');
insert into his_department(dept_id,parent_id,dept_name,dept_code,dept_type,location,phone,intro,order_num,status,create_by,create_time) values ('10','0','放射科','FSK','2','医技楼1F','0571-88010010','CT、MRI、X光影像检查','10','0','admin','2026-01-01 08:00:00');
insert into his_department(dept_id,parent_id,dept_name,dept_code,dept_type,location,phone,intro,order_num,status,create_by,create_time) values ('11','0','药房','YF','3','门诊楼1F','0571-88010011','门诊/住院药品调配发放','11','0','admin','2026-01-01 08:00:00');
insert into his_department(dept_id,parent_id,dept_name,dept_code,dept_type,location,phone,intro,order_num,status,create_by,create_time) values ('12','0','住院部','ZYB','1','住院楼','0571-88010012','住院病区管理','12','0','admin','2026-01-01 08:00:00');

insert into his_doctor(doctor_id,doctor_name,dept_id,title,gender,phone,specialty,reg_fee,intro,status,create_by,create_time) values ('1','王建国','1','0','0','13901010001','冠心病、高血压诊治','50','从医30年，心血管内科专家','0','admin','2026-01-02 08:00:00');
insert into his_doctor(doctor_id,doctor_name,dept_id,title,gender,phone,specialty,reg_fee,intro,status,create_by,create_time) values ('2','李秀英','1','1','1','13901010002','呼吸系统疾病','30','呼吸内科副主任医师','0','admin','2026-01-02 08:00:00');
insert into his_doctor(doctor_id,doctor_name,dept_id,title,gender,phone,specialty,reg_fee,intro,status,create_by,create_time) values ('3','张伟','1','2','0','13901010003','消化内科、胃肠疾病','20','消化内科主治医师','0','admin','2026-01-02 08:00:00');
insert into his_doctor(doctor_id,doctor_name,dept_id,title,gender,phone,specialty,reg_fee,intro,status,create_by,create_time) values ('4','刘志强','2','0','0','13901010004','普外科手术','50','外科主任医师，腹腔镜手术专家','0','admin','2026-01-02 08:00:00');
insert into his_doctor(doctor_id,doctor_name,dept_id,title,gender,phone,specialty,reg_fee,intro,status,create_by,create_time) values ('5','陈静','2','2','1','13901010005','骨科、创伤外科','20','骨科主治医师','0','admin','2026-01-02 08:00:00');
insert into his_doctor(doctor_id,doctor_name,dept_id,title,gender,phone,specialty,reg_fee,intro,status,create_by,create_time) values ('6','杨晓燕','3','1','1','13901010006','小儿呼吸道、消化道疾病','30','儿科副主任医师','0','admin','2026-01-02 08:00:00');
insert into his_doctor(doctor_id,doctor_name,dept_id,title,gender,phone,specialty,reg_fee,intro,status,create_by,create_time) values ('7','赵敏','3','3','1','13901010007','新生儿护理、儿童保健','10','儿科住院医师','0','admin','2026-01-02 08:00:00');
insert into his_doctor(doctor_id,doctor_name,dept_id,title,gender,phone,specialty,reg_fee,intro,status,create_by,create_time) values ('8','孙丽华','4','1','1','13901010008','产科、高危妊娠管理','30','妇产科副主任医师','0','admin','2026-01-02 08:00:00');
insert into his_doctor(doctor_id,doctor_name,dept_id,title,gender,phone,specialty,reg_fee,intro,status,create_by,create_time) values ('9','周涛','5','2','0','13901010009','白内障、青光眼','20','眼科主治医师','0','admin','2026-01-02 08:00:00');
insert into his_doctor(doctor_id,doctor_name,dept_id,title,gender,phone,specialty,reg_fee,intro,status,create_by,create_time) values ('10','吴国庆','6','2','0','13901010010','口腔种植、正畸','20','口腔科主治医师','0','admin','2026-01-02 08:00:00');
insert into his_doctor(doctor_id,doctor_name,dept_id,title,gender,phone,specialty,reg_fee,intro,status,create_by,create_time) values ('11','郑海涛','7','2','0','13901010011','急诊急救、创伤处理','20','急诊科主治医师','0','admin','2026-01-02 08:00:00');
insert into his_doctor(doctor_id,doctor_name,dept_id,title,gender,phone,specialty,reg_fee,intro,status,create_by,create_time) values ('12','冯雅琴','8','1','1','13901010012','中医内科、针灸','30','中医科副主任医师','0','admin','2026-01-02 08:00:00');
insert into his_doctor(doctor_id,doctor_name,dept_id,title,gender,phone,specialty,reg_fee,intro,status,create_by,create_time) values ('13','何军','2','3','0','13901010013','外科常见疾病','10','外科住院医师','0','admin','2026-01-02 08:00:00');
insert into his_doctor(doctor_id,doctor_name,dept_id,title,gender,phone,specialty,reg_fee,intro,status,create_by,create_time) values ('14','林黛玉','4','3','1','13901010014','妇科炎症、计划生育','10','妇产科住院医师','0','admin','2026-01-02 08:00:00');

insert into his_patient(patient_id,patient_no,patient_name,gender,birth_date,age,id_card,phone,address,blood_type,insurance_type,allergy,status,create_by,create_time) values ('1','MZ20260001','陈大明','0','1965-03-12','61','330102196503120011','13600010001','杭州市西湖区文三路100号','A','1','青霉素过敏','0','admin','2026-02-01 09:00:00');
insert into his_patient(patient_id,patient_no,patient_name,gender,birth_date,age,id_card,phone,address,blood_type,insurance_type,allergy,status,create_by,create_time) values ('2','MZ20260002','李淑芬','1','1972-07-25','54','330102197207250022','13600010002','杭州市拱墅区莫干山路200号','B','1','','0','admin','2026-02-01 09:10:00');
insert into his_patient(patient_id,patient_no,patient_name,gender,birth_date,age,id_card,phone,address,blood_type,insurance_type,allergy,status,create_by,create_time) values ('3','MZ20260003','王小宝','0','2019-11-05','6','330102201911050033','13600010003','杭州市江干区凯旋路300号','O','2','海鲜过敏','0','admin','2026-02-02 10:00:00');
insert into his_patient(patient_id,patient_no,patient_name,gender,birth_date,age,id_card,phone,address,blood_type,insurance_type,allergy,status,create_by,create_time) values ('4','MZ20260004','张桂花','1','1958-01-30','68','330102195801300044','13600010004','杭州市下城区朝晖路400号','AB','1','','0','admin','2026-02-02 10:20:00');
insert into his_patient(patient_id,patient_no,patient_name,gender,birth_date,age,id_card,phone,address,blood_type,insurance_type,allergy,status,create_by,create_time) values ('5','MZ20260005','刘建军','0','1985-09-18','41','330102198509180055','13600010005','杭州市滨江区江南大道500号','A','3','','0','admin','2026-02-03 08:30:00');
insert into his_patient(patient_id,patient_no,patient_name,gender,birth_date,age,id_card,phone,address,blood_type,insurance_type,allergy,status,create_by,create_time) values ('6','MZ20260006','周淑华','1','1990-04-22','36','330102199004220066','13600010006','杭州市余杭区文一西路600号','O','1','头孢过敏','0','admin','2026-02-03 09:00:00');
insert into his_patient(patient_id,patient_no,patient_name,gender,birth_date,age,id_card,phone,address,blood_type,insurance_type,allergy,status,create_by,create_time) values ('7','MZ20260007','吴国强','0','1978-12-08','47','330102197812080077','13600010007','杭州市萧山区市心中路700号','B','1','','0','admin','2026-02-04 11:00:00');
insert into his_patient(patient_id,patient_no,patient_name,gender,birth_date,age,id_card,phone,address,blood_type,insurance_type,allergy,status,create_by,create_time) values ('8','MZ20260008','郑美玲','1','1995-06-15','31','330102199506150088','13600010008','杭州市上城区中山中路800号','A','0','','0','admin','2026-02-04 14:00:00');
insert into his_patient(patient_id,patient_no,patient_name,gender,birth_date,age,id_card,phone,address,blood_type,insurance_type,allergy,status,create_by,create_time) values ('9','MZ20260009','孙志远','0','2000-02-14','26','330102200002140099','13600010009','杭州市富阳区桂花西路900号','O','0','','0','admin','2026-02-05 08:00:00');
insert into his_patient(patient_id,patient_no,patient_name,gender,birth_date,age,id_card,phone,address,blood_type,insurance_type,allergy,status,create_by,create_time) values ('10','MZ20260010','林小妹','1','2018-08-30','8','330102201808300100','13600010010','杭州市临安区钱王街1000号','A','2','','0','admin','2026-02-05 09:30:00');
insert into his_patient(patient_id,patient_no,patient_name,gender,birth_date,age,id_card,phone,address,blood_type,insurance_type,allergy,status,create_by,create_time) values ('11','MZ20260011','黄建军','0','1960-10-01','65','330102196010010111','13600010011','杭州市西湖区转塘街道11号','B','1','磺胺类过敏','0','admin','2026-02-06 08:00:00');
insert into his_patient(patient_id,patient_no,patient_name,gender,birth_date,age,id_card,phone,address,blood_type,insurance_type,allergy,status,create_by,create_time) values ('12','MZ20260012','徐丽丽','1','1988-05-20','38','330102198805200122','13600010012','杭州市拱墅区上塘路1200号','AB','1','','0','admin','2026-02-06 10:00:00');
insert into his_patient(patient_id,patient_no,patient_name,gender,birth_date,age,id_card,phone,address,blood_type,insurance_type,allergy,status,create_by,create_time) values ('13','MZ20260013','马文轩','0','2015-03-08','11','330102201503080133','13600010013','杭州市江干区艮山西路1300号','O','2','','0','admin','2026-02-07 09:00:00');
insert into his_patient(patient_id,patient_no,patient_name,gender,birth_date,age,id_card,phone,address,blood_type,insurance_type,allergy,status,create_by,create_time) values ('14','MZ20260014','朱桂香','1','1955-11-11','70','330102195511110144','13600010014','杭州市下城区环城北路1400号','A','1','','0','admin','2026-02-07 15:00:00');
insert into his_patient(patient_id,patient_no,patient_name,gender,birth_date,age,id_card,phone,address,blood_type,insurance_type,allergy,status,create_by,create_time) values ('15','MZ20260015','胡永强','0','1982-07-07','44','330102198207070155','13600010015','杭州市滨江区浦沿街道1500号','B','3','','0','admin','2026-02-08 08:30:00');
insert into his_patient(patient_id,patient_no,patient_name,gender,birth_date,age,id_card,phone,address,blood_type,insurance_type,allergy,status,create_by,create_time) values ('16','MZ20260016','高秀兰','1','1993-09-25','32','330102199309250166','13600010016','杭州市余杭区临平街道1600号','O','1','','0','admin','2026-02-08 16:00:00');

insert into his_drug(drug_id,drug_code,drug_name,category,specification,unit,price,stock,stock_warn,manufacturer,approval_no,is_prescription,status,create_by,create_time) values ('1','YP001','阿莫西林胶囊','0','0.25g*24粒','盒','12.5','300','50','华北制药','国药准字H20003035','Y','0','admin','2026-01-05 08:00:00');
insert into his_drug(drug_id,drug_code,drug_name,category,specification,unit,price,stock,stock_warn,manufacturer,approval_no,is_prescription,status,create_by,create_time) values ('2','YP002','布洛芬缓释胶囊','2','0.3g*20粒','盒','15.8','260','50','中美天津史克','国药准字H10900089','N','0','admin','2026-01-05 08:00:00');
insert into his_drug(drug_id,drug_code,drug_name,category,specification,unit,price,stock,stock_warn,manufacturer,approval_no,is_prescription,status,create_by,create_time) values ('3','YP003','连花清瘟胶囊','3','0.35g*24粒','盒','14.8','180','60','以岭药业','国药准字Z20040063','N','0','admin','2026-01-05 08:00:00');
insert into his_drug(drug_id,drug_code,drug_name,category,specification,unit,price,stock,stock_warn,manufacturer,approval_no,is_prescription,status,create_by,create_time) values ('4','YP004','头孢克肟分散片','0','0.1g*12片','盒','22','150','50','广州白云山','国药准字H20041663','Y','0','admin','2026-01-05 08:00:00');
insert into his_drug(drug_id,drug_code,drug_name,category,specification,unit,price,stock,stock_warn,manufacturer,approval_no,is_prescription,status,create_by,create_time) values ('5','YP005','二甲双胍片','1','0.5g*60片','瓶','18.5','220','50','中美上海施贵宝','国药准字H20023370','Y','0','admin','2026-01-05 08:00:00');
insert into his_drug(drug_id,drug_code,drug_name,category,specification,unit,price,stock,stock_warn,manufacturer,approval_no,is_prescription,status,create_by,create_time) values ('6','YP006','硝苯地平控释片','1','30mg*7片','盒','28','90','40','拜耳医药','国药准字J20180025','Y','0','admin','2026-01-05 08:00:00');
insert into his_drug(drug_id,drug_code,drug_name,category,specification,unit,price,stock,stock_warn,manufacturer,approval_no,is_prescription,status,create_by,create_time) values ('7','YP007','阿托伐他汀钙片','1','20mg*7片','盒','32','120','40','辉瑞制药','国药准字H20051407','Y','0','admin','2026-01-05 08:00:00');
insert into his_drug(drug_id,drug_code,drug_name,category,specification,unit,price,stock,stock_warn,manufacturer,approval_no,is_prescription,status,create_by,create_time) values ('8','YP008','奥美拉唑肠溶胶囊','1','20mg*14粒','盒','16.5','200','50','阿斯利康','国药准字H20030412','Y','0','admin','2026-01-05 08:00:00');
insert into his_drug(drug_id,drug_code,drug_name,category,specification,unit,price,stock,stock_warn,manufacturer,approval_no,is_prescription,status,create_by,create_time) values ('9','YP009','氯雷他定片','9','10mg*6片','盒','11','160','50','拜耳医药','国药准字H20070030','N','0','admin','2026-01-05 08:00:00');
insert into his_drug(drug_id,drug_code,drug_name,category,specification,unit,price,stock,stock_warn,manufacturer,approval_no,is_prescription,status,create_by,create_time) values ('10','YP010','维生素C片','9','0.1g*100片','瓶','8','300','80','东北制药','国药准字H21020713','N','0','admin','2026-01-05 08:00:00');
insert into his_drug(drug_id,drug_code,drug_name,category,specification,unit,price,stock,stock_warn,manufacturer,approval_no,is_prescription,status,create_by,create_time) values ('11','YP011','板蓝根颗粒','3','10g*20袋','盒','9.8','240','60','白云山医药','国药准字Z44023485','N','0','admin','2026-01-05 08:00:00');
insert into his_drug(drug_id,drug_code,drug_name,category,specification,unit,price,stock,stock_warn,manufacturer,approval_no,is_prescription,status,create_by,create_time) values ('12','YP012','葡萄糖注射液','4','5% 250ml','瓶','5.5','500','100','科伦药业','国药准字H51020634','Y','0','admin','2026-01-05 08:00:00');
insert into his_drug(drug_id,drug_code,drug_name,category,specification,unit,price,stock,stock_warn,manufacturer,approval_no,is_prescription,status,create_by,create_time) values ('13','YP013','氯化钠注射液','4','0.9% 250ml','瓶','4.8','600','100','科伦药业','国药准字H51021156','Y','0','admin','2026-01-05 08:00:00');
insert into his_drug(drug_id,drug_code,drug_name,category,specification,unit,price,stock,stock_warn,manufacturer,approval_no,is_prescription,status,create_by,create_time) values ('14','YP014','碘伏消毒液','5','100ml','瓶','6.5','150','40','利尔康','卫消字2010第0015号','N','0','admin','2026-01-05 08:00:00');
insert into his_drug(drug_id,drug_code,drug_name,category,specification,unit,price,stock,stock_warn,manufacturer,approval_no,is_prescription,status,create_by,create_time) values ('15','YP015','云南白药气雾剂','5','85g+30g','盒','35','80','30','云南白药','国药准字Z53021107','N','0','admin','2026-01-05 08:00:00');
insert into his_drug(drug_id,drug_code,drug_name,category,specification,unit,price,stock,stock_warn,manufacturer,approval_no,is_prescription,status,create_by,create_time) values ('16','YP016','藿香正气水','3','10ml*10支','盒','12','45','50','同仁堂','国药准字Z11020377','N','0','admin','2026-01-05 08:00:00');
insert into his_drug(drug_id,drug_code,drug_name,category,specification,unit,price,stock,stock_warn,manufacturer,approval_no,is_prescription,status,create_by,create_time) values ('17','YP017','地塞米松片','0','0.75mg*100片','瓶','15','35','40','天津力生','国药准字H12020136','Y','0','admin','2026-01-05 08:00:00');
insert into his_drug(drug_id,drug_code,drug_name,category,specification,unit,price,stock,stock_warn,manufacturer,approval_no,is_prescription,status,create_by,create_time) values ('18','YP018','门冬胰岛素注射液','4','3ml:300单位','支','68','60','30','诺和诺德','国药准字J20150066','Y','0','admin','2026-01-05 08:00:00');
insert into his_drug(drug_id,drug_code,drug_name,category,specification,unit,price,stock,stock_warn,manufacturer,approval_no,is_prescription,status,create_by,create_time) values ('19','YP019','孟鲁司特钠咀嚼片','1','5mg*14片','盒','25.5','110','40','默沙东','国药准字J20130053','Y','0','admin','2026-01-05 08:00:00');
insert into his_drug(drug_id,drug_code,drug_name,category,specification,unit,price,stock,stock_warn,manufacturer,approval_no,is_prescription,status,create_by,create_time) values ('20','YP020','蒙脱石散','3','3g*10袋','盒','13.5','190','50','博福-益普生','国药准字H20000690','N','0','admin','2026-01-05 08:00:00');
insert into his_drug(drug_id,drug_code,drug_name,category,specification,unit,price,stock,stock_warn,manufacturer,approval_no,is_prescription,status,create_by,create_time) values ('21','YP021','左氧氟沙星片','0','0.5g*7片','盒','19.8','130','50','第一三共','国药准字H20040091','Y','0','admin','2026-01-05 08:00:00');
insert into his_drug(drug_id,drug_code,drug_name,category,specification,unit,price,stock,stock_warn,manufacturer,approval_no,is_prescription,status,create_by,create_time) values ('22','YP022','复方氨酚烷胺片','2','10片','盒','8.5','25','50','感康药业','国药准字H22026193','N','0','admin','2026-01-05 08:00:00');
insert into his_drug(drug_id,drug_code,drug_name,category,specification,unit,price,stock,stock_warn,manufacturer,approval_no,is_prescription,status,create_by,create_time) values ('23','YP023','复方甘草片','3','100片','瓶','11.5','170','50','太极集团','国药准字Z20093007','N','0','admin','2026-01-05 08:00:00');
insert into his_drug(drug_id,drug_code,drug_name,category,specification,unit,price,stock,stock_warn,manufacturer,approval_no,is_prescription,status,create_by,create_time) values ('24','YP024','注射用头孢曲松钠','4','1g/支','支','22','200','60','罗氏制药','国药准字H10983037','Y','0','admin','2026-01-05 08:00:00');

insert into his_supplier(supplier_id,supplier_name,contact,phone,address,status,create_by,create_time) values ('1','国药控股股份有限公司','张经理','021-23050001','上海市黄浦区福州路221号','0','admin','2026-01-06 08:00:00');
insert into his_supplier(supplier_id,supplier_name,contact,phone,address,status,create_by,create_time) values ('2','华润医药商业集团','李经理','010-65199000','北京市东城区安定门内大街257号','0','admin','2026-01-06 08:00:00');
insert into his_supplier(supplier_id,supplier_name,contact,phone,address,status,create_by,create_time) values ('3','九州通医药集团','王经理','027-84683001','武汉市汉阳区龙阳大道特8号','0','admin','2026-01-06 08:00:00');
insert into his_supplier(supplier_id,supplier_name,contact,phone,address,status,create_by,create_time) values ('4','上海医药集团','陈经理','021-63730900','上海市黄浦区太仓路200号','0','admin','2026-01-06 08:00:00');
insert into his_supplier(supplier_id,supplier_name,contact,phone,address,status,create_by,create_time) values ('5','广州医药有限公司','刘经理','020-81803000','广州市荔湾区大同路103号','0','admin','2026-01-06 08:00:00');

insert into his_fee_item(item_id,item_name,category,price,unit,status,create_by,create_time) values ('1','血常规五分类','0','25','次','0','admin','2026-01-07 08:00:00');
insert into his_fee_item(item_id,item_name,category,price,unit,status,create_by,create_time) values ('2','尿常规十一项','0','15','次','0','admin','2026-01-07 08:00:00');
insert into his_fee_item(item_id,item_name,category,price,unit,status,create_by,create_time) values ('3','肝功能全套','0','85','次','0','admin','2026-01-07 08:00:00');
insert into his_fee_item(item_id,item_name,category,price,unit,status,create_by,create_time) values ('4','肾功能三项','0','45','次','0','admin','2026-01-07 08:00:00');
insert into his_fee_item(item_id,item_name,category,price,unit,status,create_by,create_time) values ('5','血糖检测','0','12','次','0','admin','2026-01-07 08:00:00');
insert into his_fee_item(item_id,item_name,category,price,unit,status,create_by,create_time) values ('6','CT平扫(单部位)','1','280','次','0','admin','2026-01-07 08:00:00');
insert into his_fee_item(item_id,item_name,category,price,unit,status,create_by,create_time) values ('7','MRI核磁共振','1','680','次','0','admin','2026-01-07 08:00:00');
insert into his_fee_item(item_id,item_name,category,price,unit,status,create_by,create_time) values ('8','数字化X光摄影','1','80','次','0','admin','2026-01-07 08:00:00');
insert into his_fee_item(item_id,item_name,category,price,unit,status,create_by,create_time) values ('9','彩色B超','1','120','次','0','admin','2026-01-07 08:00:00');
insert into his_fee_item(item_id,item_name,category,price,unit,status,create_by,create_time) values ('10','十二导联心电图','1','40','次','0','admin','2026-01-07 08:00:00');
insert into his_fee_item(item_id,item_name,category,price,unit,status,create_by,create_time) values ('11','普通门诊诊查费','5','10','次','0','admin','2026-01-07 08:00:00');
insert into his_fee_item(item_id,item_name,category,price,unit,status,create_by,create_time) values ('12','静脉输液费','4','15','次','0','admin','2026-01-07 08:00:00');
insert into his_fee_item(item_id,item_name,category,price,unit,status,create_by,create_time) values ('13','肌肉注射费','4','10','次','0','admin','2026-01-07 08:00:00');
insert into his_fee_item(item_id,item_name,category,price,unit,status,create_by,create_time) values ('14','一级护理费','4','30','天','0','admin','2026-01-07 08:00:00');
insert into his_fee_item(item_id,item_name,category,price,unit,status,create_by,create_time) values ('15','换药费','9','20','次','0','admin','2026-01-07 08:00:00');

insert into his_ward(ward_id,ward_name,dept_id,location,nurse_count,status,create_by,create_time) values ('1','内科一病区','1','住院楼3F','8','0','admin','2026-01-08 08:00:00');
insert into his_ward(ward_id,ward_name,dept_id,location,nurse_count,status,create_by,create_time) values ('2','内科二病区','1','住院楼4F','8','0','admin','2026-01-08 08:00:00');
insert into his_ward(ward_id,ward_name,dept_id,location,nurse_count,status,create_by,create_time) values ('3','外科病区','2','住院楼5F','10','0','admin','2026-01-08 08:00:00');
insert into his_ward(ward_id,ward_name,dept_id,location,nurse_count,status,create_by,create_time) values ('4','儿科病区','3','住院楼6F','6','0','admin','2026-01-08 08:00:00');
insert into his_ward(ward_id,ward_name,dept_id,location,nurse_count,status,create_by,create_time) values ('5','重症ICU病区','2','住院楼7F','12','0','admin','2026-01-08 08:00:00');

insert into his_bed(bed_id,ward_id,bed_no,bed_type,price_per_day,status,create_by,create_time) values ('1','1','01-01','0','60','1','admin','2026-01-08 09:00:00');
insert into his_bed(bed_id,ward_id,bed_no,bed_type,price_per_day,status,create_by,create_time) values ('2','1','01-02','0','60','1','admin','2026-01-08 09:00:00');
insert into his_bed(bed_id,ward_id,bed_no,bed_type,price_per_day,status,create_by,create_time) values ('3','1','01-03','0','60','1','admin','2026-01-08 09:00:00');
insert into his_bed(bed_id,ward_id,bed_no,bed_type,price_per_day,status,create_by,create_time) values ('4','1','01-04','0','60','0','admin','2026-01-08 09:00:00');
insert into his_bed(bed_id,ward_id,bed_no,bed_type,price_per_day,status,create_by,create_time) values ('5','1','01-05','0','60','0','admin','2026-01-08 09:00:00');
insert into his_bed(bed_id,ward_id,bed_no,bed_type,price_per_day,status,create_by,create_time) values ('6','1','01-06','0','60','0','admin','2026-01-08 09:00:00');
insert into his_bed(bed_id,ward_id,bed_no,bed_type,price_per_day,status,create_by,create_time) values ('7','1','01-07','2','300','0','admin','2026-01-08 09:00:00');
insert into his_bed(bed_id,ward_id,bed_no,bed_type,price_per_day,status,create_by,create_time) values ('8','1','01-08','2','300','0','admin','2026-01-08 09:00:00');
insert into his_bed(bed_id,ward_id,bed_no,bed_type,price_per_day,status,create_by,create_time) values ('9','2','02-01','0','60','1','admin','2026-01-08 09:00:00');
insert into his_bed(bed_id,ward_id,bed_no,bed_type,price_per_day,status,create_by,create_time) values ('10','2','02-02','0','60','1','admin','2026-01-08 09:00:00');
insert into his_bed(bed_id,ward_id,bed_no,bed_type,price_per_day,status,create_by,create_time) values ('11','2','02-03','0','60','0','admin','2026-01-08 09:00:00');
insert into his_bed(bed_id,ward_id,bed_no,bed_type,price_per_day,status,create_by,create_time) values ('12','2','02-04','0','60','0','admin','2026-01-08 09:00:00');
insert into his_bed(bed_id,ward_id,bed_no,bed_type,price_per_day,status,create_by,create_time) values ('13','2','02-05','0','60','0','admin','2026-01-08 09:00:00');
insert into his_bed(bed_id,ward_id,bed_no,bed_type,price_per_day,status,create_by,create_time) values ('14','2','02-06','0','60','0','admin','2026-01-08 09:00:00');
insert into his_bed(bed_id,ward_id,bed_no,bed_type,price_per_day,status,create_by,create_time) values ('15','2','02-07','2','300','0','admin','2026-01-08 09:00:00');
insert into his_bed(bed_id,ward_id,bed_no,bed_type,price_per_day,status,create_by,create_time) values ('16','2','02-08','2','300','0','admin','2026-01-08 09:00:00');
insert into his_bed(bed_id,ward_id,bed_no,bed_type,price_per_day,status,create_by,create_time) values ('17','3','03-01','0','60','1','admin','2026-01-08 09:00:00');
insert into his_bed(bed_id,ward_id,bed_no,bed_type,price_per_day,status,create_by,create_time) values ('18','3','03-02','0','60','1','admin','2026-01-08 09:00:00');
insert into his_bed(bed_id,ward_id,bed_no,bed_type,price_per_day,status,create_by,create_time) values ('19','3','03-03','0','60','0','admin','2026-01-08 09:00:00');
insert into his_bed(bed_id,ward_id,bed_no,bed_type,price_per_day,status,create_by,create_time) values ('20','3','03-04','0','60','0','admin','2026-01-08 09:00:00');
insert into his_bed(bed_id,ward_id,bed_no,bed_type,price_per_day,status,create_by,create_time) values ('21','3','03-05','0','60','0','admin','2026-01-08 09:00:00');
insert into his_bed(bed_id,ward_id,bed_no,bed_type,price_per_day,status,create_by,create_time) values ('22','3','03-06','0','60','0','admin','2026-01-08 09:00:00');
insert into his_bed(bed_id,ward_id,bed_no,bed_type,price_per_day,status,create_by,create_time) values ('23','3','03-07','2','300','0','admin','2026-01-08 09:00:00');
insert into his_bed(bed_id,ward_id,bed_no,bed_type,price_per_day,status,create_by,create_time) values ('24','3','03-08','2','300','0','admin','2026-01-08 09:00:00');
insert into his_bed(bed_id,ward_id,bed_no,bed_type,price_per_day,status,create_by,create_time) values ('25','4','04-01','0','60','1','admin','2026-01-08 09:00:00');
insert into his_bed(bed_id,ward_id,bed_no,bed_type,price_per_day,status,create_by,create_time) values ('26','4','04-02','0','60','0','admin','2026-01-08 09:00:00');
insert into his_bed(bed_id,ward_id,bed_no,bed_type,price_per_day,status,create_by,create_time) values ('27','4','04-03','0','60','0','admin','2026-01-08 09:00:00');
insert into his_bed(bed_id,ward_id,bed_no,bed_type,price_per_day,status,create_by,create_time) values ('28','4','04-04','0','60','0','admin','2026-01-08 09:00:00');
insert into his_bed(bed_id,ward_id,bed_no,bed_type,price_per_day,status,create_by,create_time) values ('29','4','04-05','0','60','0','admin','2026-01-08 09:00:00');
insert into his_bed(bed_id,ward_id,bed_no,bed_type,price_per_day,status,create_by,create_time) values ('30','4','04-06','0','60','0','admin','2026-01-08 09:00:00');
insert into his_bed(bed_id,ward_id,bed_no,bed_type,price_per_day,status,create_by,create_time) values ('31','4','04-07','2','300','0','admin','2026-01-08 09:00:00');
insert into his_bed(bed_id,ward_id,bed_no,bed_type,price_per_day,status,create_by,create_time) values ('32','4','04-08','2','300','0','admin','2026-01-08 09:00:00');
insert into his_bed(bed_id,ward_id,bed_no,bed_type,price_per_day,status,create_by,create_time) values ('33','5','ICU-01','1','180','1','admin','2026-01-08 09:00:00');
insert into his_bed(bed_id,ward_id,bed_no,bed_type,price_per_day,status,create_by,create_time) values ('34','5','ICU-02','1','180','1','admin','2026-01-08 09:00:00');
insert into his_bed(bed_id,ward_id,bed_no,bed_type,price_per_day,status,create_by,create_time) values ('35','5','ICU-03','1','180','0','admin','2026-01-08 09:00:00');
insert into his_bed(bed_id,ward_id,bed_no,bed_type,price_per_day,status,create_by,create_time) values ('36','5','ICU-04','1','180','0','admin','2026-01-08 09:00:00');
insert into his_bed(bed_id,ward_id,bed_no,bed_type,price_per_day,status,create_by,create_time) values ('37','5','ICU-05','1','180','0','admin','2026-01-08 09:00:00');
insert into his_bed(bed_id,ward_id,bed_no,bed_type,price_per_day,status,create_by,create_time) values ('38','5','ICU-06','1','180','0','admin','2026-01-08 09:00:00');

insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('1','1','1',date_sub(curdate(), interval 6 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('2','1','1',date_sub(curdate(), interval 6 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('3','1','2',date_sub(curdate(), interval 6 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('4','1','2',date_sub(curdate(), interval 6 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('5','1','3',date_sub(curdate(), interval 6 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('6','1','3',date_sub(curdate(), interval 6 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('7','2','4',date_sub(curdate(), interval 6 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('8','2','4',date_sub(curdate(), interval 6 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('9','2','5',date_sub(curdate(), interval 6 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('10','2','5',date_sub(curdate(), interval 6 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('11','3','6',date_sub(curdate(), interval 6 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('12','3','6',date_sub(curdate(), interval 6 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('13','3','7',date_sub(curdate(), interval 6 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('14','3','7',date_sub(curdate(), interval 6 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('15','4','8',date_sub(curdate(), interval 6 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('16','4','8',date_sub(curdate(), interval 6 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('17','1','1',date_sub(curdate(), interval 5 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('18','1','1',date_sub(curdate(), interval 5 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('19','1','2',date_sub(curdate(), interval 5 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('20','1','2',date_sub(curdate(), interval 5 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('21','1','3',date_sub(curdate(), interval 5 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('22','1','3',date_sub(curdate(), interval 5 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('23','2','4',date_sub(curdate(), interval 5 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('24','2','4',date_sub(curdate(), interval 5 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('25','2','5',date_sub(curdate(), interval 5 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('26','2','5',date_sub(curdate(), interval 5 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('27','3','6',date_sub(curdate(), interval 5 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('28','3','6',date_sub(curdate(), interval 5 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('29','3','7',date_sub(curdate(), interval 5 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('30','3','7',date_sub(curdate(), interval 5 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('31','4','8',date_sub(curdate(), interval 5 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('32','4','8',date_sub(curdate(), interval 5 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('33','1','1',date_sub(curdate(), interval 4 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('34','1','1',date_sub(curdate(), interval 4 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('35','1','2',date_sub(curdate(), interval 4 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('36','1','2',date_sub(curdate(), interval 4 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('37','1','3',date_sub(curdate(), interval 4 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('38','1','3',date_sub(curdate(), interval 4 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('39','2','4',date_sub(curdate(), interval 4 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('40','2','4',date_sub(curdate(), interval 4 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('41','2','5',date_sub(curdate(), interval 4 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('42','2','5',date_sub(curdate(), interval 4 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('43','3','6',date_sub(curdate(), interval 4 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('44','3','6',date_sub(curdate(), interval 4 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('45','3','7',date_sub(curdate(), interval 4 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('46','3','7',date_sub(curdate(), interval 4 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('47','4','8',date_sub(curdate(), interval 4 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('48','4','8',date_sub(curdate(), interval 4 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('49','1','1',date_sub(curdate(), interval 3 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('50','1','1',date_sub(curdate(), interval 3 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('51','1','2',date_sub(curdate(), interval 3 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('52','1','2',date_sub(curdate(), interval 3 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('53','1','3',date_sub(curdate(), interval 3 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('54','1','3',date_sub(curdate(), interval 3 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('55','2','4',date_sub(curdate(), interval 3 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('56','2','4',date_sub(curdate(), interval 3 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('57','2','5',date_sub(curdate(), interval 3 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('58','2','5',date_sub(curdate(), interval 3 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('59','3','6',date_sub(curdate(), interval 3 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('60','3','6',date_sub(curdate(), interval 3 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('61','3','7',date_sub(curdate(), interval 3 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('62','3','7',date_sub(curdate(), interval 3 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('63','4','8',date_sub(curdate(), interval 3 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('64','4','8',date_sub(curdate(), interval 3 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('65','1','1',date_sub(curdate(), interval 2 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('66','1','1',date_sub(curdate(), interval 2 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('67','1','2',date_sub(curdate(), interval 2 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('68','1','2',date_sub(curdate(), interval 2 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('69','1','3',date_sub(curdate(), interval 2 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('70','1','3',date_sub(curdate(), interval 2 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('71','2','4',date_sub(curdate(), interval 2 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('72','2','4',date_sub(curdate(), interval 2 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('73','2','5',date_sub(curdate(), interval 2 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('74','2','5',date_sub(curdate(), interval 2 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('75','3','6',date_sub(curdate(), interval 2 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('76','3','6',date_sub(curdate(), interval 2 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('77','3','7',date_sub(curdate(), interval 2 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('78','3','7',date_sub(curdate(), interval 2 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('79','4','8',date_sub(curdate(), interval 2 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('80','4','8',date_sub(curdate(), interval 2 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('81','1','1',date_sub(curdate(), interval 1 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('82','1','1',date_sub(curdate(), interval 1 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('83','1','2',date_sub(curdate(), interval 1 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('84','1','2',date_sub(curdate(), interval 1 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('85','1','3',date_sub(curdate(), interval 1 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('86','1','3',date_sub(curdate(), interval 1 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('87','2','4',date_sub(curdate(), interval 1 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('88','2','4',date_sub(curdate(), interval 1 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('89','2','5',date_sub(curdate(), interval 1 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('90','2','5',date_sub(curdate(), interval 1 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('91','3','6',date_sub(curdate(), interval 1 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('92','3','6',date_sub(curdate(), interval 1 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('93','3','7',date_sub(curdate(), interval 1 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('94','3','7',date_sub(curdate(), interval 1 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('95','4','8',date_sub(curdate(), interval 1 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('96','4','8',date_sub(curdate(), interval 1 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('97','1','1',curdate(),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('98','1','1',curdate(),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('99','1','2',curdate(),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('100','1','2',curdate(),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('101','1','3',curdate(),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('102','1','3',curdate(),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('103','2','4',curdate(),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('104','2','4',curdate(),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('105','2','5',curdate(),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('106','2','5',curdate(),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('107','3','6',curdate(),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('108','3','6',curdate(),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('109','3','7',curdate(),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('110','3','7',curdate(),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('111','4','8',curdate(),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('112','4','8',curdate(),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('113','1','1',date_add(curdate(), interval 1 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('114','1','1',date_add(curdate(), interval 1 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('115','1','2',date_add(curdate(), interval 1 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('116','1','2',date_add(curdate(), interval 1 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('117','1','3',date_add(curdate(), interval 1 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('118','1','3',date_add(curdate(), interval 1 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('119','2','4',date_add(curdate(), interval 1 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('120','2','4',date_add(curdate(), interval 1 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('121','2','5',date_add(curdate(), interval 1 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('122','2','5',date_add(curdate(), interval 1 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('123','3','6',date_add(curdate(), interval 1 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('124','3','6',date_add(curdate(), interval 1 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('125','3','7',date_add(curdate(), interval 1 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('126','3','7',date_add(curdate(), interval 1 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('127','4','8',date_add(curdate(), interval 1 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('128','4','8',date_add(curdate(), interval 1 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('129','1','1',date_add(curdate(), interval 2 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('130','1','1',date_add(curdate(), interval 2 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('131','1','2',date_add(curdate(), interval 2 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('132','1','2',date_add(curdate(), interval 2 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('133','1','3',date_add(curdate(), interval 2 day),'0','20','20','1','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('134','1','3',date_add(curdate(), interval 2 day),'1','20','20','1','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('135','2','4',date_add(curdate(), interval 2 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('136','2','4',date_add(curdate(), interval 2 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('137','2','5',date_add(curdate(), interval 2 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('138','2','5',date_add(curdate(), interval 2 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('139','3','6',date_add(curdate(), interval 2 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('140','3','6',date_add(curdate(), interval 2 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('141','3','7',date_add(curdate(), interval 2 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('142','3','7',date_add(curdate(), interval 2 day),'1','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('143','4','8',date_add(curdate(), interval 2 day),'0','20','20','0','admin','2026-01-08 09:00:00');
insert into his_schedule(schedule_id,dept_id,doctor_id,work_date,time_slot,quota,remain_quota,status,create_by,create_time) values ('144','4','8',date_add(curdate(), interval 2 day),'1','20','20','0','admin','2026-01-08 09:00:00');

insert into his_registration(reg_id,reg_no,patient_id,dept_id,doctor_id,schedule_id,reg_date,time_slot,queue_no,reg_fee,visit_status,pay_status,create_by,create_time) values ('1','GH20260901','1','1','3','5',date_sub(curdate(), interval 6 day),'0','1','20','2','1','admin',date_sub(now(), interval 6 day));
insert into his_registration(reg_id,reg_no,patient_id,dept_id,doctor_id,schedule_id,reg_date,time_slot,queue_no,reg_fee,visit_status,pay_status,create_by,create_time) values ('2','GH20260902','2','2','4','8',date_sub(curdate(), interval 6 day),'1','2','50','2','1','admin',date_sub(now(), interval 6 day));
insert into his_registration(reg_id,reg_no,patient_id,dept_id,doctor_id,schedule_id,reg_date,time_slot,queue_no,reg_fee,visit_status,pay_status,create_by,create_time) values ('3','GH20260903','3','2','5','9',date_sub(curdate(), interval 6 day),'0','3','20','2','1','admin',date_sub(now(), interval 6 day));
insert into his_registration(reg_id,reg_no,patient_id,dept_id,doctor_id,schedule_id,reg_date,time_slot,queue_no,reg_fee,visit_status,pay_status,create_by,create_time) values ('4','GH20260904','4','3','6','12',date_sub(curdate(), interval 6 day),'1','4','30','2','1','admin',date_sub(now(), interval 6 day));
insert into his_registration(reg_id,reg_no,patient_id,dept_id,doctor_id,schedule_id,reg_date,time_slot,queue_no,reg_fee,visit_status,pay_status,create_by,create_time) values ('5','GH20260905','5','3','7','13',date_sub(curdate(), interval 6 day),'0','5','10','2','1','admin',date_sub(now(), interval 6 day));
insert into his_registration(reg_id,reg_no,patient_id,dept_id,doctor_id,schedule_id,reg_date,time_slot,queue_no,reg_fee,visit_status,pay_status,create_by,create_time) values ('6','GH20260906','6','2','4','23',date_sub(curdate(), interval 5 day),'0','1','50','2','1','admin',date_sub(now(), interval 5 day));
insert into his_registration(reg_id,reg_no,patient_id,dept_id,doctor_id,schedule_id,reg_date,time_slot,queue_no,reg_fee,visit_status,pay_status,create_by,create_time) values ('7','GH20260907','7','2','5','26',date_sub(curdate(), interval 5 day),'1','2','20','2','1','admin',date_sub(now(), interval 5 day));
insert into his_registration(reg_id,reg_no,patient_id,dept_id,doctor_id,schedule_id,reg_date,time_slot,queue_no,reg_fee,visit_status,pay_status,create_by,create_time) values ('8','GH20260908','8','3','6','27',date_sub(curdate(), interval 5 day),'0','3','30','2','1','admin',date_sub(now(), interval 5 day));
insert into his_registration(reg_id,reg_no,patient_id,dept_id,doctor_id,schedule_id,reg_date,time_slot,queue_no,reg_fee,visit_status,pay_status,create_by,create_time) values ('9','GH20260909','9','3','7','30',date_sub(curdate(), interval 5 day),'1','4','10','2','1','admin',date_sub(now(), interval 5 day));
insert into his_registration(reg_id,reg_no,patient_id,dept_id,doctor_id,schedule_id,reg_date,time_slot,queue_no,reg_fee,visit_status,pay_status,create_by,create_time) values ('10','GH202609010','10','4','8','31',date_sub(curdate(), interval 5 day),'0','5','30','2','1','admin',date_sub(now(), interval 5 day));
insert into his_registration(reg_id,reg_no,patient_id,dept_id,doctor_id,schedule_id,reg_date,time_slot,queue_no,reg_fee,visit_status,pay_status,create_by,create_time) values ('11','GH202609011','11','2','5','41',date_sub(curdate(), interval 4 day),'0','1','20','2','1','admin',date_sub(now(), interval 4 day));
insert into his_registration(reg_id,reg_no,patient_id,dept_id,doctor_id,schedule_id,reg_date,time_slot,queue_no,reg_fee,visit_status,pay_status,create_by,create_time) values ('12','GH202609012','12','3','6','44',date_sub(curdate(), interval 4 day),'1','2','30','2','1','admin',date_sub(now(), interval 4 day));
insert into his_registration(reg_id,reg_no,patient_id,dept_id,doctor_id,schedule_id,reg_date,time_slot,queue_no,reg_fee,visit_status,pay_status,create_by,create_time) values ('13','GH202609013','13','3','7','45',date_sub(curdate(), interval 4 day),'0','3','10','2','1','admin',date_sub(now(), interval 4 day));
insert into his_registration(reg_id,reg_no,patient_id,dept_id,doctor_id,schedule_id,reg_date,time_slot,queue_no,reg_fee,visit_status,pay_status,create_by,create_time) values ('14','GH202609014','14','4','8','48',date_sub(curdate(), interval 4 day),'1','4','30','2','1','admin',date_sub(now(), interval 4 day));
insert into his_registration(reg_id,reg_no,patient_id,dept_id,doctor_id,schedule_id,reg_date,time_slot,queue_no,reg_fee,visit_status,pay_status,create_by,create_time) values ('15','GH202609015','15','1','1','33',date_sub(curdate(), interval 4 day),'0','5','50','2','1','admin',date_sub(now(), interval 4 day));
insert into his_registration(reg_id,reg_no,patient_id,dept_id,doctor_id,schedule_id,reg_date,time_slot,queue_no,reg_fee,visit_status,pay_status,create_by,create_time) values ('16','GH202609016','16','3','6','59',date_sub(curdate(), interval 3 day),'0','1','30','2','1','admin',date_sub(now(), interval 3 day));
insert into his_registration(reg_id,reg_no,patient_id,dept_id,doctor_id,schedule_id,reg_date,time_slot,queue_no,reg_fee,visit_status,pay_status,create_by,create_time) values ('17','GH202609017','1','3','7','62',date_sub(curdate(), interval 3 day),'1','2','10','2','1','admin',date_sub(now(), interval 3 day));
insert into his_registration(reg_id,reg_no,patient_id,dept_id,doctor_id,schedule_id,reg_date,time_slot,queue_no,reg_fee,visit_status,pay_status,create_by,create_time) values ('18','GH202609018','2','4','8','63',date_sub(curdate(), interval 3 day),'0','3','30','2','1','admin',date_sub(now(), interval 3 day));
insert into his_registration(reg_id,reg_no,patient_id,dept_id,doctor_id,schedule_id,reg_date,time_slot,queue_no,reg_fee,visit_status,pay_status,create_by,create_time) values ('19','GH202609019','3','1','1','50',date_sub(curdate(), interval 3 day),'1','4','50','2','1','admin',date_sub(now(), interval 3 day));
insert into his_registration(reg_id,reg_no,patient_id,dept_id,doctor_id,schedule_id,reg_date,time_slot,queue_no,reg_fee,visit_status,pay_status,create_by,create_time) values ('20','GH202609020','4','1','2','51',date_sub(curdate(), interval 3 day),'0','5','30','2','1','admin',date_sub(now(), interval 3 day));
insert into his_registration(reg_id,reg_no,patient_id,dept_id,doctor_id,schedule_id,reg_date,time_slot,queue_no,reg_fee,visit_status,pay_status,create_by,create_time) values ('21','GH202609021','5','3','7','77',date_sub(curdate(), interval 2 day),'0','1','10','2','1','admin',date_sub(now(), interval 2 day));
insert into his_registration(reg_id,reg_no,patient_id,dept_id,doctor_id,schedule_id,reg_date,time_slot,queue_no,reg_fee,visit_status,pay_status,create_by,create_time) values ('22','GH202609022','6','4','8','80',date_sub(curdate(), interval 2 day),'1','2','30','2','1','admin',date_sub(now(), interval 2 day));
insert into his_registration(reg_id,reg_no,patient_id,dept_id,doctor_id,schedule_id,reg_date,time_slot,queue_no,reg_fee,visit_status,pay_status,create_by,create_time) values ('23','GH202609023','7','1','1','65',date_sub(curdate(), interval 2 day),'0','3','50','2','1','admin',date_sub(now(), interval 2 day));
insert into his_registration(reg_id,reg_no,patient_id,dept_id,doctor_id,schedule_id,reg_date,time_slot,queue_no,reg_fee,visit_status,pay_status,create_by,create_time) values ('24','GH202609024','8','1','2','68',date_sub(curdate(), interval 2 day),'1','4','30','2','1','admin',date_sub(now(), interval 2 day));
insert into his_registration(reg_id,reg_no,patient_id,dept_id,doctor_id,schedule_id,reg_date,time_slot,queue_no,reg_fee,visit_status,pay_status,create_by,create_time) values ('25','GH202609025','9','1','3','69',date_sub(curdate(), interval 2 day),'0','5','20','2','1','admin',date_sub(now(), interval 2 day));
insert into his_registration(reg_id,reg_no,patient_id,dept_id,doctor_id,schedule_id,reg_date,time_slot,queue_no,reg_fee,visit_status,pay_status,create_by,create_time) values ('26','GH202609026','10','4','8','95',date_sub(curdate(), interval 1 day),'0','1','30','2','1','admin',date_sub(now(), interval 1 day));
insert into his_registration(reg_id,reg_no,patient_id,dept_id,doctor_id,schedule_id,reg_date,time_slot,queue_no,reg_fee,visit_status,pay_status,create_by,create_time) values ('27','GH202609027','11','1','1','82',date_sub(curdate(), interval 1 day),'1','2','50','2','1','admin',date_sub(now(), interval 1 day));
insert into his_registration(reg_id,reg_no,patient_id,dept_id,doctor_id,schedule_id,reg_date,time_slot,queue_no,reg_fee,visit_status,pay_status,create_by,create_time) values ('28','GH202609028','12','1','2','83',date_sub(curdate(), interval 1 day),'0','3','30','2','1','admin',date_sub(now(), interval 1 day));
insert into his_registration(reg_id,reg_no,patient_id,dept_id,doctor_id,schedule_id,reg_date,time_slot,queue_no,reg_fee,visit_status,pay_status,create_by,create_time) values ('29','GH202609029','13','1','3','86',date_sub(curdate(), interval 1 day),'1','4','20','2','1','admin',date_sub(now(), interval 1 day));
insert into his_registration(reg_id,reg_no,patient_id,dept_id,doctor_id,schedule_id,reg_date,time_slot,queue_no,reg_fee,visit_status,pay_status,create_by,create_time) values ('30','GH202609030','14','2','4','87',date_sub(curdate(), interval 1 day),'0','5','50','2','1','admin',date_sub(now(), interval 1 day));
insert into his_registration(reg_id,reg_no,patient_id,dept_id,doctor_id,schedule_id,reg_date,time_slot,queue_no,reg_fee,visit_status,pay_status,create_by,create_time) values ('31','GH202609131','2','1','1','97',curdate(),'0','1','50','2','1','admin',now());
insert into his_registration(reg_id,reg_no,patient_id,dept_id,doctor_id,schedule_id,reg_date,time_slot,queue_no,reg_fee,visit_status,pay_status,create_by,create_time) values ('32','GH202609132','5','1','2','99',curdate(),'0','2','30','2','1','admin',now());
insert into his_registration(reg_id,reg_no,patient_id,dept_id,doctor_id,schedule_id,reg_date,time_slot,queue_no,reg_fee,visit_status,pay_status,create_by,create_time) values ('33','GH202609133','8','1','3','101',curdate(),'0','3','20','0','1','admin',now());
insert into his_registration(reg_id,reg_no,patient_id,dept_id,doctor_id,schedule_id,reg_date,time_slot,queue_no,reg_fee,visit_status,pay_status,create_by,create_time) values ('34','GH202609134','11','2','4','104',curdate(),'1','4','50','0','1','admin',now());
insert into his_registration(reg_id,reg_no,patient_id,dept_id,doctor_id,schedule_id,reg_date,time_slot,queue_no,reg_fee,visit_status,pay_status,create_by,create_time) values ('35','GH202609135','14','2','5','106',curdate(),'1','5','20','1','1','admin',now());
insert into his_registration(reg_id,reg_no,patient_id,dept_id,doctor_id,schedule_id,reg_date,time_slot,queue_no,reg_fee,visit_status,pay_status,create_by,create_time) values ('36','GH202609136','1','3','6','107',curdate(),'0','6','30','0','0','admin',now());
insert into his_registration(reg_id,reg_no,patient_id,dept_id,doctor_id,schedule_id,reg_date,time_slot,queue_no,reg_fee,visit_status,pay_status,create_by,create_time) values ('37','GH202609137','4','3','7','109',curdate(),'0','7','10','0','1','admin',now());
insert into his_registration(reg_id,reg_no,patient_id,dept_id,doctor_id,schedule_id,reg_date,time_slot,queue_no,reg_fee,visit_status,pay_status,create_by,create_time) values ('38','GH202609138','7','4','8','112',curdate(),'1','8','30','4','1','admin',now());
insert into his_registration(reg_id,reg_no,patient_id,dept_id,doctor_id,schedule_id,reg_date,time_slot,queue_no,reg_fee,visit_status,pay_status,create_by,create_time) values ('39','GH202609139','10','1','1','98',curdate(),'1','9','50','2','1','admin',now());
insert into his_registration(reg_id,reg_no,patient_id,dept_id,doctor_id,schedule_id,reg_date,time_slot,queue_no,reg_fee,visit_status,pay_status,create_by,create_time) values ('40','GH202609140','13','1','2','100',curdate(),'1','10','30','0','0','admin',now());

insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('1','GHF1','1','0','1','挂号费','20','1','20','1','0',date_sub(now(), interval 6 day),'admin','admin',date_sub(now(), interval 6 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('2','GHF2','2','0','2','挂号费','50','1','50','1','1',date_sub(now(), interval 6 day),'admin','admin',date_sub(now(), interval 6 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('3','GHF3','3','0','3','挂号费','20','1','20','1','2',date_sub(now(), interval 6 day),'admin','admin',date_sub(now(), interval 6 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('4','GHF4','4','0','4','挂号费','30','1','30','1','0',date_sub(now(), interval 6 day),'admin','admin',date_sub(now(), interval 6 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('5','GHF5','5','0','5','挂号费','10','1','10','1','1',date_sub(now(), interval 6 day),'admin','admin',date_sub(now(), interval 6 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('6','GHF6','6','0','6','挂号费','50','1','50','1','0',date_sub(now(), interval 5 day),'admin','admin',date_sub(now(), interval 5 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('7','GHF7','7','0','7','挂号费','20','1','20','1','1',date_sub(now(), interval 5 day),'admin','admin',date_sub(now(), interval 5 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('8','GHF8','8','0','8','挂号费','30','1','30','1','2',date_sub(now(), interval 5 day),'admin','admin',date_sub(now(), interval 5 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('9','GHF9','9','0','9','挂号费','10','1','10','1','0',date_sub(now(), interval 5 day),'admin','admin',date_sub(now(), interval 5 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('10','GHF10','10','0','10','挂号费','30','1','30','1','1',date_sub(now(), interval 5 day),'admin','admin',date_sub(now(), interval 5 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('11','GHF11','11','0','11','挂号费','20','1','20','1','0',date_sub(now(), interval 4 day),'admin','admin',date_sub(now(), interval 4 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('12','GHF12','12','0','12','挂号费','30','1','30','1','1',date_sub(now(), interval 4 day),'admin','admin',date_sub(now(), interval 4 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('13','GHF13','13','0','13','挂号费','10','1','10','1','2',date_sub(now(), interval 4 day),'admin','admin',date_sub(now(), interval 4 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('14','GHF14','14','0','14','挂号费','30','1','30','1','0',date_sub(now(), interval 4 day),'admin','admin',date_sub(now(), interval 4 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('15','GHF15','15','0','15','挂号费','50','1','50','1','1',date_sub(now(), interval 4 day),'admin','admin',date_sub(now(), interval 4 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('16','GHF16','16','0','16','挂号费','30','1','30','1','0',date_sub(now(), interval 3 day),'admin','admin',date_sub(now(), interval 3 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('17','GHF17','1','0','17','挂号费','10','1','10','1','1',date_sub(now(), interval 3 day),'admin','admin',date_sub(now(), interval 3 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('18','GHF18','2','0','18','挂号费','30','1','30','1','2',date_sub(now(), interval 3 day),'admin','admin',date_sub(now(), interval 3 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('19','GHF19','3','0','19','挂号费','50','1','50','1','0',date_sub(now(), interval 3 day),'admin','admin',date_sub(now(), interval 3 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('20','GHF20','4','0','20','挂号费','30','1','30','1','1',date_sub(now(), interval 3 day),'admin','admin',date_sub(now(), interval 3 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('21','GHF21','5','0','21','挂号费','10','1','10','1','0',date_sub(now(), interval 2 day),'admin','admin',date_sub(now(), interval 2 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('22','GHF22','6','0','22','挂号费','30','1','30','1','1',date_sub(now(), interval 2 day),'admin','admin',date_sub(now(), interval 2 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('23','GHF23','7','0','23','挂号费','50','1','50','1','2',date_sub(now(), interval 2 day),'admin','admin',date_sub(now(), interval 2 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('24','GHF24','8','0','24','挂号费','30','1','30','1','0',date_sub(now(), interval 2 day),'admin','admin',date_sub(now(), interval 2 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('25','GHF25','9','0','25','挂号费','20','1','20','1','1',date_sub(now(), interval 2 day),'admin','admin',date_sub(now(), interval 2 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('26','GHF26','10','0','26','挂号费','30','1','30','1','0',date_sub(now(), interval 1 day),'admin','admin',date_sub(now(), interval 1 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('27','GHF27','11','0','27','挂号费','50','1','50','1','1',date_sub(now(), interval 1 day),'admin','admin',date_sub(now(), interval 1 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('28','GHF28','12','0','28','挂号费','30','1','30','1','2',date_sub(now(), interval 1 day),'admin','admin',date_sub(now(), interval 1 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('29','GHF29','13','0','29','挂号费','20','1','20','1','0',date_sub(now(), interval 1 day),'admin','admin',date_sub(now(), interval 1 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('30','GHF30','14','0','30','挂号费','50','1','50','1','1',date_sub(now(), interval 1 day),'admin','admin',date_sub(now(), interval 1 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('31','GHF31','2','0','31','挂号费','50','1','50','1','0',now(),'admin','admin',now());
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('32','GHF32','5','0','32','挂号费','30','1','30','1','1',now(),'admin','admin',now());
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('33','GHF33','8','0','33','挂号费','20','1','20','1','2',now(),'admin','admin',now());
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('34','GHF34','11','0','34','挂号费','50','1','50','1','0',now(),'admin','admin',now());
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('35','GHF35','14','0','35','挂号费','20','1','20','1','1',now(),'admin','admin',now());
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('36','GHF36','1','0','36','挂号费','30','1','30','0',null,null,null,'admin',now());
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('37','GHF37','4','0','37','挂号费','10','1','10','1','0',now(),'admin','admin',now());
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('38','GHF38','7','0','38','挂号费','30','1','30','1','1',now(),'admin','admin',now());
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('39','GHF39','10','0','39','挂号费','50','1','50','1','2',now(),'admin','admin',now());
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('40','GHF40','13','0','40','挂号费','30','1','30','0',null,null,null,'admin',now());

insert into his_visit(visit_id,visit_no,reg_id,patient_id,doctor_id,dept_id,visit_time,chief_complaint,present_illness,past_illness,diagnosis,treatment,status,create_by,create_time) values ('1','JZ000001','1','1','3','1',date_sub(curdate(), interval 6 day),'头痛发热3天','头痛发热3天，伴乏力','高血压病史0年','急性上呼吸道感染','对症处理，多饮水休息','1','admin',date_sub(curdate(), interval 6 day));
insert into his_visit(visit_id,visit_no,reg_id,patient_id,doctor_id,dept_id,visit_time,chief_complaint,present_illness,past_illness,diagnosis,treatment,status,create_by,create_time) values ('2','JZ000002','2','2','4','2',date_sub(curdate(), interval 6 day),'反复胸闷1周','反复胸闷1周，伴乏力','高血压病史1年','冠心病待查','心电图+心脏彩超检查','1','admin',date_sub(curdate(), interval 6 day));
insert into his_visit(visit_id,visit_no,reg_id,patient_id,doctor_id,dept_id,visit_time,chief_complaint,present_illness,past_illness,diagnosis,treatment,status,create_by,create_time) values ('3','JZ000003','3','3','5','2',date_sub(curdate(), interval 6 day),'腹痛腹泻2天','腹痛腹泻2天，伴乏力','高血压病史2年','急性肠胃炎','清淡饮食，口服补液盐','1','admin',date_sub(curdate(), interval 6 day));
insert into his_visit(visit_id,visit_no,reg_id,patient_id,doctor_id,dept_id,visit_time,chief_complaint,present_illness,past_illness,diagnosis,treatment,status,create_by,create_time) values ('4','JZ000004','4','4','6','3',date_sub(curdate(), interval 6 day),'咳嗽咳痰5天','咳嗽咳痰5天，伴乏力','高血压病史0年','支气管炎','抗感染对症治疗','1','admin',date_sub(curdate(), interval 6 day));
insert into his_visit(visit_id,visit_no,reg_id,patient_id,doctor_id,dept_id,visit_time,chief_complaint,present_illness,past_illness,diagnosis,treatment,status,create_by,create_time) values ('5','JZ000005','5','5','7','3',date_sub(curdate(), interval 6 day),'血压升高半年','血压升高半年，伴乏力','高血压病史1年','原发性高血压','规律服药，低盐饮食','1','admin',date_sub(curdate(), interval 6 day));
insert into his_visit(visit_id,visit_no,reg_id,patient_id,doctor_id,dept_id,visit_time,chief_complaint,present_illness,past_illness,diagnosis,treatment,status,create_by,create_time) values ('6','JZ000006','6','6','4','2',date_sub(curdate(), interval 5 day),'腰痛1月','腰痛1月，伴乏力','高血压病史2年','腰椎间盘突出','理疗+止痛，避免负重','1','admin',date_sub(curdate(), interval 5 day));
insert into his_visit(visit_id,visit_no,reg_id,patient_id,doctor_id,dept_id,visit_time,chief_complaint,present_illness,past_illness,diagnosis,treatment,status,create_by,create_time) values ('7','JZ000007','7','7','5','2',date_sub(curdate(), interval 5 day),'咽痛2天','咽痛2天，伴乏力','高血压病史0年','急性咽炎','含片+抗感染','1','admin',date_sub(curdate(), interval 5 day));
insert into his_visit(visit_id,visit_no,reg_id,patient_id,doctor_id,dept_id,visit_time,chief_complaint,present_illness,past_illness,diagnosis,treatment,status,create_by,create_time) values ('8','JZ000008','8','8','6','3',date_sub(curdate(), interval 5 day),'失眠多梦1月','失眠多梦1月，伴乏力','高血压病史1年','神经衰弱','作息规律，中药调理','1','admin',date_sub(curdate(), interval 5 day));
insert into his_visit(visit_id,visit_no,reg_id,patient_id,doctor_id,dept_id,visit_time,chief_complaint,present_illness,past_illness,diagnosis,treatment,status,create_by,create_time) values ('9','JZ000009','9','9','7','3',date_sub(curdate(), interval 5 day),'头痛发热3天','头痛发热3天，伴乏力','高血压病史2年','急性上呼吸道感染','对症处理，多饮水休息','1','admin',date_sub(curdate(), interval 5 day));
insert into his_visit(visit_id,visit_no,reg_id,patient_id,doctor_id,dept_id,visit_time,chief_complaint,present_illness,past_illness,diagnosis,treatment,status,create_by,create_time) values ('10','JZ000010','10','10','8','4',date_sub(curdate(), interval 5 day),'反复胸闷1周','反复胸闷1周，伴乏力','高血压病史0年','冠心病待查','心电图+心脏彩超检查','1','admin',date_sub(curdate(), interval 5 day));
insert into his_visit(visit_id,visit_no,reg_id,patient_id,doctor_id,dept_id,visit_time,chief_complaint,present_illness,past_illness,diagnosis,treatment,status,create_by,create_time) values ('11','JZ000011','11','11','5','2',date_sub(curdate(), interval 4 day),'腹痛腹泻2天','腹痛腹泻2天，伴乏力','高血压病史1年','急性肠胃炎','清淡饮食，口服补液盐','1','admin',date_sub(curdate(), interval 4 day));
insert into his_visit(visit_id,visit_no,reg_id,patient_id,doctor_id,dept_id,visit_time,chief_complaint,present_illness,past_illness,diagnosis,treatment,status,create_by,create_time) values ('12','JZ000012','12','12','6','3',date_sub(curdate(), interval 4 day),'咳嗽咳痰5天','咳嗽咳痰5天，伴乏力','高血压病史2年','支气管炎','抗感染对症治疗','1','admin',date_sub(curdate(), interval 4 day));
insert into his_visit(visit_id,visit_no,reg_id,patient_id,doctor_id,dept_id,visit_time,chief_complaint,present_illness,past_illness,diagnosis,treatment,status,create_by,create_time) values ('13','JZ000013','13','13','7','3',date_sub(curdate(), interval 4 day),'血压升高半年','血压升高半年，伴乏力','高血压病史0年','原发性高血压','规律服药，低盐饮食','1','admin',date_sub(curdate(), interval 4 day));
insert into his_visit(visit_id,visit_no,reg_id,patient_id,doctor_id,dept_id,visit_time,chief_complaint,present_illness,past_illness,diagnosis,treatment,status,create_by,create_time) values ('14','JZ000014','14','14','8','4',date_sub(curdate(), interval 4 day),'腰痛1月','腰痛1月，伴乏力','高血压病史1年','腰椎间盘突出','理疗+止痛，避免负重','1','admin',date_sub(curdate(), interval 4 day));
insert into his_visit(visit_id,visit_no,reg_id,patient_id,doctor_id,dept_id,visit_time,chief_complaint,present_illness,past_illness,diagnosis,treatment,status,create_by,create_time) values ('15','JZ000015','15','15','1','1',date_sub(curdate(), interval 4 day),'咽痛2天','咽痛2天，伴乏力','高血压病史2年','急性咽炎','含片+抗感染','1','admin',date_sub(curdate(), interval 4 day));
insert into his_visit(visit_id,visit_no,reg_id,patient_id,doctor_id,dept_id,visit_time,chief_complaint,present_illness,past_illness,diagnosis,treatment,status,create_by,create_time) values ('16','JZ000016','16','16','6','3',date_sub(curdate(), interval 3 day),'失眠多梦1月','失眠多梦1月，伴乏力','高血压病史0年','神经衰弱','作息规律，中药调理','1','admin',date_sub(curdate(), interval 3 day));
insert into his_visit(visit_id,visit_no,reg_id,patient_id,doctor_id,dept_id,visit_time,chief_complaint,present_illness,past_illness,diagnosis,treatment,status,create_by,create_time) values ('17','JZ000017','17','1','7','3',date_sub(curdate(), interval 3 day),'头痛发热3天','头痛发热3天，伴乏力','高血压病史1年','急性上呼吸道感染','对症处理，多饮水休息','1','admin',date_sub(curdate(), interval 3 day));
insert into his_visit(visit_id,visit_no,reg_id,patient_id,doctor_id,dept_id,visit_time,chief_complaint,present_illness,past_illness,diagnosis,treatment,status,create_by,create_time) values ('18','JZ000018','18','2','8','4',date_sub(curdate(), interval 3 day),'反复胸闷1周','反复胸闷1周，伴乏力','高血压病史2年','冠心病待查','心电图+心脏彩超检查','1','admin',date_sub(curdate(), interval 3 day));
insert into his_visit(visit_id,visit_no,reg_id,patient_id,doctor_id,dept_id,visit_time,chief_complaint,present_illness,past_illness,diagnosis,treatment,status,create_by,create_time) values ('19','JZ000019','19','3','1','1',date_sub(curdate(), interval 3 day),'腹痛腹泻2天','腹痛腹泻2天，伴乏力','高血压病史0年','急性肠胃炎','清淡饮食，口服补液盐','1','admin',date_sub(curdate(), interval 3 day));
insert into his_visit(visit_id,visit_no,reg_id,patient_id,doctor_id,dept_id,visit_time,chief_complaint,present_illness,past_illness,diagnosis,treatment,status,create_by,create_time) values ('20','JZ000020','20','4','2','1',date_sub(curdate(), interval 3 day),'咳嗽咳痰5天','咳嗽咳痰5天，伴乏力','高血压病史1年','支气管炎','抗感染对症治疗','1','admin',date_sub(curdate(), interval 3 day));
insert into his_visit(visit_id,visit_no,reg_id,patient_id,doctor_id,dept_id,visit_time,chief_complaint,present_illness,past_illness,diagnosis,treatment,status,create_by,create_time) values ('21','JZ000021','21','5','7','3',date_sub(curdate(), interval 2 day),'血压升高半年','血压升高半年，伴乏力','高血压病史2年','原发性高血压','规律服药，低盐饮食','1','admin',date_sub(curdate(), interval 2 day));
insert into his_visit(visit_id,visit_no,reg_id,patient_id,doctor_id,dept_id,visit_time,chief_complaint,present_illness,past_illness,diagnosis,treatment,status,create_by,create_time) values ('22','JZ000022','22','6','8','4',date_sub(curdate(), interval 2 day),'腰痛1月','腰痛1月，伴乏力','高血压病史0年','腰椎间盘突出','理疗+止痛，避免负重','1','admin',date_sub(curdate(), interval 2 day));
insert into his_visit(visit_id,visit_no,reg_id,patient_id,doctor_id,dept_id,visit_time,chief_complaint,present_illness,past_illness,diagnosis,treatment,status,create_by,create_time) values ('23','JZ000023','23','7','1','1',date_sub(curdate(), interval 2 day),'咽痛2天','咽痛2天，伴乏力','高血压病史1年','急性咽炎','含片+抗感染','1','admin',date_sub(curdate(), interval 2 day));
insert into his_visit(visit_id,visit_no,reg_id,patient_id,doctor_id,dept_id,visit_time,chief_complaint,present_illness,past_illness,diagnosis,treatment,status,create_by,create_time) values ('24','JZ000024','24','8','2','1',date_sub(curdate(), interval 2 day),'失眠多梦1月','失眠多梦1月，伴乏力','高血压病史2年','神经衰弱','作息规律，中药调理','1','admin',date_sub(curdate(), interval 2 day));
insert into his_visit(visit_id,visit_no,reg_id,patient_id,doctor_id,dept_id,visit_time,chief_complaint,present_illness,past_illness,diagnosis,treatment,status,create_by,create_time) values ('25','JZ000025','25','9','3','1',date_sub(curdate(), interval 2 day),'头痛发热3天','头痛发热3天，伴乏力','高血压病史0年','急性上呼吸道感染','对症处理，多饮水休息','1','admin',date_sub(curdate(), interval 2 day));
insert into his_visit(visit_id,visit_no,reg_id,patient_id,doctor_id,dept_id,visit_time,chief_complaint,present_illness,past_illness,diagnosis,treatment,status,create_by,create_time) values ('26','JZ000026','26','10','8','4',date_sub(curdate(), interval 1 day),'反复胸闷1周','反复胸闷1周，伴乏力','高血压病史1年','冠心病待查','心电图+心脏彩超检查','1','admin',date_sub(curdate(), interval 1 day));
insert into his_visit(visit_id,visit_no,reg_id,patient_id,doctor_id,dept_id,visit_time,chief_complaint,present_illness,past_illness,diagnosis,treatment,status,create_by,create_time) values ('27','JZ000027','27','11','1','1',date_sub(curdate(), interval 1 day),'腹痛腹泻2天','腹痛腹泻2天，伴乏力','高血压病史2年','急性肠胃炎','清淡饮食，口服补液盐','1','admin',date_sub(curdate(), interval 1 day));
insert into his_visit(visit_id,visit_no,reg_id,patient_id,doctor_id,dept_id,visit_time,chief_complaint,present_illness,past_illness,diagnosis,treatment,status,create_by,create_time) values ('28','JZ000028','28','12','2','1',date_sub(curdate(), interval 1 day),'咳嗽咳痰5天','咳嗽咳痰5天，伴乏力','高血压病史0年','支气管炎','抗感染对症治疗','1','admin',date_sub(curdate(), interval 1 day));
insert into his_visit(visit_id,visit_no,reg_id,patient_id,doctor_id,dept_id,visit_time,chief_complaint,present_illness,past_illness,diagnosis,treatment,status,create_by,create_time) values ('29','JZ000029','29','13','3','1',date_sub(curdate(), interval 1 day),'血压升高半年','血压升高半年，伴乏力','高血压病史1年','原发性高血压','规律服药，低盐饮食','1','admin',date_sub(curdate(), interval 1 day));
insert into his_visit(visit_id,visit_no,reg_id,patient_id,doctor_id,dept_id,visit_time,chief_complaint,present_illness,past_illness,diagnosis,treatment,status,create_by,create_time) values ('30','JZ000030','30','14','4','2',date_sub(curdate(), interval 1 day),'腰痛1月','腰痛1月，伴乏力','高血压病史2年','腰椎间盘突出','理疗+止痛，避免负重','1','admin',date_sub(curdate(), interval 1 day));
insert into his_visit(visit_id,visit_no,reg_id,patient_id,doctor_id,dept_id,visit_time,chief_complaint,present_illness,past_illness,diagnosis,treatment,status,create_by,create_time) values ('31','JZ000031','31','2','1','1',curdate(),'咽痛2天','咽痛2天，伴乏力','高血压病史0年','急性咽炎','含片+抗感染','1','admin',curdate());
insert into his_visit(visit_id,visit_no,reg_id,patient_id,doctor_id,dept_id,visit_time,chief_complaint,present_illness,past_illness,diagnosis,treatment,status,create_by,create_time) values ('32','JZ000032','32','5','2','1',curdate(),'失眠多梦1月','失眠多梦1月，伴乏力','高血压病史1年','神经衰弱','作息规律，中药调理','1','admin',curdate());
insert into his_visit(visit_id,visit_no,reg_id,patient_id,doctor_id,dept_id,visit_time,chief_complaint,present_illness,past_illness,diagnosis,treatment,status,create_by,create_time) values ('33','JZ000033','39','10','1','1',curdate(),'咽痛2天','咽痛2天，伴乏力','高血压病史2年','急性咽炎','含片+抗感染','1','admin',curdate());

insert into his_prescription(rx_id,rx_no,visit_id,patient_id,doctor_id,rx_type,total_amount,status,dispense_by,dispense_time,create_by,create_time) values ('1','CF000001','1','1','1','0','48.80','2','admin',date_sub(curdate(), interval 6 day),'admin',date_sub(curdate(), interval 6 day));
insert into his_prescription(rx_id,rx_no,visit_id,patient_id,doctor_id,rx_type,total_amount,status,dispense_by,dispense_time,create_by,create_time) values ('2','CF000002','2','2','2','0','41.60','2','admin',date_sub(curdate(), interval 6 day),'admin',date_sub(curdate(), interval 6 day));
insert into his_prescription(rx_id,rx_no,visit_id,patient_id,doctor_id,rx_type,total_amount,status,dispense_by,dispense_time,create_by,create_time) values ('3','CF000003','3','3','3','0','36.60','2','admin',date_sub(curdate(), interval 6 day),'admin',date_sub(curdate(), interval 6 day));
insert into his_prescription(rx_id,rx_no,visit_id,patient_id,doctor_id,rx_type,total_amount,status,dispense_by,dispense_time,create_by,create_time) values ('4','CF000004','4','4','4','0','50.50','2','admin',date_sub(curdate(), interval 6 day),'admin',date_sub(curdate(), interval 6 day));
insert into his_prescription(rx_id,rx_no,visit_id,patient_id,doctor_id,rx_type,total_amount,status,dispense_by,dispense_time,create_by,create_time) values ('5','CF000005','5','5','5','0','44.00','2','admin',date_sub(curdate(), interval 6 day),'admin',date_sub(curdate(), interval 6 day));
insert into his_prescription(rx_id,rx_no,visit_id,patient_id,doctor_id,rx_type,total_amount,status,dispense_by,dispense_time,create_by,create_time) values ('6','CF000006','6','6','6','0','41.10','2','admin',date_sub(curdate(), interval 5 day),'admin',date_sub(curdate(), interval 5 day));
insert into his_prescription(rx_id,rx_no,visit_id,patient_id,doctor_id,rx_type,total_amount,status,dispense_by,dispense_time,create_by,create_time) values ('7','CF000007','7','7','7','0','50.80','2','admin',date_sub(curdate(), interval 5 day),'admin',date_sub(curdate(), interval 5 day));
insert into his_prescription(rx_id,rx_no,visit_id,patient_id,doctor_id,rx_type,total_amount,status,dispense_by,dispense_time,create_by,create_time) values ('8','CF000008','8','8','8','0','29.00','2','admin',date_sub(curdate(), interval 5 day),'admin',date_sub(curdate(), interval 5 day));
insert into his_prescription(rx_id,rx_no,visit_id,patient_id,doctor_id,rx_type,total_amount,status,dispense_by,dispense_time,create_by,create_time) values ('9','CF000009','9','9','9','0','28.00','1',null,null,'admin',date_sub(curdate(), interval 5 day));
insert into his_prescription(rx_id,rx_no,visit_id,patient_id,doctor_id,rx_type,total_amount,status,dispense_by,dispense_time,create_by,create_time) values ('10','CF000010','10','10','10','0','57.50','1',null,null,'admin',date_sub(curdate(), interval 5 day));
insert into his_prescription(rx_id,rx_no,visit_id,patient_id,doctor_id,rx_type,total_amount,status,dispense_by,dispense_time,create_by,create_time) values ('11','CF000011','11','11','11','0','41.30','0',null,null,'admin',date_sub(curdate(), interval 4 day));
insert into his_prescription(rx_id,rx_no,visit_id,patient_id,doctor_id,rx_type,total_amount,status,dispense_by,dispense_time,create_by,create_time) values ('12','CF000012','12','12','12','0','24.60','0',null,null,'admin',date_sub(curdate(), interval 4 day));

insert into his_prescription_item(item_id,rx_id,drug_id,drug_name,specification,unit,price,quantity,amount,usage_dose,frequency,days) values ('1','1','1','阿莫西林胶囊','0.25g*24粒','盒','12.5','2','25.00','每日一次','口服','7');
insert into his_prescription_item(item_id,rx_id,drug_id,drug_name,specification,unit,price,quantity,amount,usage_dose,frequency,days) values ('2','1','2','布洛芬缓释胶囊','0.3g*20粒','盒','15.8','1','15.80','每日两次','口服','7');
insert into his_prescription_item(item_id,rx_id,drug_id,drug_name,specification,unit,price,quantity,amount,usage_dose,frequency,days) values ('3','1','10','维生素C片','0.1g*100片','瓶','8','1','8.00','每日两次','口服','7');
insert into his_prescription_item(item_id,rx_id,drug_id,drug_name,specification,unit,price,quantity,amount,usage_dose,frequency,days) values ('4','2','4','头孢克肟分散片','0.1g*12片','盒','22','1','22.00','每日两次','口服','7');
insert into his_prescription_item(item_id,rx_id,drug_id,drug_name,specification,unit,price,quantity,amount,usage_dose,frequency,days) values ('5','2','11','板蓝根颗粒','10g*20袋','盒','9.8','2','19.60','每日一次','口服','7');
insert into his_prescription_item(item_id,rx_id,drug_id,drug_name,specification,unit,price,quantity,amount,usage_dose,frequency,days) values ('6','3','20','蒙脱石散','3g*10袋','盒','13.5','2','27.00','每日一次','口服','7');
insert into his_prescription_item(item_id,rx_id,drug_id,drug_name,specification,unit,price,quantity,amount,usage_dose,frequency,days) values ('7','3','13','氯化钠注射液','0.9% 250ml','瓶','4.8','2','9.60','每日一次','口服','7');
insert into his_prescription_item(item_id,rx_id,drug_id,drug_name,specification,unit,price,quantity,amount,usage_dose,frequency,days) values ('8','4','5','二甲双胍片','0.5g*60片','瓶','18.5','1','18.50','每日两次','口服','7');
insert into his_prescription_item(item_id,rx_id,drug_id,drug_name,specification,unit,price,quantity,amount,usage_dose,frequency,days) values ('9','4','7','阿托伐他汀钙片','20mg*7片','盒','32','1','32.00','每日两次','口服','7');
insert into his_prescription_item(item_id,rx_id,drug_id,drug_name,specification,unit,price,quantity,amount,usage_dose,frequency,days) values ('10','5','8','奥美拉唑肠溶胶囊','20mg*14粒','盒','16.5','2','33.00','每日一次','口服','7');
insert into his_prescription_item(item_id,rx_id,drug_id,drug_name,specification,unit,price,quantity,amount,usage_dose,frequency,days) values ('11','5','9','氯雷他定片','10mg*6片','盒','11','1','11.00','每日两次','口服','7');
insert into his_prescription_item(item_id,rx_id,drug_id,drug_name,specification,unit,price,quantity,amount,usage_dose,frequency,days) values ('12','6','3','连花清瘟胶囊','0.35g*24粒','盒','14.8','2','29.60','每日一次','口服','7');
insert into his_prescription_item(item_id,rx_id,drug_id,drug_name,specification,unit,price,quantity,amount,usage_dose,frequency,days) values ('13','6','23','复方甘草片','100片','瓶','11.5','1','11.50','每日两次','口服','7');
insert into his_prescription_item(item_id,rx_id,drug_id,drug_name,specification,unit,price,quantity,amount,usage_dose,frequency,days) values ('14','7','2','布洛芬缓释胶囊','0.3g*20粒','盒','15.8','1','15.80','每日两次','口服','7');
insert into his_prescription_item(item_id,rx_id,drug_id,drug_name,specification,unit,price,quantity,amount,usage_dose,frequency,days) values ('15','7','15','云南白药气雾剂','85g+30g','盒','35','1','35.00','每日两次','口服','7');
insert into his_prescription_item(item_id,rx_id,drug_id,drug_name,specification,unit,price,quantity,amount,usage_dose,frequency,days) values ('16','8','1','阿莫西林胶囊','0.25g*24粒','盒','12.5','1','12.50','每日两次','口服','7');
insert into his_prescription_item(item_id,rx_id,drug_id,drug_name,specification,unit,price,quantity,amount,usage_dose,frequency,days) values ('17','8','12','葡萄糖注射液','5% 250ml','瓶','5.5','3','16.50','每日三次','口服','7');
insert into his_prescription_item(item_id,rx_id,drug_id,drug_name,specification,unit,price,quantity,amount,usage_dose,frequency,days) values ('18','9','6','硝苯地平控释片','30mg*7片','盒','28','1','28.00','每日两次','口服','7');
insert into his_prescription_item(item_id,rx_id,drug_id,drug_name,specification,unit,price,quantity,amount,usage_dose,frequency,days) values ('19','10','4','头孢克肟分散片','0.1g*12片','盒','22','2','44.00','每日一次','口服','7');
insert into his_prescription_item(item_id,rx_id,drug_id,drug_name,specification,unit,price,quantity,amount,usage_dose,frequency,days) values ('20','10','20','蒙脱石散','3g*10袋','盒','13.5','1','13.50','每日两次','口服','7');
insert into his_prescription_item(item_id,rx_id,drug_id,drug_name,specification,unit,price,quantity,amount,usage_dose,frequency,days) values ('21','11','19','孟鲁司特钠咀嚼片','5mg*14片','盒','25.5','1','25.50','每日两次','口服','7');
insert into his_prescription_item(item_id,rx_id,drug_id,drug_name,specification,unit,price,quantity,amount,usage_dose,frequency,days) values ('22','11','2','布洛芬缓释胶囊','0.3g*20粒','盒','15.8','1','15.80','每日两次','口服','7');
insert into his_prescription_item(item_id,rx_id,drug_id,drug_name,specification,unit,price,quantity,amount,usage_dose,frequency,days) values ('23','12','21','左氧氟沙星片','0.5g*7片','盒','19.8','1','19.80','每日两次','口服','7');
insert into his_prescription_item(item_id,rx_id,drug_id,drug_name,specification,unit,price,quantity,amount,usage_dose,frequency,days) values ('24','12','13','氯化钠注射液','0.9% 250ml','瓶','4.8','1','4.80','每日两次','口服','7');

insert into his_admission(adm_id,adm_no,patient_id,dept_id,ward_id,bed_id,doctor_id,in_date,out_date,deposit,adm_status,diagnosis_in,create_by,create_time) values ('1','ZY000001','1','1','1','1','1',date_sub(now(), interval 4 day),null,'5000','0','高血压3级(高危)，头晕待查','admin',date_sub(now(), interval 4 day));
insert into his_admission(adm_id,adm_no,patient_id,dept_id,ward_id,bed_id,doctor_id,in_date,out_date,deposit,adm_status,diagnosis_in,create_by,create_time) values ('2','ZY000002','2','1','1','2','2',date_sub(now(), interval 3 day),null,'3000','0','冠心病，心功能II级','admin',date_sub(now(), interval 3 day));
insert into his_admission(adm_id,adm_no,patient_id,dept_id,ward_id,bed_id,doctor_id,in_date,out_date,deposit,adm_status,diagnosis_in,create_by,create_time) values ('4','ZY000004','4','1','1','3','3',date_sub(now(), interval 2 day),null,'3000','0','慢性胃炎急性发作','admin',date_sub(now(), interval 2 day));
insert into his_admission(adm_id,adm_no,patient_id,dept_id,ward_id,bed_id,doctor_id,in_date,out_date,deposit,adm_status,diagnosis_in,create_by,create_time) values ('5','ZY000005','5','2','2','9','4',date_sub(now(), interval 5 day),null,'8000','0','胆囊结石伴胆囊炎，待手术','admin',date_sub(now(), interval 5 day));
insert into his_admission(adm_id,adm_no,patient_id,dept_id,ward_id,bed_id,doctor_id,in_date,out_date,deposit,adm_status,diagnosis_in,create_by,create_time) values ('6','ZY000006','6','2','2','10','4',date_sub(now(), interval 1 day),null,'5000','0','右下腹痛：阑尾炎？','admin',date_sub(now(), interval 1 day));
insert into his_admission(adm_id,adm_no,patient_id,dept_id,ward_id,bed_id,doctor_id,in_date,out_date,deposit,adm_status,diagnosis_in,create_by,create_time) values ('7','ZY000007','7','3','3','17','6',date_sub(now(), interval 3 day),null,'2000','0','小儿支气管肺炎','admin',date_sub(now(), interval 3 day));
insert into his_admission(adm_id,adm_no,patient_id,dept_id,ward_id,bed_id,doctor_id,in_date,out_date,deposit,adm_status,diagnosis_in,create_by,create_time) values ('9','ZY000009','9','3','3','18','6',date_sub(now(), interval 2 day),null,'2000','0','小儿腹泻病伴脱水','admin',date_sub(now(), interval 2 day));
insert into his_admission(adm_id,adm_no,patient_id,dept_id,ward_id,bed_id,doctor_id,in_date,out_date,deposit,adm_status,diagnosis_in,create_by,create_time) values ('10','ZY000010','10','2','4','25','5',date_sub(now(), interval 6 day),null,'10000','0','脑出血恢复期','admin',date_sub(now(), interval 6 day));
insert into his_admission(adm_id,adm_no,patient_id,dept_id,ward_id,bed_id,doctor_id,in_date,out_date,deposit,adm_status,diagnosis_in,create_by,create_time) values ('11','ZY000011','11','1','1','4','1',date_sub(now(), interval 10 day),date_sub(now(), interval 6 day),'4000','1','2型糖尿病，血糖控制不佳','admin',date_sub(now(), interval 10 day));
insert into his_admission(adm_id,adm_no,patient_id,dept_id,ward_id,bed_id,doctor_id,in_date,out_date,deposit,adm_status,diagnosis_in,create_by,create_time) values ('13','ZY000013','13','4','4','26','8',date_sub(now(), interval 15 day),date_sub(now(), interval 9 day),'3000','1','妊娠38周，待产已分娩','admin',date_sub(now(), interval 15 day));

insert into his_medical_order(order_id,adm_id,patient_id,doctor_id,order_type,content,drug_id,dose,frequency,start_time,end_time,order_status,exec_by,exec_time,create_by,create_time) values ('1','1','1','1','0','低盐低脂饮食，持续心电监护',null,null,'每日一次',date_sub(now(), interval 4 day),null,'1','李护士',date_sub(now(), interval 4 day),'admin',date_sub(now(), interval 4 day));
insert into his_medical_order(order_id,adm_id,patient_id,doctor_id,order_type,content,drug_id,dose,frequency,start_time,end_time,order_status,exec_by,exec_time,create_by,create_time) values ('2','1','1','1','0','硝苯地平控释片 30mg 口服','6','30mg','每日一次',date_sub(now(), interval 4 day),null,'1','李护士',date_sub(now(), interval 4 day),'admin',date_sub(now(), interval 4 day));
insert into his_medical_order(order_id,adm_id,patient_id,doctor_id,order_type,content,drug_id,dose,frequency,start_time,end_time,order_status,exec_by,exec_time,create_by,create_time) values ('3','2','2','2','0','阿托伐他汀钙片 20mg 睡前服','7','20mg','每晚一次',date_sub(now(), interval 3 day),null,'1','王护士',date_sub(now(), interval 3 day),'admin',date_sub(now(), interval 3 day));
insert into his_medical_order(order_id,adm_id,patient_id,doctor_id,order_type,content,drug_id,dose,frequency,start_time,end_time,order_status,exec_by,exec_time,create_by,create_time) values ('4','2','2','2','1','急查心电图+心肌酶谱',null,null,'立即',date_sub(now(), interval 3 day),date_sub(now(), interval 3 day),'1','王护士',date_sub(now(), interval 3 day),'admin',date_sub(now(), interval 3 day));
insert into his_medical_order(order_id,adm_id,patient_id,doctor_id,order_type,content,drug_id,dose,frequency,start_time,end_time,order_status,exec_by,exec_time,create_by,create_time) values ('5','4','4','4','0','头孢曲松钠 2g+0.9%氯化钠250ml 静滴','24','2g','每日一次',date_sub(now(), interval 2 day),null,'1','张护士',date_sub(now(), interval 2 day),'admin',date_sub(now(), interval 2 day));
insert into his_medical_order(order_id,adm_id,patient_id,doctor_id,order_type,content,drug_id,dose,frequency,start_time,end_time,order_status,exec_by,exec_time,create_by,create_time) values ('6','5','5','4','1','术前禁食禁水8小时',null,null,'立即',date_sub(now(), interval 1 day),date_sub(now(), interval 1 day),'1','张护士',date_sub(now(), interval 1 day),'admin',date_sub(now(), interval 1 day));
insert into his_medical_order(order_id,adm_id,patient_id,doctor_id,order_type,content,drug_id,dose,frequency,start_time,end_time,order_status,exec_by,exec_time,create_by,create_time) values ('7','7','9','6','0','布洛芬混悬液 10ml 发热时服','2','10ml','必要时',date_sub(now(), interval 3 day),null,'0',null,null,'admin',date_sub(now(), interval 3 day));
insert into his_medical_order(order_id,adm_id,patient_id,doctor_id,order_type,content,drug_id,dose,frequency,start_time,end_time,order_status,exec_by,exec_time,create_by,create_time) values ('8','7','9','6','1','急查血常规+CRP',null,null,'立即',date_sub(now(), interval 3 day),date_sub(now(), interval 3 day),'1','刘护士',date_sub(now(), interval 3 day),'admin',date_sub(now(), interval 3 day));
insert into his_medical_order(order_id,adm_id,patient_id,doctor_id,order_type,content,drug_id,dose,frequency,start_time,end_time,order_status,exec_by,exec_time,create_by,create_time) values ('9','10','13','5','0','甘露醇125ml 静滴 降颅压','13','125ml','每8小时一次',date_sub(now(), interval 6 day),date_sub(now(), interval 2 day),'2','赵护士',date_sub(now(), interval 2 day),'admin',date_sub(now(), interval 6 day));
insert into his_medical_order(order_id,adm_id,patient_id,doctor_id,order_type,content,drug_id,dose,frequency,start_time,end_time,order_status,exec_by,exec_time,create_by,create_time) values ('10','10','13','5','0','气垫床、翻身拍背每2小时',null,null,'每2小时',date_sub(now(), interval 6 day),null,'1','赵护士',date_sub(now(), interval 6 day),'admin',date_sub(now(), interval 6 day));
insert into his_medical_order(order_id,adm_id,patient_id,doctor_id,order_type,content,drug_id,dose,frequency,start_time,end_time,order_status,exec_by,exec_time,create_by,create_time) values ('11','3','4','3','0','奥美拉唑20mg 口服','8','20mg','每日两次',date_sub(now(), interval 2 day),null,'0',null,null,'admin',date_sub(now(), interval 2 day));
insert into his_medical_order(order_id,adm_id,patient_id,doctor_id,order_type,content,drug_id,dose,frequency,start_time,end_time,order_status,exec_by,exec_time,create_by,create_time) values ('12','6','6','4','1','头孢克肟100mg 口服','4','100mg','每日两次',date_sub(now(), interval 1 day),null,'0',null,null,'admin',date_sub(now(), interval 1 day));

insert into his_nursing(record_id,adm_id,patient_id,nurse_name,record_time,temperature,pulse,breath,bp_high,bp_low,spo2,content,create_by,create_time) values ('1','1','1','李护士',date_sub(now(), interval 5 day),'37.8','82','18','125','82','97','晨间护理，生命体征平稳','admin',date_sub(now(), interval 5 day));
insert into his_nursing(record_id,adm_id,patient_id,nurse_name,record_time,temperature,pulse,breath,bp_high,bp_low,spo2,content,create_by,create_time) values ('2','1','1','李护士',date_sub(now(), interval 4 day),'37.2','78','17','123','81','97','晨间护理，生命体征平稳','admin',date_sub(now(), interval 4 day));
insert into his_nursing(record_id,adm_id,patient_id,nurse_name,record_time,temperature,pulse,breath,bp_high,bp_low,spo2,content,create_by,create_time) values ('3','1','1','李护士',date_sub(now(), interval 3 day),'36.8','75','16','121','80','97','晨间护理，生命体征平稳','admin',date_sub(now(), interval 3 day));
insert into his_nursing(record_id,adm_id,patient_id,nurse_name,record_time,temperature,pulse,breath,bp_high,bp_low,spo2,content,create_by,create_time) values ('4','1','1','李护士',date_sub(now(), interval 2 day),'36.5','72','16','119','79','97','晨间护理，生命体征平稳','admin',date_sub(now(), interval 2 day));
insert into his_nursing(record_id,adm_id,patient_id,nurse_name,record_time,temperature,pulse,breath,bp_high,bp_low,spo2,content,create_by,create_time) values ('5','1','1','李护士',date_sub(now(), interval 1 day),'36.6','74','17','117','78','97','晨间护理，生命体征平稳','admin',date_sub(now(), interval 1 day));
insert into his_nursing(record_id,adm_id,patient_id,nurse_name,record_time,temperature,pulse,breath,bp_high,bp_low,spo2,content,create_by,create_time) values ('6','1','1','李护士',date_sub(now(), interval 0 day),'36.4','71','16','115','77','97','晨间护理，生命体征平稳','admin',date_sub(now(), interval 0 day));
insert into his_nursing(record_id,adm_id,patient_id,nurse_name,record_time,temperature,pulse,breath,bp_high,bp_low,spo2,content,create_by,create_time) values ('7','2','2','王护士',date_sub(now(), interval 5 day),'37.8','82','18','125','82','97','晨间护理，生命体征平稳','admin',date_sub(now(), interval 5 day));
insert into his_nursing(record_id,adm_id,patient_id,nurse_name,record_time,temperature,pulse,breath,bp_high,bp_low,spo2,content,create_by,create_time) values ('8','2','2','王护士',date_sub(now(), interval 4 day),'37.2','78','17','123','81','97','晨间护理，生命体征平稳','admin',date_sub(now(), interval 4 day));
insert into his_nursing(record_id,adm_id,patient_id,nurse_name,record_time,temperature,pulse,breath,bp_high,bp_low,spo2,content,create_by,create_time) values ('9','2','2','王护士',date_sub(now(), interval 3 day),'36.8','75','16','121','80','97','晨间护理，生命体征平稳','admin',date_sub(now(), interval 3 day));
insert into his_nursing(record_id,adm_id,patient_id,nurse_name,record_time,temperature,pulse,breath,bp_high,bp_low,spo2,content,create_by,create_time) values ('10','2','2','王护士',date_sub(now(), interval 2 day),'36.5','72','16','119','79','97','晨间护理，生命体征平稳','admin',date_sub(now(), interval 2 day));
insert into his_nursing(record_id,adm_id,patient_id,nurse_name,record_time,temperature,pulse,breath,bp_high,bp_low,spo2,content,create_by,create_time) values ('11','2','2','王护士',date_sub(now(), interval 1 day),'36.6','74','17','117','78','97','晨间护理，生命体征平稳','admin',date_sub(now(), interval 1 day));
insert into his_nursing(record_id,adm_id,patient_id,nurse_name,record_time,temperature,pulse,breath,bp_high,bp_low,spo2,content,create_by,create_time) values ('12','2','2','王护士',date_sub(now(), interval 0 day),'36.4','71','16','115','77','97','晨间护理，生命体征平稳','admin',date_sub(now(), interval 0 day));

insert into his_lab_test(test_id,test_no,patient_id,visit_id,adm_id,doctor_id,test_item,sample_type,status,apply_time,sample_time,report_time,result_summary,report_by,create_by,create_time) values ('1','JY000001','1','1',null,'1','血常规五分类','0','3',date_sub(now(), interval 5 day),date_sub(now(), interval 5 day),date_sub(now(), interval 5 day),'白细胞偏高，中性粒细胞比例升高','admin','admin',date_sub(now(), interval 5 day));
insert into his_lab_test(test_id,test_no,patient_id,visit_id,adm_id,doctor_id,test_item,sample_type,status,apply_time,sample_time,report_time,result_summary,report_by,create_by,create_time) values ('2','JY000002','2','2',null,'2','肝功能全套','0','3',date_sub(now(), interval 4 day),date_sub(now(), interval 4 day),date_sub(now(), interval 4 day),'ALT轻度升高，余正常','admin','admin',date_sub(now(), interval 4 day));
insert into his_lab_test(test_id,test_no,patient_id,visit_id,adm_id,doctor_id,test_item,sample_type,status,apply_time,sample_time,report_time,result_summary,report_by,create_by,create_time) values ('3','JY000003','4',null,'3','4','血糖+肾功能','0','3',date_sub(now(), interval 2 day),date_sub(now(), interval 2 day),date_sub(now(), interval 1 day),'空腹血糖8.2偏高','admin','admin',date_sub(now(), interval 2 day));
insert into his_lab_test(test_id,test_no,patient_id,visit_id,adm_id,doctor_id,test_item,sample_type,status,apply_time,sample_time,report_time,result_summary,report_by,create_by,create_time) values ('4','JY000004','5',null,'4','4','术前四项','0','2',date_sub(now(), interval 1 day),date_sub(now(), interval 1 day),null,null,null,'admin',date_sub(now(), interval 1 day));
insert into his_lab_test(test_id,test_no,patient_id,visit_id,adm_id,doctor_id,test_item,sample_type,status,apply_time,sample_time,report_time,result_summary,report_by,create_by,create_time) values ('5','JY000005','9',null,'7','6','血常规+CRP','0','1',now(),now(),null,null,null,'admin',now());
insert into his_lab_test(test_id,test_no,patient_id,visit_id,adm_id,doctor_id,test_item,sample_type,status,apply_time,sample_time,report_time,result_summary,report_by,create_by,create_time) values ('6','JY000006','3',null,'8','6','尿常规','1','0',now(),null,null,null,null,'admin',now());
insert into his_lab_test(test_id,test_no,patient_id,visit_id,adm_id,doctor_id,test_item,sample_type,status,apply_time,sample_time,report_time,result_summary,report_by,create_by,create_time) values ('7','JY000007','7',null,'9','7','血型鉴定','0','0',now(),null,null,null,null,'admin',now());

insert into his_lab_result(result_id,test_id,item_name,result_value,unit,ref_range,flag) values ('1','1','白细胞计数(WBC)','11.2','10^9/L','3.5-9.5','1');
insert into his_lab_result(result_id,test_id,item_name,result_value,unit,ref_range,flag) values ('2','1','中性粒细胞比例','78.5','%','40-75','1');
insert into his_lab_result(result_id,test_id,item_name,result_value,unit,ref_range,flag) values ('3','1','血红蛋白','135','g/L','130-175','0');
insert into his_lab_result(result_id,test_id,item_name,result_value,unit,ref_range,flag) values ('4','1','血小板计数','210','10^9/L','125-350','0');
insert into his_lab_result(result_id,test_id,item_name,result_value,unit,ref_range,flag) values ('5','2','谷丙转氨酶(ALT)','65','U/L','9-50','1');
insert into his_lab_result(result_id,test_id,item_name,result_value,unit,ref_range,flag) values ('6','2','谷草转氨酶(AST)','42','U/L','15-40','1');
insert into his_lab_result(result_id,test_id,item_name,result_value,unit,ref_range,flag) values ('7','2','总胆红素','15.2','umol/L','5-21','0');
insert into his_lab_result(result_id,test_id,item_name,result_value,unit,ref_range,flag) values ('8','3','空腹血糖','8.2','mmol/L','3.9-6.1','1');
insert into his_lab_result(result_id,test_id,item_name,result_value,unit,ref_range,flag) values ('9','3','肌酐','88','umol/L','57-111','0');
insert into his_lab_result(result_id,test_id,item_name,result_value,unit,ref_range,flag) values ('10','3','尿素氮','5.6','mmol/L','3.6-9.5','0');

insert into his_exam(exam_id,exam_no,patient_id,doctor_id,exam_type,body_part,purpose,status,apply_time,report_time,finding,conclusion,report_by,create_by,create_time) values ('1','JC000001','1','1','0','胸部','咳嗽发热待查','2',date_sub(now(), interval 5 day),date_sub(now(), interval 5 day),'双肺纹理增粗，右下肺见斑片状阴影','右下肺炎症','admin','admin',date_sub(now(), interval 5 day));
insert into his_exam(exam_id,exam_no,patient_id,doctor_id,exam_type,body_part,purpose,status,apply_time,report_time,finding,conclusion,report_by,create_by,create_time) values ('2','JC000002','2','2','4','心脏','胸闷待查','2',date_sub(now(), interval 4 day),date_sub(now(), interval 4 day),'窦性心律，ST段压低','心肌缺血改变','admin','admin',date_sub(now(), interval 4 day));
insert into his_exam(exam_id,exam_no,patient_id,doctor_id,exam_type,body_part,purpose,status,apply_time,report_time,finding,conclusion,report_by,create_by,create_time) values ('3','JC000003','5','4','3','右上腹','腹痛待查','2',date_sub(now(), interval 3 day),date_sub(now(), interval 3 day),'胆囊壁增厚，内见多发强回声','胆囊结石伴胆囊炎','admin','admin',date_sub(now(), interval 3 day));
insert into his_exam(exam_id,exam_no,patient_id,doctor_id,exam_type,body_part,purpose,status,apply_time,report_time,finding,conclusion,report_by,create_by,create_time) values ('4','JC000004','10','5','2','腰椎','腰痛待查','1',date_sub(now(), interval 1 day),null,null,null,null,'admin',date_sub(now(), interval 1 day));
insert into his_exam(exam_id,exam_no,patient_id,doctor_id,exam_type,body_part,purpose,status,apply_time,report_time,finding,conclusion,report_by,create_by,create_time) values ('5','JC000005','7','7','0','头颅','头晕待查','0',now(),null,null,null,null,'admin',now());
insert into his_exam(exam_id,exam_no,patient_id,doctor_id,exam_type,body_part,purpose,status,apply_time,report_time,finding,conclusion,report_by,create_by,create_time) values ('6','JC000006','12','8','3','子宫附件','早孕检查','0',now(),null,null,null,null,'admin',now());

insert into his_surgery(surgery_id,surgery_no,surgery_name,patient_id,dept_id,surgeon_id,anesthesia,room_no,level,plan_time,start_time,end_time,status,create_by,create_time) values ('1','SS000001','腹腔镜胆囊切除术','5','2','4','0','1号手术间','2',date_add(now(), interval 1 day),null,null,'0','admin',date_sub(now(), interval 2 day));
insert into his_surgery(surgery_id,surgery_no,surgery_name,patient_id,dept_id,surgeon_id,anesthesia,room_no,level,plan_time,start_time,end_time,status,create_by,create_time) values ('2','SS000002','阑尾切除术','6','2','4','1','2号手术间','1',now(),now(),null,'1','admin',date_sub(now(), interval 1 day));
insert into his_surgery(surgery_id,surgery_no,surgery_name,patient_id,dept_id,surgeon_id,anesthesia,room_no,level,plan_time,start_time,end_time,status,create_by,create_time) values ('3','SS000003','剖宫产术','13','4','8','3','3号手术间','2',date_sub(now(), interval 9 day),date_sub(now(), interval 9 day),date_sub(now(), interval 9 day),'2','admin',date_sub(now(), interval 10 day));
insert into his_surgery(surgery_id,surgery_no,surgery_name,patient_id,dept_id,surgeon_id,anesthesia,room_no,level,plan_time,start_time,end_time,status,create_by,create_time) values ('4','SS000004','骨折切开复位内固定术','15','2','5','1','1号手术间','2',date_sub(now(), interval 7 day),date_sub(now(), interval 7 day),date_sub(now(), interval 7 day),'2','admin',date_sub(now(), interval 8 day));
insert into his_surgery(surgery_id,surgery_no,surgery_name,patient_id,dept_id,surgeon_id,anesthesia,room_no,level,plan_time,start_time,end_time,status,create_by,create_time) values ('5','SS000005','白内障超声乳化术','14','5','9','1','4号手术间','1',date_add(now(), interval 2 day),null,null,'0','admin',date_sub(now(), interval 1 day));

insert into his_purchase(purchase_id,purchase_no,supplier_id,total_amount,status,in_time,create_by,create_time) values ('1','CG000001','1','4850.00','1','2026-02-10 10:00:00','admin','2026-02-09 09:00:00');
insert into his_purchase(purchase_id,purchase_no,supplier_id,total_amount,status,in_time,create_by,create_time) values ('2','CG000002','2','3240.00','1','2026-03-01 10:00:00','admin','2026-02-28 09:00:00');
insert into his_purchase(purchase_id,purchase_no,supplier_id,total_amount,status,in_time,create_by,create_time) values ('3','CG000003','3','2100.00','0',null,'admin','2026-09-10 09:00:00');

insert into his_purchase_item(item_id,purchase_id,drug_id,quantity,price,amount) values ('1','1','1','100','12','1200.00');
insert into his_purchase_item(item_id,purchase_id,drug_id,quantity,price,amount) values ('2','1','4','80','20.5','1640.00');
insert into his_purchase_item(item_id,purchase_id,drug_id,quantity,price,amount) values ('3','1','24','90','21','1890.00');
insert into his_purchase_item(item_id,purchase_id,drug_id,quantity,price,amount) values ('4','1','10','15','7','105.00');
insert into his_purchase_item(item_id,purchase_id,drug_id,quantity,price,amount) values ('5','2','12','200','5','1000.00');
insert into his_purchase_item(item_id,purchase_id,drug_id,quantity,price,amount) values ('6','2','13','200','4.5','900.00');
insert into his_purchase_item(item_id,purchase_id,drug_id,quantity,price,amount) values ('7','2','18','20','62','1240.00');
insert into his_purchase_item(item_id,purchase_id,drug_id,quantity,price,amount) values ('8','3','16','100','11','1100.00');
insert into his_purchase_item(item_id,purchase_id,drug_id,quantity,price,amount) values ('9','3','17','50','14','700.00');
insert into his_purchase_item(item_id,purchase_id,drug_id,quantity,price,amount) values ('10','3','22','40','7.5','300.00');

insert into his_stock_record(record_id,record_no,record_type,drug_id,quantity,before_stock,after_stock,source_id,operator,create_by,create_time) values ('1','RK000001','0','1','100','200','300','1','admin','admin',date_sub(now(), interval 30 day));
insert into his_stock_record(record_id,record_no,record_type,drug_id,quantity,before_stock,after_stock,source_id,operator,create_by,create_time) values ('2','RK000002','0','4','80','70','150','1','admin','admin',date_sub(now(), interval 30 day));
insert into his_stock_record(record_id,record_no,record_type,drug_id,quantity,before_stock,after_stock,source_id,operator,create_by,create_time) values ('3','RK000003','0','24','90','110','200','1','admin','admin',date_sub(now(), interval 30 day));
insert into his_stock_record(record_id,record_no,record_type,drug_id,quantity,before_stock,after_stock,source_id,operator,create_by,create_time) values ('4','RK000004','0','12','200','300','500','2','admin','admin',date_sub(now(), interval 15 day));
insert into his_stock_record(record_id,record_no,record_type,drug_id,quantity,before_stock,after_stock,source_id,operator,create_by,create_time) values ('5','RK000005','0','13','200','400','600','2','admin','admin',date_sub(now(), interval 15 day));
insert into his_stock_record(record_id,record_no,record_type,drug_id,quantity,before_stock,after_stock,source_id,operator,create_by,create_time) values ('6','CK000001','1','1','2','302','300',null,'admin','admin',date_sub(now(), interval 4 day));
insert into his_stock_record(record_id,record_no,record_type,drug_id,quantity,before_stock,after_stock,source_id,operator,create_by,create_time) values ('7','CK000002','1','2','1','261','260',null,'admin','admin',date_sub(now(), interval 4 day));
insert into his_stock_record(record_id,record_no,record_type,drug_id,quantity,before_stock,after_stock,source_id,operator,create_by,create_time) values ('8','CK000003','1','4','1','151','150',null,'admin','admin',date_sub(now(), interval 3 day));
insert into his_stock_record(record_id,record_no,record_type,drug_id,quantity,before_stock,after_stock,source_id,operator,create_by,create_time) values ('9','CK000004','1','20','2','192','190',null,'admin','admin',date_sub(now(), interval 2 day));
insert into his_stock_record(record_id,record_no,record_type,drug_id,quantity,before_stock,after_stock,source_id,operator,create_by,create_time) values ('10','CK000005','1','8','2','202','200',null,'admin','admin',date_sub(now(), interval 1 day));

insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('41','CFF1','1','1','1','处方药品费','48.80','1','48.80','1','1',date_sub(curdate(), interval 6 day),'admin','admin',date_sub(curdate(), interval 6 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('42','CFF2','2','1','2','处方药品费','41.60','1','41.60','1','1',date_sub(curdate(), interval 6 day),'admin','admin',date_sub(curdate(), interval 6 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('43','CFF3','3','1','3','处方药品费','36.60','1','36.60','1','1',date_sub(curdate(), interval 6 day),'admin','admin',date_sub(curdate(), interval 6 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('44','CFF4','4','1','4','处方药品费','50.50','1','50.50','1','1',date_sub(curdate(), interval 6 day),'admin','admin',date_sub(curdate(), interval 6 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('45','CFF5','5','1','5','处方药品费','44.00','1','44.00','1','1',date_sub(curdate(), interval 6 day),'admin','admin',date_sub(curdate(), interval 6 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('46','CFF6','6','1','6','处方药品费','41.10','1','41.10','1','1',date_sub(curdate(), interval 5 day),'admin','admin',date_sub(curdate(), interval 5 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('47','CFF7','7','1','7','处方药品费','50.80','1','50.80','1','1',date_sub(curdate(), interval 5 day),'admin','admin',date_sub(curdate(), interval 5 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('48','CFF8','8','1','8','处方药品费','29.00','1','29.00','1','1',date_sub(curdate(), interval 5 day),'admin','admin',date_sub(curdate(), interval 5 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('49','CFF9','9','1','9','处方药品费','28.00','1','28.00','1','1',date_sub(curdate(), interval 5 day),'admin','admin',date_sub(curdate(), interval 5 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('50','CFF10','10','1','10','处方药品费','57.50','1','57.50','1','1',date_sub(curdate(), interval 5 day),'admin','admin',date_sub(curdate(), interval 5 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('51','JYF1','1','2','1','检验费','25','1','25','1','1',date_sub(now(), interval 1 day),'admin','admin',date_sub(now(), interval 1 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('52','JYF2','2','2','2','检验费','25','1','25','1','1',date_sub(now(), interval 2 day),'admin','admin',date_sub(now(), interval 2 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('53','JYF3','4','2','3','检验费','25','1','25','1','1',date_sub(now(), interval 3 day),'admin','admin',date_sub(now(), interval 3 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('54','JYF4','5','2','4','检验费','25','1','25','1','1',date_sub(now(), interval 4 day),'admin','admin',date_sub(now(), interval 4 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('55','JYF5','9','2','5','检验费','25','1','25','0',null,null,null,'admin',date_sub(now(), interval 5 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('56','JYF6','3','2','6','检验费','25','1','25','0',null,null,null,'admin',date_sub(now(), interval 6 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('57','JYF7','7','2','7','检验费','25','1','25','0',null,null,null,'admin',date_sub(now(), interval 7 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('58','JCF1','1','3','1','检查费','280','1','280','1','1',date_sub(now(), interval 1 day),'admin','admin',date_sub(now(), interval 1 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('59','JCF2','2','3','2','检查费','40','1','40','1','1',date_sub(now(), interval 2 day),'admin','admin',date_sub(now(), interval 2 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('60','JCF3','5','3','3','检查费','120','1','120','1','1',date_sub(now(), interval 3 day),'admin','admin',date_sub(now(), interval 3 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('61','JCF4','10','3','4','检查费','80','1','80','0',null,null,null,'admin',date_sub(now(), interval 4 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('62','JCF5','7','3','5','检查费','280','1','280','0',null,null,null,'admin',date_sub(now(), interval 5 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('63','JCF6','12','3','6','检查费','120','1','120','0',null,null,null,'admin',date_sub(now(), interval 6 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('64','ZYF-Y1','1','4','1','住院押金','5000','1','5000','1','1',date_sub(now(), interval 4 day),'admin','admin',date_sub(now(), interval 4 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('65','ZYF-Y2','2','4','2','住院押金','3000','1','3000','1','1',date_sub(now(), interval 3 day),'admin','admin',date_sub(now(), interval 3 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('66','ZYF-Y3','4','4','3','住院押金','3000','1','3000','1','1',date_sub(now(), interval 2 day),'admin','admin',date_sub(now(), interval 2 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('67','ZYF-Y4','5','4','4','住院押金','8000','1','8000','1','1',date_sub(now(), interval 5 day),'admin','admin',date_sub(now(), interval 5 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('68','ZYF-Y5','6','4','5','住院押金','5000','1','5000','1','1',date_sub(now(), interval 1 day),'admin','admin',date_sub(now(), interval 1 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('69','ZYF-Y6','7','4','6','住院押金','2000','1','2000','1','1',date_sub(now(), interval 3 day),'admin','admin',date_sub(now(), interval 3 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('70','ZYF-Y7','9','4','7','住院押金','2000','1','2000','1','1',date_sub(now(), interval 2 day),'admin','admin',date_sub(now(), interval 2 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('71','ZYF-Y8','10','4','8','住院押金','10000','1','10000','1','1',date_sub(now(), interval 6 day),'admin','admin',date_sub(now(), interval 6 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('72','YZF1','1','4','1','用药-硝苯地平控释片','28','1','28','0',null,null,null,'admin',date_sub(now(), interval 4 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('73','YZF2','2','4','3','用药-阿托伐他汀钙片','32','1','32','0',null,null,null,'admin',date_sub(now(), interval 3 day));
insert into his_charge(charge_id,charge_no,patient_id,source_type,source_id,item_name,price,quantity,amount,charge_status,pay_type,settle_time,operator,create_by,create_time) values ('74','YZF3','4','4','5','用药-注射用头孢曲松钠','22','2','44','0',null,null,null,'admin',date_sub(now(), interval 2 day));


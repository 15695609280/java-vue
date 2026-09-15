-- 患者表新增紧急联系电话字段
ALTER TABLE `his_patient`
    ADD COLUMN `emergency_phone` VARCHAR(20) DEFAULT NULL COMMENT '紧急联系电话' AFTER `phone`;

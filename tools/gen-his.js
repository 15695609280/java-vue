/**
 * 医院信息系统(HIS) 代码生成器
 * 用法: node tools/gen-his.js
 * 依据 his-meta.js 元数据生成:
 *   后端: ruoyi-system/com/ruoyi/his/{domain,mapper,service,service.impl} + mapper/his/*.xml
 *         ruoyi-admin/com/ruoyi/web/controller/his/*Controller.java
 *   前端: src/api/his/*.js + src/views/his/<group>/<view>/index.vue (简单CRUD页)
 *   SQL : java-test/sql/his.sql (建表 + 字典 + 菜单 + 演示数据)
 */
const fs = require('fs');
const path = require('path');
const { ENTITIES, DICTS, MENUS } = require('./his-meta');

const ROOT = path.resolve(__dirname, '..');
const SYS = path.join(ROOT, 'java-test/ruoyi-system/src/main/java/com/ruoyi/his');
const CTRL = path.join(ROOT, 'java-test/ruoyi-admin/src/main/java/com/ruoyi/web/controller/his');
const XMLDIR = path.join(ROOT, 'java-test/ruoyi-system/src/main/resources/mapper/his');
const API = path.join(ROOT, 'RuoYi-Vue3-master/src/api/his');
const VIEWS = path.join(ROOT, 'RuoYi-Vue3-master/src/views/his');
const SQLFILE = path.join(ROOT, 'java-test/sql/his.sql');

const w = (file, content) => { fs.mkdirSync(path.dirname(file), { recursive: true }); fs.writeFileSync(file, content, 'utf8'); };
const cap = s => s.charAt(0).toUpperCase() + s.slice(1);
const low = s => s.charAt(0).toLowerCase() + s.slice(1);
const snake = s => s.replace(/([A-Z])/g, '_$1').toLowerCase();

/* ===================== 复杂业务扩展代码 ===================== */
const EXTRAS = {

  /* ---------- 挂号 Registration ---------- */
  Registration: {
    mapper: `    /** 查询排班当前最大排队号 */\n    public Integer selectMaxQueueNo(Long scheduleId);\n\n    /** 修改就诊状态 */\n    public int updateVisitStatus(@Param("regId") Long regId, @Param("visitStatus") String visitStatus);\n\n    /** 修改缴费状态 */\n    public int updatePayStatus(@Param("regId") Long regId, @Param("payStatus") String payStatus);\n\n    /** 候诊队列(按排队号) */\n    public List<Registration> selectQueueList(Registration registration);`,
    mapperImports: ['org.apache.ibatis.annotations.Param'],
    xml: `    <select id="selectMaxQueueNo" parameterType="Long" resultType="Integer">\n        select ifnull(max(queue_no), 0) from his_registration where schedule_id = #{scheduleId}\n    </select>\n\n    <update id="updateVisitStatus">\n        update his_registration set visit_status = #{visitStatus} where reg_id = #{regId}\n    </update>\n\n    <update id="updatePayStatus">\n        update his_registration set pay_status = #{payStatus} where reg_id = #{regId}\n    </update>\n\n    <select id="selectQueueList" parameterType="com.ruoyi.his.domain.Registration" resultMap="RegistrationResult">\n        <include refid="selectRegistrationVo"/>\n        <where>\n            t.visit_status in ('0','1','4')\n            <if test="regDate != null"> AND t.reg_date = #{regDate}</if>\n            <if test="deptId != null"> AND t.dept_id = #{deptId}</if>\n            <if test="doctorId != null"> AND t.doctor_id = #{doctorId}</if>\n            <if test="visitStatus != null and visitStatus != ''"> AND t.visit_status = #{visitStatus}</if>\n        </where>\n        order by t.queue_no\n    </select>`,
    service: `    /** 挂号：生成单号/排队号，扣号源，生成挂号费账单 */\n    public Long register(Registration registration);\n\n    /** 状态流转: 1叫号就诊 3退号 4过号 */\n    public int changeStatus(Long regId, String status);\n\n    /** 候诊队列 */\n    public List<Registration> selectQueueList(Registration registration);`,
    implImports: ['com.ruoyi.his.domain.Schedule', 'com.ruoyi.his.domain.Doctor', 'com.ruoyi.his.domain.Charge',
      'com.ruoyi.his.mapper.ScheduleMapper', 'com.ruoyi.his.mapper.DoctorMapper', 'com.ruoyi.his.mapper.ChargeMapper',
      'com.ruoyi.common.exception.ServiceException', 'com.ruoyi.common.utils.DateUtils',
      'org.springframework.transaction.annotation.Transactional', 'java.math.BigDecimal'],
    implAutowired: ['ScheduleMapper scheduleMapper', 'DoctorMapper doctorMapper', 'ChargeMapper chargeMapper'],
    impl: `    /** 挂号：生成单号/排队号，扣号源，生成挂号费账单 */\n    @Override\n    @Transactional\n    public Long register(Registration registration)\n    {\n        Schedule schedule = scheduleMapper.selectScheduleByScheduleId(registration.getScheduleId());\n        if (schedule == null || !"0".equals(schedule.getStatus()))\n        {\n            throw new ServiceException("排班不存在或已停诊");\n        }\n        if (scheduleMapper.decrQuota(schedule.getScheduleId()) <= 0)\n        {\n            throw new ServiceException("该时段号源已约满");\n        }\n        Doctor doctor = doctorMapper.selectDoctorByDoctorId(schedule.getDoctorId());\n        registration.setRegNo("GH" + DateUtils.dateTimeNow("yyyyMMddHHmmss") + String.format("%03d", new java.util.Random().nextInt(1000)));\n        registration.setQueueNo(registrationMapper.selectMaxQueueNo(schedule.getScheduleId()) + 1);\n        registration.setDeptId(schedule.getDeptId());\n        registration.setDoctorId(schedule.getDoctorId());\n        registration.setRegDate(schedule.getWorkDate());\n        registration.setTimeSlot(schedule.getTimeSlot());\n        registration.setRegFee(doctor != null && doctor.getRegFee() != null ? doctor.getRegFee() : BigDecimal.ZERO);\n        registration.setVisitStatus("0");\n        registration.setPayStatus("0");\n        registrationMapper.insertRegistration(registration);\n\n        Charge charge = new Charge();\n        charge.setChargeNo("GHF" + registration.getRegNo());\n        charge.setPatientId(registration.getPatientId());\n        charge.setSourceType("0");\n        charge.setSourceId(registration.getRegId());\n        charge.setItemName("挂号费");\n        charge.setPrice(registration.getRegFee());\n        charge.setQuantity(1);\n        charge.setAmount(registration.getRegFee());\n        charge.setChargeStatus("0");\n        chargeMapper.insertCharge(charge);\n        return registration.getRegId();\n    }\n\n    /** 状态流转: 1叫号就诊 3退号(退号源+作废未缴挂号费) 4过号 */\n    @Override\n    @Transactional\n    public int changeStatus(Long regId, String status)\n    {\n        Registration reg = registrationMapper.selectRegistrationByRegId(regId);\n        if (reg == null) { throw new ServiceException("挂号单不存在"); }\n        if ("3".equals(status))\n        {\n            if (!"0".equals(reg.getVisitStatus()) && !"4".equals(reg.getVisitStatus()))\n            {\n                throw new ServiceException("只有待就诊/过号才能退号");\n            }\n            if (reg.getScheduleId() != null) { scheduleMapper.incrQuota(reg.getScheduleId()); }\n            chargeMapper.cancelBySource(regId, "0");\n        }\n        else if ("4".equals(status) && !"0".equals(reg.getVisitStatus()) && !"1".equals(reg.getVisitStatus()))\n        {\n            throw new ServiceException("当前状态不能过号");\n        }\n        else if ("1".equals(status) && !"0".equals(reg.getVisitStatus()) && !"4".equals(reg.getVisitStatus()))\n        {\n            throw new ServiceException("当前状态不能叫号");\n        }\n        return registrationMapper.updateVisitStatus(regId, status);\n    }\n\n    @Override\n    public List<Registration> selectQueueList(Registration registration)\n    {\n        return registrationMapper.selectQueueList(registration);\n    }`,
    api: `// 挂号(选择排班，自动扣号源/生成排队号/生成挂号费账单)\nexport function register(data) {\n  return request({ url: '/his/registration/register', method: 'post', data: data })\n}\n\n// 状态流转 1叫号 3退号 4过号\nexport function changeRegStatus(regId, status) {\n  return request({ url: '/his/registration/status/' + regId + '/' + status, method: 'put' })\n}\n\n// 候诊队列\nexport function listQueue(query) {\n  return request({ url: '/his/registration/queue', method: 'get', params: query })\n}`,
    controller: `    /** 挂号 */\n    @PreAuthorize("@ss.hasPermi('his:registration:add')")\n    @Log(title = "挂号", businessType = BusinessType.INSERT)\n    @PostMapping("/register")\n    public AjaxResult register(@RequestBody Registration registration)\n    {\n        Long regId = registrationService.register(registration);\n        return success(registrationService.selectRegistrationByRegId(regId));\n    }\n\n    /** 状态流转: 1叫号就诊 3退号 4过号 */\n    @PreAuthorize("@ss.hasPermi('his:registration:refund') or @ss.hasPermi('his:queue:call') or @ss.hasPermi('his:workstation:visit')")\n    @Log(title = "挂号状态流转", businessType = BusinessType.UPDATE)\n    @PutMapping("/status/{regId}/{status}")\n    public AjaxResult changeStatus(@PathVariable("regId") Long regId, @PathVariable("status") String status)\n    {\n        return toAjax(registrationService.changeStatus(regId, status));\n    }\n\n    /** 候诊队列 */\n    @GetMapping("/queue")\n    public TableDataInfo queue(Registration registration)\n    {\n        List<Registration> list = registrationService.selectQueueList(registration);\n        return getDataTable(list);\n    }`,
  },

  /* ---------- 处方 Prescription ---------- */
  Prescription: {
    domain: `    /** 处方明细 */\n    private List<PrescriptionItem> itemList;\n\n    public List<PrescriptionItem> getItemList() { return itemList; }\n    public void setItemList(List<PrescriptionItem> itemList) { this.itemList = itemList; }`,
    domainImports: ['java.util.List', 'com.ruoyi.his.domain.PrescriptionItem'],
    mapper: `    /** 修改处方状态 */\n    public int updateStatus(@Param("rxId") Long rxId, @Param("status") String status);`,
    mapperImports: ['org.apache.ibatis.annotations.Param'],
    xml: `    <update id="updateStatus">\n        update his_prescription set status = #{status} where rx_id = #{rxId}\n    </update>`,
    service: `    /** 开具处方(含明细，自动算总额+生成待收费账单) */\n    public Long createPrescription(Prescription prescription);\n\n    /** 发药(校验库存->扣减->写出入库记录) */\n    public int dispense(Long rxId);\n\n    /** 作废处方(未发药才允许) */\n    public int cancel(Long rxId);`,
    implImports: ['com.ruoyi.his.domain.PrescriptionItem', 'com.ruoyi.his.domain.Charge', 'com.ruoyi.his.domain.Drug',
      'com.ruoyi.his.domain.StockRecord', 'com.ruoyi.his.mapper.PrescriptionItemMapper', 'com.ruoyi.his.mapper.ChargeMapper',
      'com.ruoyi.his.mapper.DrugMapper', 'com.ruoyi.his.mapper.StockRecordMapper',
      'com.ruoyi.common.exception.ServiceException', 'com.ruoyi.common.utils.DateUtils', 'com.ruoyi.common.utils.SecurityUtils',
      'org.springframework.transaction.annotation.Transactional', 'java.math.BigDecimal', 'java.util.List', 'java.util.Date'],
    implAutowired: ['PrescriptionItemMapper prescriptionItemMapper', 'ChargeMapper chargeMapper', 'DrugMapper drugMapper', 'StockRecordMapper stockRecordMapper'],
    impl: `    /** 开具处方(含明细) */\n    @Override\n    @Transactional\n    public Long createPrescription(Prescription rx)\n    {\n        if (rx.getItemList() == null || rx.getItemList().isEmpty())\n        {\n            throw new ServiceException("处方明细不能为空");\n        }\n        BigDecimal total = BigDecimal.ZERO;\n        for (PrescriptionItem item : rx.getItemList())\n        {\n            if (item.getDrugId() == null || item.getQuantity() == null || item.getQuantity() <= 0)\n            {\n                throw new ServiceException("明细中药品/数量不完整");\n            }\n            item.setAmount(item.getPrice().multiply(new BigDecimal(item.getQuantity())));\n            total = total.add(item.getAmount());\n        }\n        rx.setTotalAmount(total);\n        rx.setStatus("0");\n        rx.setRxNo("CF" + DateUtils.dateTimeNow("yyyyMMddHHmmss") + String.format("%03d", new java.util.Random().nextInt(1000)));\n        prescriptionMapper.insertPrescription(rx);\n        for (PrescriptionItem item : rx.getItemList())\n        {\n            item.setRxId(rx.getRxId());\n            prescriptionItemMapper.insertPrescriptionItem(item);\n        }\n        Charge charge = new Charge();\n        charge.setChargeNo("CF" + rx.getRxNo());\n        charge.setPatientId(rx.getPatientId());\n        charge.setSourceType("1");\n        charge.setSourceId(rx.getRxId());\n        charge.setItemName("处方药品费");\n        charge.setPrice(total);\n        charge.setQuantity(1);\n        charge.setAmount(total);\n        charge.setChargeStatus("0");\n        chargeMapper.insertCharge(charge);\n        return rx.getRxId();\n    }\n\n    /** 发药：校验库存 -> 逐行扣减 -> 写出库记录 -> 处方状态置已发药 */\n    @Override\n    @Transactional\n    public int dispense(Long rxId)\n    {\n        Prescription rx = prescriptionMapper.selectPrescriptionByRxId(rxId);\n        if (rx == null) { throw new ServiceException("处方不存在"); }\n        if (!"1".equals(rx.getStatus())) { throw new ServiceException("处方未收费或已发药"); }\n        List<PrescriptionItem> items = prescriptionItemMapper.selectByRxId(rxId);\n        for (PrescriptionItem item : items)\n        {\n            if (drugMapper.deductStock(item.getDrugId(), item.getQuantity()) <= 0)\n            {\n                throw new ServiceException("药品【" + item.getDrugName() + "】库存不足");\n            }\n            Drug drug = drugMapper.selectDrugByDrugId(item.getDrugId());\n            StockRecord rec = new StockRecord();\n            rec.setRecordNo("CK" + DateUtils.dateTimeNow("yyyyMMddHHmmss") + String.format("%03d", new java.util.Random().nextInt(1000)));\n            rec.setRecordType("1");\n            rec.setDrugId(item.getDrugId());\n            rec.setQuantity(item.getQuantity());\n            rec.setAfterStock(drug.getStock());\n            rec.setBeforeStock(drug.getStock() + item.getQuantity());\n            rec.setSourceId(rxId);\n            rec.setOperator(SecurityUtils.getUsername());\n            stockRecordMapper.insertStockRecord(rec);\n        }\n        Prescription upd = new Prescription();\n        upd.setRxId(rxId);\n        upd.setStatus("2");\n        upd.setDispenseBy(SecurityUtils.getUsername());\n        upd.setDispenseTime(new Date());\n        return prescriptionMapper.updatePrescription(upd);\n    }\n\n    /** 作废处方(未发药才允许；已收费的先退费) */\n    @Override\n    @Transactional\n    public int cancel(Long rxId)\n    {\n        Prescription rx = prescriptionMapper.selectPrescriptionByRxId(rxId);\n        if (rx == null) { throw new ServiceException("处方不存在"); }\n        if ("2".equals(rx.getStatus())) { throw new ServiceException("已发药的处方不能作废"); }\n        if ("1".equals(rx.getStatus()))\n        {\n            chargeMapper.refundBySource(rxId, "1");\n        }\n        else\n        {\n            chargeMapper.cancelBySource(rxId, "1");\n        }\n        Prescription upd = new Prescription();\n        upd.setRxId(rxId);\n        upd.setStatus("3");\n        return prescriptionMapper.updatePrescription(upd);\n    }`,
    api: `// 开具处方(含明细)\nexport function createPrescription(data) {\n  return request({ url: '/his/prescription/create', method: 'post', data: data })\n}\n\n// 发药\nexport function dispenseRx(rxId) {\n  return request({ url: '/his/prescription/dispense/' + rxId, method: 'put' })\n}\n\n// 作废处方\nexport function cancelRx(rxId) {\n  return request({ url: '/his/prescription/cancel/' + rxId, method: 'put' })\n}\n\n// 待发药处方(已收费)\nexport function listPaidRx(query) {\n  return request({ url: '/his/prescription/paid', method: 'get', params: query })\n}\n\n// 处方明细\nexport function listRxItems(rxId) {\n  return request({ url: '/his/prescription/items/' + rxId, method: 'get' })\n}`,
    controller: `    /** 开具处方(含明细) */\n    @PreAuthorize("@ss.hasPermi('his:prescription:add') or @ss.hasPermi('his:workstation:rx')")\n    @Log(title = "处方", businessType = BusinessType.INSERT)\n    @PostMapping("/create")\n    public AjaxResult create(@RequestBody Prescription prescription)\n    {\n        Long rxId = prescriptionService.createPrescription(prescription);\n        return success(prescriptionService.selectPrescriptionByRxId(rxId));\n    }\n\n    /** 待发药处方(已收费) */\n    @GetMapping("/paid")\n    public TableDataInfo paid(Prescription prescription)\n    {\n        prescription.setStatus("1");\n        List<Prescription> list = prescriptionService.selectPrescriptionList(prescription);\n        return getDataTable(list);\n    }\n\n    /** 发药 */\n    @PreAuthorize("@ss.hasPermi('his:dispense:dispense')")\n    @Log(title = "处方发药", businessType = BusinessType.UPDATE)\n    @PutMapping("/dispense/{rxId}")\n    public AjaxResult dispense(@PathVariable("rxId") Long rxId)\n    {\n        return toAjax(prescriptionService.dispense(rxId));\n    }\n\n    /** 作废处方 */\n    @PreAuthorize("@ss.hasPermi('his:prescription:cancel')")\n    @Log(title = "处方作废", businessType = BusinessType.UPDATE)\n    @PutMapping("/cancel/{rxId}")\n    public AjaxResult cancel(@PathVariable("rxId") Long rxId)\n    {\n        return toAjax(prescriptionService.cancel(rxId));\n    }\n\n    /** 处方明细 */\n    @GetMapping("/items/{rxId}")\n    public AjaxResult items(@PathVariable("rxId") Long rxId)\n    {\n        return success(prescriptionService.selectItemList(rxId));\n    }`,
    serviceExtra2: `    /** 处方明细 */\n    public List<PrescriptionItem> selectItemList(Long rxId);`,
    implExtra2: `    @Override\n    public List<PrescriptionItem> selectItemList(Long rxId)\n    {\n        return prescriptionItemMapper.selectByRxId(rxId);\n    }`,
  },

  /* ---------- 费用 Charge ---------- */
  Charge: {
    mapper: `    /** 按来源作废未收费用单 */\n    public int cancelBySource(@Param("sourceId") Long sourceId, @Param("sourceType") String sourceType);\n\n    /** 按来源退费已收费用单 */\n    public int refundBySource(@Param("sourceId") Long sourceId, @Param("sourceType") String sourceType);`,
    mapperImports: ['org.apache.ibatis.annotations.Param'],
    xml: `    <update id="cancelBySource">\n        update his_charge set charge_status = '2'\n        where source_id = #{sourceId} and source_type = #{sourceType} and charge_status = '0'\n    </update>\n\n    <update id="refundBySource">\n        update his_charge set charge_status = '2'\n        where source_id = #{sourceId} and source_type = #{sourceType} and charge_status = '1'\n    </update>`,
    service: `    /** 批量结算 */\n    public int settle(Long[] chargeIds, String payType);\n\n    /** 退费 */\n    public int refund(Long chargeId);`,
    implImports: ['com.ruoyi.his.mapper.PrescriptionMapper', 'com.ruoyi.his.mapper.RegistrationMapper',
      'com.ruoyi.common.exception.ServiceException', 'com.ruoyi.common.utils.SecurityUtils',
      'org.springframework.transaction.annotation.Transactional', 'java.util.Date'],
    implAutowired: ['PrescriptionMapper prescriptionMapper', 'RegistrationMapper registrationMapper'],
    impl: `    /** 批量结算：置已收费，并联动更新来源单据状态(挂号->已缴费 处方->已收费) */\n    @Override\n    @Transactional\n    public int settle(Long[] chargeIds, String payType)\n    {\n        int rows = 0;\n        for (Long chargeId : chargeIds)\n        {\n            Charge charge = chargeMapper.selectChargeByChargeId(chargeId);\n            if (charge == null || !"0".equals(charge.getChargeStatus())) { continue; }\n            Charge upd = new Charge();\n            upd.setChargeId(chargeId);\n            upd.setChargeStatus("1");\n            upd.setPayType(payType);\n            upd.setSettleTime(new Date());\n            upd.setOperator(SecurityUtils.getUsername());\n            rows += chargeMapper.updateCharge(upd);\n            if ("1".equals(charge.getSourceType()) && charge.getSourceId() != null)\n            {\n                prescriptionMapper.updateStatus(charge.getSourceId(), "1");\n            }\n            else if ("0".equals(charge.getSourceType()) && charge.getSourceId() != null)\n            {\n                registrationMapper.updatePayStatus(charge.getSourceId(), "1");\n            }\n        }\n        return rows;\n    }\n\n    /** 退费：置已退费，并回滚来源单据状态 */\n    @Override\n    @Transactional\n    public int refund(Long chargeId)\n    {\n        Charge charge = chargeMapper.selectChargeByChargeId(chargeId);\n        if (charge == null) { throw new ServiceException("费用单不存在"); }\n        if (!"1".equals(charge.getChargeStatus())) { throw new ServiceException("只有已收费单据才能退费"); }\n        if ("1".equals(charge.getSourceType()) && charge.getSourceId() != null)\n        {\n            prescriptionMapper.updateStatus(charge.getSourceId(), "0");\n        }\n        else if ("0".equals(charge.getSourceType()) && charge.getSourceId() != null)\n        {\n            registrationMapper.updatePayStatus(charge.getSourceId(), "0");\n        }\n        Charge upd = new Charge();\n        upd.setChargeId(chargeId);\n        upd.setChargeStatus("2");\n        upd.setSettleTime(new Date());\n        upd.setOperator(SecurityUtils.getUsername());\n        return chargeMapper.updateCharge(upd);\n    }`,
    api: `// 批量结算\nexport function settleCharge(chargeIds, payType) {\n  return request({ url: '/his/charge/settle', method: 'put', data: { chargeIds: chargeIds, payType: payType } })\n}\n\n// 退费\nexport function refundCharge(chargeId) {\n  return request({ url: '/his/charge/refund/' + chargeId, method: 'put' })\n}`,
    controller: `    /** 批量结算 */\n    @PreAuthorize("@ss.hasPermi('his:charge:settle') or @ss.hasPermi('his:discharge:settle')")\n    @Log(title = "收费结算", businessType = BusinessType.UPDATE)\n    @PutMapping("/settle")\n    public AjaxResult settle(@RequestBody Charge charge)\n    {\n        return toAjax(chargeService.settle(charge.getChargeIds(), charge.getPayType()));\n    }\n\n    /** 退费 */\n    @PreAuthorize("@ss.hasPermi('his:charge:refund')")\n    @Log(title = "退费", businessType = BusinessType.UPDATE)\n    @PutMapping("/refund/{chargeId}")\n    public AjaxResult refund(@PathVariable("chargeId") Long chargeId)\n    {\n        return toAjax(chargeService.refund(chargeId));\n    }`,
    domain: `    /** 批量结算IDS(结算接口用) */\n    private Long[] chargeIds;\n\n    public Long[] getChargeIds() { return chargeIds; }\n    public void setChargeIds(Long[] chargeIds) { this.chargeIds = chargeIds; }`,
  },

  /* ---------- 住院 Admission ---------- */
  Admission: {
    mapper: `    /** 查询患者在院记录 */\n    public Admission selectInHospitalByPatient(Long patientId);`,
    xml: `    <select id="selectInHospitalByPatient" parameterType="Long" resultMap="AdmissionResult">\n        <include refid="selectAdmissionVo"/>\n        where t.patient_id = #{patientId} and t.adm_status = '0' order by t.adm_id desc limit 1\n    </select>`,
    service: `    /** 住院登记(占用床位) */\n    public Long admit(Admission admission);\n\n    /** 出院(释放床位+生成床位费账单) */\n    public int discharge(Long admId);\n\n    /** 查询患者在院记录 */\n    public Admission selectInHospitalByPatient(Long patientId);`,
    implImports: ['com.ruoyi.his.domain.Bed', 'com.ruoyi.his.domain.Charge', 'com.ruoyi.his.mapper.BedMapper', 'com.ruoyi.his.mapper.ChargeMapper',
      'com.ruoyi.common.exception.ServiceException', 'com.ruoyi.common.utils.DateUtils',
      'org.springframework.transaction.annotation.Transactional', 'java.math.BigDecimal', 'java.util.Date'],
    implAutowired: ['BedMapper bedMapper', 'ChargeMapper chargeMapper'],
    impl: `    /** 住院登记：校验床位 -> 占用 -> 生成住院号 */\n    @Override\n    @Transactional\n    public Long admit(Admission admission)\n    {\n        if (selectInHospitalByPatient(admission.getPatientId()) != null)\n        {\n            throw new ServiceException("该患者已有在院记录，请先办理出院");\n        }\n        Bed bed = bedMapper.selectBedByBedId(admission.getBedId());\n        if (bed == null || !"0".equals(bed.getStatus()))\n        {\n            throw new ServiceException("床位不可用，请重新选择");\n        }\n        Bed updBed = new Bed();\n        updBed.setBedId(bed.getBedId());\n        updBed.setStatus("1");\n        bedMapper.updateBed(updBed);\n\n        admission.setAdmNo("ZY" + DateUtils.dateTimeNow("yyyyMMddHHmmss") + String.format("%03d", new java.util.Random().nextInt(1000)));\n        admission.setInDate(new Date());\n        admission.setAdmStatus("0");\n        admissionMapper.insertAdmission(admission);\n        return admission.getAdmId();\n    }\n\n    /** 出院：释放床位 + 按住院天数生成床位费账单 */\n    @Override\n    @Transactional\n    public int discharge(Long admId)\n    {\n        Admission adm = admissionMapper.selectAdmissionByAdmId(admId);\n        if (adm == null) { throw new ServiceException("住院记录不存在"); }\n        if (!"0".equals(adm.getAdmStatus())) { throw new ServiceException("该患者已出院"); }\n        Date now = new Date();\n        Admission upd = new Admission();\n        upd.setAdmId(admId);\n        upd.setAdmStatus("1");\n        upd.setOutDate(now);\n        int rows = admissionMapper.updateAdmission(upd);\n        if (adm.getBedId() != null)\n        {\n            Bed updBed = new Bed();\n            updBed.setBedId(adm.getBedId());\n            updBed.setStatus("0");\n            bedMapper.updateBed(updBed);\n            Bed bed = bedMapper.selectBedByBedId(adm.getBedId());\n            long days = Math.max(1, (now.getTime() - adm.getInDate().getTime()) / (1000 * 3600 * 24) + 1);\n            if (bed != null && bed.getPricePerDay() != null)\n            {\n                Charge charge = new Charge();\n                charge.setChargeNo("ZYF" + adm.getAdmNo());\n                charge.setPatientId(adm.getPatientId());\n                charge.setSourceType("4");\n                charge.setSourceId(admId);\n                charge.setItemName("床位费(" + bed.getBedNo() + "床 x" + days + "天)");\n                charge.setPrice(bed.getPricePerDay());\n                charge.setQuantity((int) days);\n                charge.setAmount(bed.getPricePerDay().multiply(new BigDecimal(days)));\n                charge.setChargeStatus("0");\n                chargeMapper.insertCharge(charge);\n            }\n        }\n        return rows;\n    }\n\n    @Override\n    public Admission selectInHospitalByPatient(Long patientId)\n    {\n        return admissionMapper.selectInHospitalByPatient(patientId);\n    }`,
    api: `// 住院登记(占用床位)\nexport function admit(data) {\n  return request({ url: '/his/admission/admit', method: 'post', data: data })\n}\n\n// 出院(释放床位+生成床位费)\nexport function discharge(admId) {\n  return request({ url: '/his/admission/discharge/' + admId, method: 'put' })\n}\n\n// 查询患者在院记录\nexport function getInHospital(patientId) {\n  return request({ url: '/his/admission/inHospital/' + patientId, method: 'get' })\n}`,
    controller: `    /** 住院登记(占用床位) */\n    @PreAuthorize("@ss.hasPermi('his:admission:add') or @ss.hasPermi('his:bed:edit')")\n    @Log(title = "住院登记", businessType = BusinessType.INSERT)\n    @PostMapping("/admit")\n    public AjaxResult admit(@RequestBody Admission admission)\n    {\n        Long admId = admissionService.admit(admission);\n        return success(admissionService.selectAdmissionByAdmId(admId));\n    }\n\n    /** 出院(释放床位+生成床位费) */\n    @PreAuthorize("@ss.hasPermi('his:discharge:settle') or @ss.hasPermi('his:admission:edit')")\n    @Log(title = "出院结算", businessType = BusinessType.UPDATE)\n    @PutMapping("/discharge/{admId}")\n    public AjaxResult discharge(@PathVariable("admId") Long admId)\n    {\n        return toAjax(admissionService.discharge(admId));\n    }\n\n    /** 查询患者在院记录 */\n    @GetMapping("/inHospital/{patientId}")\n    public AjaxResult inHospital(@PathVariable("patientId") Long patientId)\n    {\n        return success(admissionService.selectInHospitalByPatient(patientId));\n    }`,
  },

  /* ---------- 医嘱 MedicalOrder ---------- */
  MedicalOrder: {
    mapper: `    /** 修改医嘱状态 */\n    public int updateStatus(@Param("orderId") Long orderId, @Param("status") String status, @Param("execBy") String execBy);`,
    mapperImports: ['org.apache.ibatis.annotations.Param'],
    xml: `    <update id="updateStatus">\n        update his_medical_order\n        <set>\n            order_status = #{status},\n            <if test="execBy != null">exec_by = #{execBy}, exec_time = now(),</if>\n            <if test="status == '2'">end_time = now(),</if>\n        </set>\n        where order_id = #{orderId}\n    </update>`,
    service: `    /** 执行医嘱(关联药品自动生成费用) */\n    public int execute(Long orderId);\n\n    /** 停止医嘱 */\n    public int stop(Long orderId);`,
    implImports: ['com.ruoyi.his.domain.Drug', 'com.ruoyi.his.domain.Charge', 'com.ruoyi.his.mapper.DrugMapper', 'com.ruoyi.his.mapper.ChargeMapper',
      'com.ruoyi.common.exception.ServiceException', 'com.ruoyi.common.utils.SecurityUtils', 'com.ruoyi.common.utils.DateUtils',
      'org.springframework.transaction.annotation.Transactional', 'java.math.BigDecimal'],
    implAutowired: ['DrugMapper drugMapper', 'ChargeMapper chargeMapper'],
    impl: `    /** 执行医嘱：置已执行，关联药品自动计费 */\n    @Override\n    @Transactional\n    public int execute(Long orderId)\n    {\n        MedicalOrder order = medicalOrderMapper.selectMedicalOrderByOrderId(orderId);\n        if (order == null) { throw new ServiceException("医嘱不存在"); }\n        if (!"0".equals(order.getOrderStatus())) { throw new ServiceException("医嘱已执行或已停止"); }\n        int rows = medicalOrderMapper.updateStatus(orderId, "1", SecurityUtils.getUsername());\n        if (order.getDrugId() != null)\n        {\n            Drug drug = drugMapper.selectDrugByDrugId(order.getDrugId());\n            if (drug != null)\n            {\n                Charge charge = new Charge();\n                charge.setChargeNo("YZ" + DateUtils.dateTimeNow("yyyyMMddHHmmss") + String.format("%03d", new java.util.Random().nextInt(1000)));\n                charge.setPatientId(order.getPatientId());\n                charge.setSourceType("4");\n                charge.setSourceId(orderId);\n                charge.setItemName("用药-" + drug.getDrugName());\n                charge.setPrice(drug.getPrice());\n                charge.setQuantity(1);\n                charge.setAmount(drug.getPrice());\n                charge.setChargeStatus("0");\n                chargeMapper.insertCharge(charge);\n            }\n        }\n        return rows;\n    }\n\n    /** 停止医嘱 */\n    @Override\n    public int stop(Long orderId)\n    {\n        MedicalOrder order = medicalOrderMapper.selectMedicalOrderByOrderId(orderId);\n        if (order == null) { throw new ServiceException("医嘱不存在"); }\n        if ("2".equals(order.getOrderStatus())) { throw new ServiceException("医嘱已停止"); }\n        return medicalOrderMapper.updateStatus(orderId, "2", null);\n    }`,
    api: `// 执行医嘱\nexport function executeOrder(orderId) {\n  return request({ url: '/his/order/execute/' + orderId, method: 'put' })\n}\n\n// 停止医嘱\nexport function stopOrder(orderId) {\n  return request({ url: '/his/order/stop/' + orderId, method: 'put' })\n}`,
    controller: `    /** 执行医嘱 */\n    @PreAuthorize("@ss.hasPermi('his:order:exec')")\n    @Log(title = "医嘱执行", businessType = BusinessType.UPDATE)\n    @PutMapping("/execute/{orderId}")\n    public AjaxResult execute(@PathVariable("orderId") Long orderId)\n    {\n        return toAjax(medicalOrderService.execute(orderId));\n    }\n\n    /** 停止医嘱 */\n    @PreAuthorize("@ss.hasPermi('his:order:stop')")\n    @Log(title = "医嘱停止", businessType = BusinessType.UPDATE)\n    @PutMapping("/stop/{orderId}")\n    public AjaxResult stop(@PathVariable("orderId") Long orderId)\n    {\n        return toAjax(medicalOrderService.stop(orderId));\n    }`,
  },

  /* ---------- 检验 LabTest ---------- */
  LabTest: {
    domain: `    /** 检验结果明细 */\n    private List<LabResult> resultList;\n\n    /** 检验费(申请时联动生成账单用，非持久) */\n    private java.math.BigDecimal fee;\n\n    public List<LabResult> getResultList() { return resultList; }\n    public void setResultList(List<LabResult> resultList) { this.resultList = resultList; }\n    public java.math.BigDecimal getFee() { return fee; }\n    public void setFee(java.math.BigDecimal fee) { this.fee = fee; }`,
    domainImports: ['java.util.List'],
    insertExtra: `        // 申请检验联动生成待缴检验费账单\n        if (${'labTest'}.getFee() != null)\n        {\n            Charge charge = new Charge();\n            charge.setChargeNo("JYF" + ${'labTest'}.getTestNo());\n            charge.setPatientId(${'labTest'}.getPatientId());\n            charge.setSourceType("2");\n            charge.setSourceId(${'labTest'}.getTestId());\n            charge.setItemName("检验费-" + ${'labTest'}.getTestItem());\n            charge.setPrice(${'labTest'}.getFee());\n            charge.setQuantity(1);\n            charge.setAmount(${'labTest'}.getFee());\n            charge.setChargeStatus("0");\n            chargeMapper.insertCharge(charge);\n        }`,
    implAutowiredMerge: ['ChargeMapper chargeMapper'],
    implImportsMerge: ['com.ruoyi.his.domain.Charge', 'com.ruoyi.his.mapper.ChargeMapper'],
    mapper: `    /** 修改检验状态 */\n    public int updateStatus(@Param("testId") Long testId, @Param("status") String status);`,
    mapperImports: ['org.apache.ibatis.annotations.Param'],
    xml: `    <update id="updateStatus">\n        update his_lab_test\n        <set>\n            status = #{status},\n            <if test="status == '1'">sample_time = now(),</if>\n        </set>\n        where test_id = #{testId}\n    </update>`,
    service: `    /** 状态流转(1采样 2开始检验 4作废) */\n    public int flow(Long testId, String status);\n\n    /** 出报告(含结果明细) */\n    public int report(LabTest labTest);`,
    implImports: ['com.ruoyi.his.domain.LabResult', 'com.ruoyi.his.mapper.LabResultMapper',
      'com.ruoyi.common.exception.ServiceException', 'com.ruoyi.common.utils.SecurityUtils',
      'org.springframework.transaction.annotation.Transactional', 'java.util.List', 'java.util.Date'],
    implAutowired: ['LabResultMapper labResultMapper'],
    impl: `    /** 状态流转: 0已申请 -> 1已采样 -> 2检验中；4作废(仅申请态) */\n    @Override\n    public int flow(Long testId, String status)\n    {\n        LabTest test = labTestMapper.selectLabTestByTestId(testId);\n        if (test == null) { throw new ServiceException("检验单不存在"); }\n        if ("1".equals(status) && !"0".equals(test.getStatus())) { throw new ServiceException("只有已申请才能采样"); }\n        if ("2".equals(status) && !"1".equals(test.getStatus())) { throw new ServiceException("只有已采样才能开始检验"); }\n        if ("4".equals(status) && !"0".equals(test.getStatus())) { throw new ServiceException("只有已申请才能作废"); }\n        return labTestMapper.updateStatus(testId, status);\n    }\n\n    /** 出报告：保存明细结果 + 结论，状态置已出报告 */\n    @Override\n    @Transactional\n    public int report(LabTest labTest)\n    {\n        LabTest test = labTestMapper.selectLabTestByTestId(labTest.getTestId());\n        if (test == null) { throw new ServiceException("检验单不存在"); }\n        if (!"2".equals(test.getStatus())) { throw new ServiceException("检验中状态才能出报告"); }\n        labResultMapper.deleteByTestId(labTest.getTestId());\n        if (labTest.getResultList() != null)\n        {\n            for (LabResult item : labTest.getResultList())\n            {\n                item.setTestId(labTest.getTestId());\n                labResultMapper.insertLabResult(item);\n            }\n        }\n        LabTest upd = new LabTest();\n        upd.setTestId(labTest.getTestId());\n        upd.setStatus("3");\n        upd.setResultSummary(labTest.getResultSummary());\n        upd.setReportTime(new Date());\n        upd.setReportBy(SecurityUtils.getUsername());\n        return labTestMapper.updateLabTest(upd);\n    }`,
    api: `// 检验状态流转 1采样 2开始检验 4作废\nexport function labFlow(testId, status) {\n  return request({ url: '/his/lab/flow/' + testId + '/' + status, method: 'put' })\n}\n\n// 出报告(含结果明细)\nexport function reportLab(data) {\n  return request({ url: '/his/lab/report', method: 'put', data: data })\n}\n\n// 检验结果明细\nexport function listLabResult(testId) {\n  return request({ url: '/his/lab/results/' + testId, method: 'get' })\n}`,
    controller: `    /** 检验状态流转 */\n    @PreAuthorize("@ss.hasPermi('his:lab:flow')")\n    @Log(title = "检验流程", businessType = BusinessType.UPDATE)\n    @PutMapping("/flow/{testId}/{status}")\n    public AjaxResult flow(@PathVariable("testId") Long testId, @PathVariable("status") String status)\n    {\n        return toAjax(labTestService.flow(testId, status));\n    }\n\n    /** 出报告(含结果明细) */\n    @PreAuthorize("@ss.hasPermi('his:lab:flow')")\n    @Log(title = "检验报告", businessType = BusinessType.UPDATE)\n    @PutMapping("/report")\n    public AjaxResult report(@RequestBody LabTest labTest)\n    {\n        return toAjax(labTestService.report(labTest));\n    }\n\n    /** 检验结果明细 */\n    @GetMapping("/results/{testId}")\n    public AjaxResult results(@PathVariable("testId") Long testId)\n    {\n        return success(labTestService.selectResultList(testId));\n    }`,
    serviceExtra2: `    /** 检验结果明细 */\n    public List<LabResult> selectResultList(Long testId);`,
    implExtra2: `    @Override\n    public List<LabResult> selectResultList(Long testId)\n    {\n        return labResultMapper.selectByTestId(testId);\n    }`,
  },

  /* ---------- 检查 Exam ---------- */
  Exam: {
    domain: `    /** 检查费(申请时联动生成账单用，非持久) */\n    private java.math.BigDecimal fee;\n\n    public java.math.BigDecimal getFee() { return fee; }\n    public void setFee(java.math.BigDecimal fee) { this.fee = fee; }`,
    insertExtra: `        // 申请检查联动生成待缴检查费账单\n        if (${'exam'}.getFee() != null)\n        {\n            Charge charge = new Charge();\n            charge.setChargeNo("JCF" + ${'exam'}.getExamNo());\n            charge.setPatientId(${'exam'}.getPatientId());\n            charge.setSourceType("3");\n            charge.setSourceId(${'exam'}.getExamId());\n            charge.setItemName("检查费-" + ${'exam'}.getBodyPart());\n            charge.setPrice(${'exam'}.getFee());\n            charge.setQuantity(1);\n            charge.setAmount(${'exam'}.getFee());\n            charge.setChargeStatus("0");\n            chargeMapper.insertCharge(charge);\n        }`,
    implAutowiredMerge: ['ChargeMapper chargeMapper'],
    implImportsMerge: ['com.ruoyi.his.domain.Charge', 'com.ruoyi.his.mapper.ChargeMapper'],
    mapper: `    /** 修改检查状态 */\n    public int updateStatus(@Param("examId") Long examId, @Param("status") String status);`,
    mapperImports: ['org.apache.ibatis.annotations.Param'],
    xml: `    <update id="updateStatus">\n        update his_exam set status = #{status} where exam_id = #{examId}\n    </update>`,
    service: `    /** 状态流转(1已检查 3取消) */\n    public int flow(Long examId, String status);\n\n    /** 出报告 */\n    public int report(Exam exam);`,
    implImports: ['com.ruoyi.common.exception.ServiceException', 'com.ruoyi.common.utils.SecurityUtils', 'java.util.Date'],
    impl: `    /** 状态流转: 0已申请 -> 1已检查；3取消(仅申请态) */\n    @Override\n    public int flow(Long examId, String status)\n    {\n        Exam exam = examMapper.selectExamByExamId(examId);\n        if (exam == null) { throw new ServiceException("检查单不存在"); }\n        if ("1".equals(status) && !"0".equals(exam.getStatus())) { throw new ServiceException("只有已申请才能登记检查"); }\n        if ("3".equals(status) && !"0".equals(exam.getStatus())) { throw new ServiceException("只有已申请才能取消"); }\n        return examMapper.updateStatus(examId, status);\n    }\n\n    /** 出报告 */\n    @Override\n    public int report(Exam exam)\n    {\n        Exam old = examMapper.selectExamByExamId(exam.getExamId());\n        if (old == null) { throw new ServiceException("检查单不存在"); }\n        if (!"1".equals(old.getStatus())) { throw new ServiceException("已检查状态才能出报告"); }\n        Exam upd = new Exam();\n        upd.setExamId(exam.getExamId());\n        upd.setStatus("2");\n        upd.setFinding(exam.getFinding());\n        upd.setConclusion(exam.getConclusion());\n        upd.setReportTime(new Date());\n        upd.setReportBy(SecurityUtils.getUsername());\n        return examMapper.updateExam(upd);\n    }`,
    api: `// 检查状态流转 1已检查 3取消\nexport function examFlow(examId, status) {\n  return request({ url: '/his/exam/flow/' + examId + '/' + status, method: 'put' })\n}\n\n// 出报告\nexport function reportExam(data) {\n  return request({ url: '/his/exam/report', method: 'put', data: data })\n}`,
    controller: `    /** 检查状态流转 */\n    @PreAuthorize("@ss.hasPermi('his:exam:flow')")\n    @Log(title = "检查流程", businessType = BusinessType.UPDATE)\n    @PutMapping("/flow/{examId}/{status}")\n    public AjaxResult flow(@PathVariable("examId") Long examId, @PathVariable("status") String status)\n    {\n        return toAjax(examService.flow(examId, status));\n    }\n\n    /** 出报告 */\n    @PreAuthorize("@ss.hasPermi('his:exam:flow')")\n    @Log(title = "检查报告", businessType = BusinessType.UPDATE)\n    @PutMapping("/report")\n    public AjaxResult report(@RequestBody Exam exam)\n    {\n        return toAjax(examService.report(exam));\n    }`,
  },

  /* ---------- 采购 Purchase ---------- */
  Purchase: {
    domain: `    /** 采购明细 */\n    private List<PurchaseItem> itemList;\n\n    public List<PurchaseItem> getItemList() { return itemList; }\n    public void setItemList(List<PurchaseItem> itemList) { this.itemList = itemList; }`,
    domainImports: ['java.util.List'],
    service: `    /** 新建采购单(含明细) */\n    public Long createPurchase(Purchase purchase);\n\n    /** 入库审核(增加库存+写入库记录) */\n    public int inbound(Long purchaseId);`,
    implImports: ['com.ruoyi.his.domain.PurchaseItem', 'com.ruoyi.his.domain.Drug', 'com.ruoyi.his.domain.StockRecord',
      'com.ruoyi.his.mapper.PurchaseItemMapper', 'com.ruoyi.his.mapper.DrugMapper', 'com.ruoyi.his.mapper.StockRecordMapper',
      'com.ruoyi.common.exception.ServiceException', 'com.ruoyi.common.utils.DateUtils', 'com.ruoyi.common.utils.SecurityUtils',
      'org.springframework.transaction.annotation.Transactional', 'java.math.BigDecimal', 'java.util.Date'],
    implAutowired: ['PurchaseItemMapper purchaseItemMapper', 'DrugMapper drugMapper', 'StockRecordMapper stockRecordMapper'],
    impl: `    /** 新建采购单(含明细，算总额) */\n    @Override\n    @Transactional\n    public Long createPurchase(Purchase purchase)\n    {\n        if (purchase.getItemList() == null || purchase.getItemList().isEmpty())\n        {\n            throw new ServiceException("采购明细不能为空");\n        }\n        BigDecimal total = BigDecimal.ZERO;\n        for (PurchaseItem item : purchase.getItemList())\n        {\n            if (item.getDrugId() == null || item.getQuantity() == null || item.getQuantity() <= 0)\n            {\n                throw new ServiceException("明细中药品/数量不完整");\n            }\n            item.setAmount(item.getPrice().multiply(new BigDecimal(item.getQuantity())));\n            total = total.add(item.getAmount());\n        }\n        purchase.setTotalAmount(total);\n        purchase.setStatus("0");\n        purchase.setPurchaseNo("CG" + DateUtils.dateTimeNow("yyyyMMddHHmmss") + String.format("%03d", new java.util.Random().nextInt(1000)));\n        purchaseMapper.insertPurchase(purchase);\n        for (PurchaseItem item : purchase.getItemList())\n        {\n            item.setPurchaseId(purchase.getPurchaseId());\n            purchaseItemMapper.insertPurchaseItem(item);\n        }\n        return purchase.getPurchaseId();\n    }\n\n    /** 入库审核：逐行加库存 + 写入库记录 */\n    @Override\n    @Transactional\n    public int inbound(Long purchaseId)\n    {\n        Purchase purchase = purchaseMapper.selectPurchaseByPurchaseId(purchaseId);\n        if (purchase == null) { throw new ServiceException("采购单不存在"); }\n        if (!"0".equals(purchase.getStatus())) { throw new ServiceException("采购单已入库或已取消"); }\n        List<PurchaseItem> items = purchaseItemMapper.selectByPurchaseId(purchaseId);\n        for (PurchaseItem item : items)\n        {\n            drugMapper.addStock(item.getDrugId(), item.getQuantity());\n            Drug drug = drugMapper.selectDrugByDrugId(item.getDrugId());\n            StockRecord rec = new StockRecord();\n            rec.setRecordNo("RK" + DateUtils.dateTimeNow("yyyyMMddHHmmss") + String.format("%03d", new java.util.Random().nextInt(1000)));\n            rec.setRecordType("0");\n            rec.setDrugId(item.getDrugId());\n            rec.setQuantity(item.getQuantity());\n            rec.setBeforeStock(drug.getStock() - item.getQuantity());\n            rec.setAfterStock(drug.getStock());\n            rec.setSourceId(purchaseId);\n            rec.setOperator(SecurityUtils.getUsername());\n            stockRecordMapper.insertStockRecord(rec);\n        }\n        Purchase upd = new Purchase();\n        upd.setPurchaseId(purchaseId);\n        upd.setStatus("1");\n        upd.setInTime(new Date());\n        return purchaseMapper.updatePurchase(upd);\n    }`,
    api: `// 新建采购单(含明细)\nexport function createPurchase(data) {\n  return request({ url: '/his/purchase/create', method: 'post', data: data })\n}\n\n// 入库审核\nexport function inboundPurchase(purchaseId) {\n  return request({ url: '/his/purchase/inbound/' + purchaseId, method: 'put' })\n}\n\n// 采购明细\nexport function listPurchaseItem(purchaseId) {\n  return request({ url: '/his/purchase/items/' + purchaseId, method: 'get' })\n}`,
    controller: `    /** 新建采购单(含明细) */\n    @PreAuthorize("@ss.hasPermi('his:purchase:add')")\n    @Log(title = "采购单", businessType = BusinessType.INSERT)\n    @PostMapping("/create")\n    public AjaxResult create(@RequestBody Purchase purchase)\n    {\n        Long id = purchaseService.createPurchase(purchase);\n        return success(purchaseService.selectPurchaseByPurchaseId(id));\n    }\n\n    /** 入库审核 */\n    @PreAuthorize("@ss.hasPermi('his:purchase:inbound')")\n    @Log(title = "采购入库", businessType = BusinessType.UPDATE)\n    @PutMapping("/inbound/{purchaseId}")\n    public AjaxResult inbound(@PathVariable("purchaseId") Long purchaseId)\n    {\n        return toAjax(purchaseService.inbound(purchaseId));\n    }\n\n    /** 采购明细 */\n    @GetMapping("/items/{purchaseId}")\n    public AjaxResult items(@PathVariable("purchaseId") Long purchaseId)\n    {\n        return success(purchaseService.selectItemList(purchaseId));\n    }`,
    serviceExtra2: `    /** 采购明细 */\n    public List<PurchaseItem> selectItemList(Long purchaseId);`,
    implExtra2: `    @Override\n    public List<PurchaseItem> selectItemList(Long purchaseId)\n    {\n        return purchaseItemMapper.selectByPurchaseId(purchaseId);\n    }`,
    implExtraImports: ['java.util.List'],
  },

  /* ---------- 药品 Drug ---------- */
  Drug: {
    mapper: `    /** 扣减库存(库存>=数量才成功) */\n    public int deductStock(@Param("drugId") Long drugId, @Param("qty") Integer qty);\n\n    /** 增加库存 */\n    public int addStock(@Param("drugId") Long drugId, @Param("qty") Integer qty);\n\n    /** 库存预警列表 */\n    public List<Drug> selectWarnList();`,
    mapperImports: ['org.apache.ibatis.annotations.Param'],
    xml: `    <update id="deductStock">\n        update his_drug set stock = stock - #{qty} where drug_id = #{drugId} and stock >= #{qty}\n    </update>\n\n    <update id="addStock">\n        update his_drug set stock = stock + #{qty} where drug_id = #{drugId}\n    </update>\n\n    <select id="selectWarnList" resultMap="DrugResult">\n        <include refid="selectDrugVo"/>\n        where t.stock &lt;= t.stock_warn order by t.stock\n    </select>`,
    service: `    /** 库存预警列表 */\n    public List<Drug> selectWarnList();`,
    impl: `    @Override\n    public List<Drug> selectWarnList()\n    {\n        return drugMapper.selectWarnList();\n    }`,
    api: `// 库存预警\nexport function listWarnDrug() {\n  return request({ url: '/his/drug/warnList', method: 'get' })\n}`,
    controller: `    /** 库存预警 */\n    @GetMapping("/warnList")\n    public TableDataInfo warnList()\n    {\n        List<Drug> list = drugService.selectWarnList();\n        return getDataTable(list);\n    }`,
  },

  /* ---------- 处方明细 PrescriptionItem ---------- */
  PrescriptionItem: {
    mapper: `    /** 按处方查明细 */\n    public List<PrescriptionItem> selectByRxId(Long rxId);\n\n    /** 按处方删明细 */\n    public int deleteByRxId(Long rxId);`,
    xml: `    <select id="selectByRxId" parameterType="Long" resultMap="PrescriptionItemResult">\n        <include refid="selectPrescriptionItemVo"/>\n        where t.rx_id = #{rxId} order by t.item_id\n    </select>\n\n    <delete id="deleteByRxId" parameterType="Long">\n        delete from his_prescription_item where rx_id = #{rxId}\n    </delete>`,
    service: `    /** 按处方查明细 */\n    public List<PrescriptionItem> selectByRxId(Long rxId);`,
    impl: `    @Override\n    public List<PrescriptionItem> selectByRxId(Long rxId)\n    {\n        return prescriptionItemMapper.selectByRxId(rxId);\n    }`,
  },

  /* ---------- 采购明细 PurchaseItem ---------- */
  PurchaseItem: {
    mapper: `    /** 按采购单查明细 */\n    public List<PurchaseItem> selectByPurchaseId(Long purchaseId);\n\n    /** 按采购单删明细 */\n    public int deleteByPurchaseId(Long purchaseId);`,
    xml: `    <select id="selectByPurchaseId" parameterType="Long" resultMap="PurchaseItemResult">\n        <include refid="selectPurchaseItemVo"/>\n        where t.purchase_id = #{purchaseId} order by t.item_id\n    </select>\n\n    <delete id="deleteByPurchaseId" parameterType="Long">\n        delete from his_purchase_item where purchase_id = #{purchaseId}\n    </delete>`,
    service: `    /** 按采购单查明细 */\n    public List<PurchaseItem> selectByPurchaseId(Long purchaseId);`,
    impl: `    @Override\n    public List<PurchaseItem> selectByPurchaseId(Long purchaseId)\n    {\n        return purchaseItemMapper.selectByPurchaseId(purchaseId);\n    }`,
  },

  /* ---------- 检验结果 LabResult ---------- */
  LabResult: {
    mapper: `    /** 按检验单查结果 */\n    public List<LabResult> selectByTestId(Long testId);\n\n    /** 按检验单删结果 */\n    public int deleteByTestId(Long testId);`,
    xml: `    <select id="selectByTestId" parameterType="Long" resultMap="LabResultResult">\n        <include refid="selectLabResultVo"/>\n        where t.test_id = #{testId} order by t.result_id\n    </select>\n\n    <delete id="deleteByTestId" parameterType="Long">\n        delete from his_lab_result where test_id = #{testId}\n    </delete>`,
    service: `    /** 按检验单查结果 */\n    public List<LabResult> selectByTestId(Long testId);`,
    impl: `    @Override\n    public List<LabResult> selectByTestId(Long testId)\n    {\n        return labResultMapper.selectByTestId(testId);\n    }`,
  },
};

/* 实体上已定义 extraXxx 的合并(如 bed/schedule/visit/nursing/surgery) */
for (const ent of ENTITIES) {
  const ex = EXTRAS[ent.entity] = EXTRAS[ent.entity] || {};
  if (ent.extraMapper) ex.mapper = (ex.mapper ? ex.mapper + '\n\n' : '') + ent.extraMapper;
  if (ent.extraService) ex.service = (ex.service ? ex.service + '\n\n' : '') + ent.extraService;
  if (ent.extraImpl) ex.impl = (ex.impl ? ex.impl + '\n\n' : '') + ent.extraImpl;
  if (ent.extraXml) ex.xml = (ex.xml ? ex.xml + '\n\n' : '') + ent.extraXml;
  if (ent.extraApi) ex.api = (ex.api ? ex.api + '\n\n' : '') + ent.extraApi;
  if (ent.extraController) ex.controller = (ex.controller ? ex.controller + '\n\n' : '') + ent.extraController;
  if (ent.extraImplImports) ex.implImports = (ex.implImports || []).concat(ent.extraImplImports);
  if (ent.extraImplAutowired) ex.implAutowired = (ex.implAutowired || []).concat(ent.extraImplAutowired);
}

/* ===================== 生成器 ===================== */

const persisted = e => e.fields.filter(x => x.persist !== false);
const joinedFields = e => e.fields.filter(x => x.persist === false);
const queryFields = e => e.fields.filter(x => x.query && x.persist !== false);
const colLabel = x => { if (x.joinCol && x.joinCol.includes(' as ')) return x.joinCol.split(' as ').pop().trim(); if (x.joinCol) return x.joinCol.split('.').pop(); return x.col; };

function javaImports(e, ex) {
  const im = new Set(['com.ruoyi.common.core.domain.BaseEntity']);
  if (e.fields.some(x => x.type === 'Date')) { im.add('java.util.Date'); im.add('com.fasterxml.jackson.annotation.JsonFormat'); }
  if (e.fields.some(x => x.type === 'BigDecimal')) im.add('java.math.BigDecimal');
  if (e.fields.some(x => x.excel)) im.add('com.ruoyi.common.annotation.Excel');
  (ex.domainImports || []).forEach(i => im.add(i));
  return [...im].sort().map(i => `import ${i};`).join('\n');
}

function genDomain(e) {
  const ex = EXTRAS[e.entity] || {};
  const lines = e.fields.map(x => {
    let out = `    /** ${x.cn} */\n`;
    if (x.excel) out += `    @Excel(name = "${x.cn}")\n`;
    if (x.type === 'Date') out += `    @JsonFormat(pattern = "${x.dateType === 'datetime' ? 'yyyy-MM-dd HH:mm:ss' : 'yyyy-MM-dd'}")\n`;
    out += `    private ${x.type} ${x.name};\n`;
    return out;
  });
  const gs = e.fields.map(x => `    public void set${cap(x.name)}(${x.type} ${x.name}) { this.${x.name} = ${x.name}; }\n    public ${x.type} get${cap(x.name)}() { return ${x.name}; }`);
  return `package com.ruoyi.his.domain;

${javaImports(e, ex)}

/**
 * ${e.cn}对象 ${e.table}
 */
public class ${e.entity} extends BaseEntity
{
    private static final long serialVersionUID = 1L;

${lines.join('\n')}
${ex.domain ? ex.domain + '\n' : ''}
${gs.join('\n\n')}
}
`;
}

function genMapperJava(e) {
  const ex = EXTRAS[e.entity] || {};
  const pk = e.pk, ent = e.entity, n = low(ent);
  const im = new Set(['java.util.List', `com.ruoyi.his.domain.${ent}`]);
  (ex.mapperImports || []).forEach(i => im.add(i));
  return `package com.ruoyi.his.mapper;

${[...im].sort().map(i => `import ${i};`).join('\n')}

/**
 * ${e.cn}Mapper接口
 */
public interface ${ent}Mapper
{
    public ${ent} select${ent}By${cap(pk)}(Long ${pk});

    public List<${ent}> select${ent}List(${ent} ${n});

    public int insert${ent}(${ent} ${n});

    public int update${ent}(${ent} ${n});

    public int delete${ent}By${cap(pk)}(Long ${pk});

    public int delete${ent}By${cap(pk)}s(Long[] ${pk}s);
${ex.mapper ? '\n' + ex.mapper + '\n' : ''}}
`;
}

function genService(e) {
  const ex = EXTRAS[e.entity] || {};
  const ent = e.entity, n = low(ent), pk = e.pk;
  const extraTypes = new Set();
  ((ex.service || '') + (ex.serviceExtra2 || '')).replace(/List<(\w+)>/g, (m, t) => { if (t !== ent) extraTypes.add(t); return m; });
  const im = ['java.util.List', `com.ruoyi.his.domain.${ent}`, ...[...extraTypes].map(t => `com.ruoyi.his.domain.${t}`)];
  return `package com.ruoyi.his.service;

${[...new Set(im)].sort().map(i => `import ${i};`).join('\n')}

/**
 * ${e.cn}Service接口
 */
public interface I${ent}Service
{
    public ${ent} select${ent}By${cap(pk)}(Long ${pk});

    public List<${ent}> select${ent}List(${ent} ${n});

    public int insert${ent}(${ent} ${n});

    public int update${ent}(${ent} ${n});

    public int delete${ent}By${cap(pk)}s(Long[] ${pk}s);

    public int delete${ent}By${cap(pk)}(Long ${pk});
${ex.service ? '\n' + ex.service + '\n' : ''}${ex.serviceExtra2 ? '\n' + ex.serviceExtra2 + '\n' : ''}}
`;
}

function genServiceImpl(e) {
  const ex = EXTRAS[e.entity] || {};
  const ent = e.entity, n = low(ent), pk = e.pk;
  const im = new Set(['java.util.List', 'org.springframework.beans.factory.annotation.Autowired', 'org.springframework.stereotype.Service',
    `com.ruoyi.his.domain.${ent}`, `com.ruoyi.his.mapper.${ent}Mapper`, `com.ruoyi.his.service.I${ent}Service`]);
  (ex.implImports || []).forEach(i => im.add(i));
  (ex.implExtraImports || []).forEach(i => im.add(i));
  (ex.implImportsMerge || []).forEach(i => im.add(i));
  // 实现里引用到的其它 domain 类型
  (ex.impl || '').replace(/List<(\w+)>/g, (m, t) => { if (t !== ent && !m.includes('.')) im.add(`com.ruoyi.his.domain.${t}`); return m; });
  const autowired = [...(ex.implAutowired || []), ...(ex.implAutowiredMerge || [])].map(x => `    @Autowired\n    private ${x};`).join('\n\n');
  return `package com.ruoyi.his.service.impl;

${[...im].sort().map(i => `import ${i};`).join('\n')}

/**
 * ${e.cn}Service业务层处理
 */
@Service
public class ${ent}ServiceImpl implements I${ent}Service
{
    @Autowired
    private ${ent}Mapper ${n}Mapper;
${autowired ? '\n' + autowired + '\n' : ''}
    @Override
    public ${ent} select${ent}By${cap(pk)}(Long ${pk})
    {
        return ${n}Mapper.select${ent}By${cap(pk)}(${pk});
    }

    @Override
    public List<${ent}> select${ent}List(${ent} ${n})
    {
        return ${n}Mapper.select${ent}List(${n});
    }

    @Override
    public int insert${ent}(${ent} ${n})
    {
        ${n}.setCreateTime(com.ruoyi.common.utils.DateUtils.getNowDate());
        int rows = ${n}Mapper.insert${ent}(${n});${ex.insertExtra ? '\n' + ex.insertExtra : ''}
        return rows;
    }

    @Override
    public int update${ent}(${ent} ${n})
    {
        ${n}.setUpdateTime(com.ruoyi.common.utils.DateUtils.getNowDate());
        return ${n}Mapper.update${ent}(${n});
    }

    @Override
    public int delete${ent}By${cap(pk)}s(Long[] ${pk}s)
    {
        return ${n}Mapper.delete${ent}By${cap(pk)}s(${pk}s);
    }

    @Override
    public int delete${ent}By${cap(pk)}(Long ${pk})
    {
        return ${n}Mapper.delete${ent}By${cap(pk)}(${pk});
    }
${ex.impl ? '\n' + ex.impl + '\n' : ''}${ex.implExtra2 ? '\n' + ex.implExtra2 + '\n' : ''}}
`;
}

function genXml(e) {
  const ex = EXTRAS[e.entity] || {};
  const ent = e.entity, pk = e.pk, pkCol = e.pkCol;
  const all = e.fields;
  const rm = all.map(x => `        <${x.pk ? 'id' : 'result'} property="${x.name}" column="${x.persist === false ? colLabel(x) : x.col}" />`).join('\n');
  const tCols = persisted(e).filter(x => !x.pk).map(x => `t.${x.col}`);
  const jCols = joinedFields(e).map(x => x.joinCol);
  const joinSql = (e.joins || []).map(j => `        left join ${j.table} ${j.alias} on ${j.on}`).join('\n');
  const selectVo = `        select t.${pkCol}${tCols.length ? ', ' + tCols.join(', ') : ''}${jCols.length ? ', ' + jCols.join(', ') : ''}\n        from ${e.table} t${joinSql ? '\n' + joinSql : ''}`;
  const wh = queryFields(e).map(x => {
    const cond = x.query === 'like' ? `AND t.${x.col} like concat('%', #{${x.name}}, '%')` : `AND t.${x.col} = #{${x.name}}`;
    return `            <if test="${x.name} != null${x.type === 'String' ? ` and ${x.name} != ''` : ''}"> ${cond}</if>`;
  });
  if (e.createTimeQuery) {
    wh.push(`            <if test="params.beginTime != null and params.beginTime != ''"> AND date_format(t.create_time,'%y%m%d') &gt;= #{params.beginTime}</if>`);
    wh.push(`            <if test="params.endTime != null and params.endTime != ''"> AND date_format(t.create_time,'%y%m%d') &lt;= #{params.endTime}</if>`);
  }
  const insCols = persisted(e).filter(x => !x.pk);
  const insC = insCols.map(x => `            <if test="${x.name} != null${x.type === 'String' ? ` and ${x.name} != ''` : ''}">${x.col},</if>`);
  const insV = insCols.map(x => `            <if test="${x.name} != null${x.type === 'String' ? ` and ${x.name} != ''` : ''}">#{${x.name}},</if>`);
  insC.push('            <if test="createBy != null">create_by,</if>', '            <if test="createTime != null">create_time,</if>', '            <if test="remark != null">remark,</if>');
  insV.push('            <if test="createBy != null">#{createBy},</if>', '            <if test="createTime != null">#{createTime},</if>', '            <if test="remark != null">#{remark},</if>');
  const upd = insCols.map(x => `            <if test="${x.name} != null${x.type === 'String' ? ` and ${x.name} != ''` : ''}">${x.col} = #{${x.name}},</if>`);
  upd.push('            <if test="updateBy != null">update_by = #{updateBy},</if>', '            <if test="updateTime != null">update_time = #{updateTime},</if>', '            <if test="remark != null">remark = #{remark},</if>');
  return `<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
"http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.ruoyi.his.mapper.${ent}Mapper">

    <resultMap type="${ent}" id="${ent}Result">
${rm}
        <result property="createBy"    column="create_by"    />
        <result property="createTime"  column="create_time"  />
        <result property="updateBy"    column="update_by"    />
        <result property="updateTime"  column="update_time"  />
        <result property="remark"      column="remark"       />
    </resultMap>

    <sql id="select${ent}Vo">
${selectVo}
    </sql>

    <select id="select${ent}List" parameterType="com.ruoyi.his.domain.${ent}" resultMap="${ent}Result">
        <include refid="select${ent}Vo"/>
${wh.length ? '        <where>\n' + wh.join('\n') + '\n        </where>' : ''}
        order by ${e.orderBy || 't.' + pkCol + ' desc'}
    </select>

    <select id="select${ent}By${cap(pk)}" parameterType="Long" resultMap="${ent}Result">
        <include refid="select${ent}Vo"/>
        where t.${pkCol} = #{${pk}}
    </select>

    <insert id="insert${ent}" parameterType="com.ruoyi.his.domain.${ent}" useGeneratedKeys="true" keyProperty="${pk}">
        insert into ${e.table}
        <trim prefix="(" suffix=")" suffixOverrides=",">
${insC.join('\n')}
        </trim>
        <trim prefix="values (" suffix=")" suffixOverrides=",">
${insV.join('\n')}
        </trim>
    </insert>

    <update id="update${ent}" parameterType="com.ruoyi.his.domain.${ent}">
        update ${e.table}
        <trim prefix="SET" suffixOverrides=",">
${upd.join('\n')}
        </trim>
        where ${pkCol} = #{${pk}}
    </update>

    <delete id="delete${ent}By${cap(pk)}" parameterType="Long">
        delete from ${e.table} where ${pkCol} = #{${pk}}
    </delete>

    <delete id="delete${ent}By${cap(pk)}s" parameterType="String">
        delete from ${e.table} where ${pkCol} in
        <foreach item="${pk}" collection="array" open="(" separator="," close=")">
            #{${pk}}
        </foreach>
    </delete>
${ex.xml ? '\n' + ex.xml + '\n' : ''}</mapper>
`;
}

function genController(e) {
  const ex = EXTRAS[e.entity] || {};
  const ent = e.entity, n = low(ent), pk = e.pk;
  return `package com.ruoyi.web.controller.his;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.his.domain.${ent};
import com.ruoyi.his.service.I${ent}Service;

/**
 * ${e.cn}Controller
 */
@RestController
@RequestMapping("/his/${e.path}")
public class ${ent}Controller extends BaseController
{
    @Autowired
    private I${ent}Service ${n}Service;

    /** 查询${e.cn}列表 */
    @PreAuthorize("@ss.hasPermi('his:${e.path}:list')")
    @GetMapping("/list")
    public TableDataInfo list(${ent} ${n})
    {
        startPage();
        List<${ent}> list = ${n}Service.select${ent}List(${n});
        return getDataTable(list);
    }

    /** 导出${e.cn}列表 */
    @PreAuthorize("@ss.hasPermi('his:${e.path}:export')")
    @Log(title = "${e.cn}", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ${ent} ${n})
    {
        List<${ent}> list = ${n}Service.select${ent}List(${n});
        ExcelUtil<${ent}> util = new ExcelUtil<${ent}>(${ent}.class);
        util.exportExcel(response, list, "${e.cn}数据");
    }

    /** 获取${e.cn}详细信息 */
    @PreAuthorize("@ss.hasPermi('his:${e.path}:query')")
    @GetMapping(value = "/{${pk}}")
    public AjaxResult getInfo(@PathVariable("${pk}") Long ${pk})
    {
        return success(${n}Service.select${ent}By${cap(pk)}(${pk}));
    }

    /** 新增${e.cn} */
    @PreAuthorize("@ss.hasPermi('his:${e.path}:add')")
    @Log(title = "${e.cn}", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ${ent} ${n})
    {
        return toAjax(${n}Service.insert${ent}(${n}));
    }

    /** 修改${e.cn} */
    @PreAuthorize("@ss.hasPermi('his:${e.path}:edit')")
    @Log(title = "${e.cn}", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ${ent} ${n})
    {
        return toAjax(${n}Service.update${ent}(${n}));
    }

    /** 删除${e.cn} */
    @PreAuthorize("@ss.hasPermi('his:${e.path}:remove')")
    @Log(title = "${e.cn}", businessType = BusinessType.DELETE)
    @DeleteMapping("/{${pk}s}")
    public AjaxResult remove(@PathVariable Long[] ${pk}s)
    {
        return toAjax(${n}Service.delete${ent}By${cap(pk)}s(${pk}s));
    }
${ex.controller ? '\n' + ex.controller + '\n' : ''}}
`;
}

function genApi(e) {
  const ex = EXTRAS[e.entity] || {};
  const ent = e.entity;
  return `import request from '@/utils/request'

// 查询${e.cn}列表
export function list${ent}(query) {
  return request({
    url: '/his/${e.path}/list',
    method: 'get',
    params: query
  })
}

// 查询${e.cn}详细
export function get${ent}(${e.pk}) {
  return request({
    url: '/his/${e.path}/' + ${e.pk},
    method: 'get'
  })
}

// 新增${e.cn}
export function add${ent}(data) {
  return request({
    url: '/his/${e.path}',
    method: 'post',
    data: data
  })
}

// 修改${e.cn}
export function update${ent}(data) {
  return request({
    url: '/his/${e.path}',
    method: 'put',
    data: data
  })
}

// 删除${e.cn}
export function del${ent}(${e.pk}) {
  return request({
    url: '/his/${e.path}/' + ${e.pk},
    method: 'delete'
  })
}
${ex.api ? '\n' + ex.api + '\n' : ''}`;
}

/* ---------- 简单CRUD Vue 页面 ---------- */
function genVue(e) {
  const ent = e.entity, n = low(ent), pk = e.pk;
  const dicts = [...new Set(e.fields.filter(x => x.dict).map(x => x.dict))];
  const fks = e.fields.filter(x => x.fk);
  const qf = queryFields(e);
  const listCols = e.fields.filter(x => x.list !== false && !x.pk);
  const formFields = e.fields.filter(x => x.form !== false && !x.pk && x.persist !== false);
  const view = e.view || e.path;
  const apiName = e.path;
  const nameCap = cap(view.replace(/-/g, ''));

  const searchItems = qf.map(x => {
    if (x.dict) return `         <el-form-item label="${x.cn}" prop="${x.name}">
            <el-select v-model="queryParams.${x.name}" placeholder="请选择${x.cn}" clearable style="width: 180px">
               <el-option v-for="dict in ${x.dict}" :key="dict.value" :label="dict.label" :value="dict.value" />
            </el-select>
         </el-form-item>`;
    if (x.fk) return `         <el-form-item label="${x.cn}" prop="${x.name}">
            <el-select v-model="queryParams.${x.name}" placeholder="请选择${x.cn}" clearable filterable style="width: 180px">
               <el-option v-for="item in ${x.fk.value.replace('Id', '')}Options" :key="item.${x.fk.value}" :label="item.${x.fk.label}" :value="item.${x.fk.value}" />
            </el-select>
         </el-form-item>`;
    if (x.type === 'Date') return `         <el-form-item label="${x.cn}" prop="${x.name}">
            <el-date-picker v-model="queryParams.${x.name}" type="${x.dateType === 'datetime' ? 'datetime' : 'date'}" value-format="${x.dateType === 'datetime' ? 'YYYY-MM-DD HH:mm:ss' : 'YYYY-MM-DD'}" placeholder="请选择${x.cn}" style="width: 180px" />
         </el-form-item>`;
    return `         <el-form-item label="${x.cn}" prop="${x.name}">
            <el-input v-model="queryParams.${x.name}" placeholder="请输入${x.cn}" clearable style="width: 180px" @keyup.enter="handleQuery" />
         </el-form-item>`;
  });

  const cols = listCols.map(x => {
    if (x.dict) return `         <el-table-column label="${x.cn}" align="center" prop="${x.name}">
            <template #default="scope">
               <dict-tag :options="${x.dict}" :value="scope.row.${x.name}" />
            </template>
         </el-table-column>`;
    if (x.type === 'Date') return `         <el-table-column label="${x.cn}" align="center" prop="${x.name}" width="160">
            <template #default="scope">
               <span>{{ parseTime(scope.row.${x.name}${x.dateType === 'datetime' ? '' : ", '{y}-{m}-{d}'"}) }}</span>
            </template>
         </el-table-column>`;
    return `         <el-table-column label="${x.cn}" align="center" prop="${x.name}" :show-overflow-tooltip="true" />`;
  });

  const formItems = formFields.map(x => {
    if (x.textarea) return `            <el-form-item label="${x.cn}" prop="${x.name}">
               <el-input v-model="form.${x.name}" type="textarea" :rows="3" placeholder="请输入${x.cn}" />
            </el-form-item>`;
    if (x.dict && x.dictCtl === 'radio') return `            <el-form-item label="${x.cn}" prop="${x.name}">
               <el-radio-group v-model="form.${x.name}">
                  <el-radio v-for="dict in ${x.dict}" :key="dict.value" :value="dict.value">{{ dict.label }}</el-radio>
               </el-radio-group>
            </el-form-item>`;
    if (x.dict) return `            <el-form-item label="${x.cn}" prop="${x.name}">
               <el-select v-model="form.${x.name}" placeholder="请选择${x.cn}" clearable>
                  <el-option v-for="dict in ${x.dict}" :key="dict.value" :label="dict.label" :value="dict.value" />
               </el-select>
            </el-form-item>`;
    if (x.fk) return `            <el-form-item label="${x.cn}" prop="${x.name}">
               <el-select v-model="form.${x.name}" placeholder="请选择${x.cn}" clearable filterable>
                  <el-option v-for="item in ${x.fk.value.replace('Id', '')}Options" :key="item.${x.fk.value}" :label="item.${x.fk.label}" :value="item.${x.fk.value}" />
               </el-select>
            </el-form-item>`;
    if (x.type === 'Date') return `            <el-form-item label="${x.cn}" prop="${x.name}">
               <el-date-picker v-model="form.${x.name}" type="${x.dateType === 'datetime' ? 'datetime' : 'date'}" value-format="${x.dateType === 'datetime' ? 'YYYY-MM-DD HH:mm:ss' : 'YYYY-MM-DD'}" placeholder="请选择${x.cn}" style="width: 100%" />
            </el-form-item>`;
    if (x.type === 'Integer') return `            <el-form-item label="${x.cn}" prop="${x.name}">
               <el-input-number v-model="form.${x.name}" :min="0" controls-position="right" style="width: 100%" />
            </el-form-item>`;
    if (x.type === 'BigDecimal') return `            <el-form-item label="${x.cn}" prop="${x.name}">
               <el-input-number v-model="form.${x.name}" :precision="2" :min="0" controls-position="right" style="width: 100%" />
            </el-form-item>`;
    if (x.type === 'Long' && x.name !== 'parentId') return `            <el-form-item label="${x.cn}" prop="${x.name}">
               <el-input-number v-model="form.${x.name}" :min="0" controls-position="right" style="width: 100%" />
            </el-form-item>`;
    return `            <el-form-item label="${x.cn}" prop="${x.name}">
               <el-input v-model="form.${x.name}" placeholder="请输入${x.cn}" />
            </el-form-item>`;
  });

  const fkImports = fks.map(x => `import { ${x.fk.api} } from "@/api/his/${x.fk.module}"`).join('\n');
  const fkOptions = fks.map(x => `const ${x.fk.value.replace('Id', '')}Options = ref([])`).join('\n');
  const fkLoads = fks.map(x => `function get${cap(x.fk.value.replace('Id', ''))}s() {
  ${x.fk.api}({ pageNum: 1, pageSize: 200 }).then(res => { ${x.fk.value.replace('Id', '')}Options.value = res.rows })
}`).join('\n');
  const fkCalls = fks.map(x => `get${cap(x.fk.value.replace('Id', ''))}s()`).join('\n');

  const dictUse = dicts.length ? `const { ${dicts.join(', ')} } = useDict(${dicts.map(d => `"${d}"`).join(', ')})` : '';
  const resetFields = formFields.map(x => `    ${x.name}: ${x.def !== undefined ? JSON.stringify(x.def) : 'undefined'},`).join('\n');
  const rules = formFields.filter(x => x.required).map(x => `    ${x.name}: [{ required: true, message: "${x.cn}不能为空", trigger: "${x.dict || x.fk || x.type === 'Date' ? 'change' : 'blur'}" }],`).join('\n');
  const qInit = qf.map(x => `    ${x.name}: undefined,`).join('\n');

  return `<template>
   <div class="app-container">
      <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
${searchItems.join('\n')}
         <el-form-item>
            <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
            <el-button icon="Refresh" @click="resetQuery">重置</el-button>
         </el-form-item>
      </el-form>

      <el-row :gutter="10" class="mb8">
         <el-col :span="1.5">
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['his:${apiName}:add']">新增</el-button>
         </el-col>
         <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['his:${apiName}:edit']">修改</el-button>
         </el-col>
         <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['his:${apiName}:remove']">删除</el-button>
         </el-col>
         <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['his:${apiName}:export']">导出</el-button>
         </el-col>
         <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <el-table v-loading="loading" :data="${n}List" @selection-change="handleSelectionChange">
         <el-table-column type="selection" width="55" align="center" />
         <el-table-column label="ID" align="center" prop="${pk}" width="80" />
${cols.join('\n')}
         <el-table-column label="创建时间" align="center" prop="createTime" width="160">
            <template #default="scope">
               <span>{{ parseTime(scope.row.createTime) }}</span>
            </template>
         </el-table-column>
         <el-table-column label="操作" width="150" align="center" class-name="small-padding fixed-width">
            <template #default="scope">
               <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['his:${apiName}:edit']">修改</el-button>
               <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['his:${apiName}:remove']">删除</el-button>
            </template>
         </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

      <!-- 添加或修改${e.cn}对话框 -->
      <el-dialog :title="title" v-model="open" width="640px" append-to-body>
         <el-form ref="${n}Ref" :model="form" :rules="rules" label-width="110px">
${formItems.join('\n')}
            <el-form-item label="备注" prop="remark">
               <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
            </el-form-item>
         </el-form>
         <template #footer>
            <div class="dialog-footer">
               <el-button type="primary" @click="submitForm">确 定</el-button>
               <el-button @click="cancel">取 消</el-button>
            </div>
         </template>
      </el-dialog>
   </div>
</template>

<script setup name="${nameCap}">
import { list${ent}, add${ent}, del${ent}, get${ent}, update${ent} } from "@/api/his/${apiName}"
${fkImports}
const { proxy } = getCurrentInstance()
${dictUse}

const ${n}List = ref([])
${fkOptions}
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
${qInit}
  },
  rules: {
${rules}
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询${e.cn}列表 */
function getList() {
  loading.value = true
  list${ent}(queryParams.value).then(response => {
    ${n}List.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

${fkLoads}

/** 取消按钮 */
function cancel() {
  open.value = false
  reset()
}

/** 表单重置 */
function reset() {
  form.value = {
    ${pk}: undefined,
${resetFields}
    remark: undefined
  }
  proxy.resetForm("${n}Ref")
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef")
  handleQuery()
}

/** 多选框选中数据 */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.${pk})
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加${e.cn}"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const id = row.${pk} || ids.value
  get${ent}(id).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改${e.cn}"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["${n}Ref"].validate(valid => {
    if (valid) {
      if (form.value.${pk} != undefined) {
        update${ent}(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        add${ent}(form.value).then(() => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

/** 删除按钮操作 */
function handleDelete(row) {
  const delIds = row.${pk} || ids.value
  proxy.$modal.confirm('是否确认删除编号为"' + delIds + '"的数据项？').then(function() {
    return del${ent}(delIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download("his/${apiName}/export", {
    ...queryParams.value
  }, "${apiName}_" + new Date().getTime() + ".xlsx")
}

${fkCalls}
getList()
</script>
`;
}

/* ===================== SQL 生成 ===================== */
function genSql() {
  let s = `-- ============================================================
-- 医院信息系统(HIS) 一键初始化脚本
-- 包含: 24张业务表 + 字典 + 菜单 + 演示数据
-- 执行: mysql -uroot -p ry-vue < sql/his.sql
-- ============================================================

`;
  // 建表
  for (const e of ENTITIES) {
    s += `-- ----------------------------\n-- ${e.cn}表 ${e.table}\n-- ----------------------------\nDROP TABLE IF EXISTS \`${e.table}\`;\nCREATE TABLE \`${e.table}\` (\n`;
    const cols = persisted(e).map(x => {
      const nn = x.required ? ' NOT NULL' : ' DEFAULT NULL';
      const extra = x.pk ? ' NOT NULL AUTO_INCREMENT' : nn;
      return `  \`${x.col}\` ${x.sql}${x.pk ? '' : ''}${extra} COMMENT '${x.cn}'`;
    });
    cols.push(`  \`create_by\` varchar(64) DEFAULT '' COMMENT '创建者'`);
    cols.push(`  \`create_time\` datetime DEFAULT NULL COMMENT '创建时间'`);
    cols.push(`  \`update_by\` varchar(64) DEFAULT '' COMMENT '更新者'`);
    cols.push(`  \`update_time\` datetime DEFAULT NULL COMMENT '更新时间'`);
    cols.push(`  \`remark\` varchar(500) DEFAULT NULL COMMENT '备注'`);
    s += cols.join(',\n');
    s += `,\n  PRIMARY KEY (\`${e.pkCol}\`)\n) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='${e.cn}表';\n\n`;
  }

  // 字典
  s += `-- ----------------------------\n-- HIS 字典(可重复执行: 先清理旧数据)\n-- ----------------------------\ndelete from sys_dict_data where dict_type like 'his_%';\ndelete from sys_dict_type where dict_type like 'his_%';\n\n`;
  let dictId = 200, code = 2000;
  for (const d of DICTS) {
    s += `insert into sys_dict_type(dict_id, dict_name, dict_type, status, create_by, create_time, remark) values (${dictId}, '${d.name}', '${d.type}', '0', 'admin', now(), '${d.name}');\n`;
    let sort = 1;
    for (const [label, value, cls] of d.data) {
      s += `insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values (${code}, ${sort}, '${label}', '${value}', '${d.type}', '', '${cls || 'default'}', '${sort === 1 ? 'Y' : 'N'}', '0', 'admin', now(), '');\n`;
      code++; sort++;
    }
    dictId++;
    s += '\n';
  }

  // 菜单
  s += `-- ----------------------------\n-- HIS 菜单(可重复执行: 先清理旧菜单)\n-- ----------------------------\ndelete from sys_menu where menu_id >= 3000 and menu_id < 40000;\n\n`;
  const BTNS = [{ n: '查询', p: 'query' }, { n: '新增', p: 'add' }, { n: '修改', p: 'edit' }, { n: '删除', p: 'remove' }, { n: '导出', p: 'export' }];
  for (const m of MENUS) {
    const comp = m.comp ? `'${m.comp}'` : 'null';
    const perms = m.perms ? `'${m.perms}'` : `''`;
    const qstr = m.type === 'C' ? `'component':'${m.comp}'` : '';
    s += `insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) values (${m.id}, '${m.name}', ${m.parent}, ${m.order}, '${m.path}', ${comp}, '', 1, 0, '${m.type}', '0', '0', ${perms}, '${m.icon}', 'admin', now(), '${m.name}');\n`;
    if (m.btns) {
      const btns = m.btns === 1 ? BTNS : m.btns;
      let bi = 1;
      for (const b of btns) {
        s += `insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) values (${m.id * 10 + bi}, '${b.n}', ${m.id}, ${bi}, '', null, '', 1, 0, 'F', '0', '0', '${m.perms.replace(/:list$/, '')}:${b.p}', '#', 'admin', now());\n`;
        bi++;
      }
    }
  }
  s += '\n';
  s += genSeed();
  return s;
}

/* ===================== 演示数据 ===================== */
const q = v => v === null || v === undefined ? 'null' : `'${String(v).replace(/'/g, "''")}'`;
const ins = (table, cols, rows) => rows.map(r => `insert into ${table}(${cols.join(',')}) values (${r.map(q).join(',')});`).join('\n') + '\n\n';
// 相对今天 N 天的日期表达式
const dexpr = d => d === 0 ? 'curdate()' : (d < 0 ? `date_sub(curdate(), interval ${-d} day)` : `date_add(curdate(), interval ${d} day)`);
const texpr = d => d === 0 ? 'now()' : (d < 0 ? `date_sub(now(), interval ${-d} day)` : `date_add(now(), interval ${d} day)`);
// 允许原始SQL表达式(不加引号)
const raw = v => ({ __raw: v });
function insRaw(table, cols, rows) {
  return rows.map(r => `insert into ${table}(${cols.join(',')}) values (${r.map(v => v && v.__raw ? v.__raw : q(v)).join(',')});`).join('\n') + '\n\n';
}

function genSeed() {
  let s = `-- ----------------------------\n-- HIS 演示数据\n-- ----------------------------\n`;

  s += ins('his_department', ['dept_id','parent_id','dept_name','dept_code','dept_type','location','phone','intro','order_num','status','create_by','create_time'], [
    [1,0,'内科','NK','0','门诊楼2F','0571-88010001','心血管、呼吸、消化等内科疾病诊治',1,'0','admin','2026-01-01 08:00:00'],
    [2,0,'外科','WK','0','门诊楼3F','0571-88010002','普外、骨科、神经外科等手术治疗',2,'0','admin','2026-01-01 08:00:00'],
    [3,0,'儿科','EK','0','门诊楼4F','0571-88010003','儿童常见病、多发病诊治',3,'0','admin','2026-01-01 08:00:00'],
    [4,0,'妇产科','FCK','0','门诊楼4F','0571-88010004','妇科、产科诊疗与围产保健',4,'0','admin','2026-01-01 08:00:00'],
    [5,0,'眼科','YK','0','门诊楼5F','0571-88010005','眼科疾病、视光检查',5,'0','admin','2026-01-01 08:00:00'],
    [6,0,'口腔科','KQK','0','门诊楼5F','0571-88010006','口腔疾病诊治、牙齿修复',6,'0','admin','2026-01-01 08:00:00'],
    [7,0,'急诊科','JZK','0','急诊楼1F','0571-88010007','24小时急诊急救',7,'0','admin','2026-01-01 08:00:00'],
    [8,0,'中医科','ZYK','0','门诊楼6F','0571-88010008','中医内科、针灸推拿',8,'0','admin','2026-01-01 08:00:00'],
    [9,0,'检验科','JYK','2','医技楼2F','0571-88010009','临床检验、生化免疫检测',9,'0','admin','2026-01-01 08:00:00'],
    [10,0,'放射科','FSK','2','医技楼1F','0571-88010010','CT、MRI、X光影像检查',10,'0','admin','2026-01-01 08:00:00'],
    [11,0,'药房','YF','3','门诊楼1F','0571-88010011','门诊/住院药品调配发放',11,'0','admin','2026-01-01 08:00:00'],
    [12,0,'住院部','ZYB','1','住院楼','0571-88010012','住院病区管理',12,'0','admin','2026-01-01 08:00:00'],
  ]);

  s += ins('his_doctor', ['doctor_id','doctor_name','dept_id','title','gender','phone','specialty','reg_fee','intro','status','create_by','create_time'], [
    [1,'王建国',1,'0','0','13901010001','冠心病、高血压诊治',50,'从医30年，心血管内科专家','0','admin','2026-01-02 08:00:00'],
    [2,'李秀英',1,'1','1','13901010002','呼吸系统疾病',30,'呼吸内科副主任医师','0','admin','2026-01-02 08:00:00'],
    [3,'张伟',1,'2','0','13901010003','消化内科、胃肠疾病',20,'消化内科主治医师','0','admin','2026-01-02 08:00:00'],
    [4,'刘志强',2,'0','0','13901010004','普外科手术',50,'外科主任医师，腹腔镜手术专家','0','admin','2026-01-02 08:00:00'],
    [5,'陈静',2,'2','1','13901010005','骨科、创伤外科',20,'骨科主治医师','0','admin','2026-01-02 08:00:00'],
    [6,'杨晓燕',3,'1','1','13901010006','小儿呼吸道、消化道疾病',30,'儿科副主任医师','0','admin','2026-01-02 08:00:00'],
    [7,'赵敏',3,'3','1','13901010007','新生儿护理、儿童保健',10,'儿科住院医师','0','admin','2026-01-02 08:00:00'],
    [8,'孙丽华',4,'1','1','13901010008','产科、高危妊娠管理',30,'妇产科副主任医师','0','admin','2026-01-02 08:00:00'],
    [9,'周涛',5,'2','0','13901010009','白内障、青光眼',20,'眼科主治医师','0','admin','2026-01-02 08:00:00'],
    [10,'吴国庆',6,'2','0','13901010010','口腔种植、正畸',20,'口腔科主治医师','0','admin','2026-01-02 08:00:00'],
    [11,'郑海涛',7,'2','0','13901010011','急诊急救、创伤处理',20,'急诊科主治医师','0','admin','2026-01-02 08:00:00'],
    [12,'冯雅琴',8,'1','1','13901010012','中医内科、针灸',30,'中医科副主任医师','0','admin','2026-01-02 08:00:00'],
    [13,'何军',2,'3','0','13901010013','外科常见疾病',10,'外科住院医师','0','admin','2026-01-02 08:00:00'],
    [14,'林黛玉',4,'3','1','13901010014','妇科炎症、计划生育',10,'妇产科住院医师','0','admin','2026-01-02 08:00:00'],
  ]);

  s += ins('his_patient', ['patient_id','patient_no','patient_name','gender','birth_date','age','id_card','phone','address','blood_type','insurance_type','allergy','status','create_by','create_time'], [
    [1,'MZ20260001','陈大明','0','1965-03-12',61,'330102196503120011','13600010001','杭州市西湖区文三路100号','A','1','青霉素过敏','0','admin','2026-02-01 09:00:00'],
    [2,'MZ20260002','李淑芬','1','1972-07-25',54,'330102197207250022','13600010002','杭州市拱墅区莫干山路200号','B','1','','0','admin','2026-02-01 09:10:00'],
    [3,'MZ20260003','王小宝','0','2019-11-05',6,'330102201911050033','13600010003','杭州市江干区凯旋路300号','O','2','海鲜过敏','0','admin','2026-02-02 10:00:00'],
    [4,'MZ20260004','张桂花','1','1958-01-30',68,'330102195801300044','13600010004','杭州市下城区朝晖路400号','AB','1','','0','admin','2026-02-02 10:20:00'],
    [5,'MZ20260005','刘建军','0','1985-09-18',41,'330102198509180055','13600010005','杭州市滨江区江南大道500号','A','3','','0','admin','2026-02-03 08:30:00'],
    [6,'MZ20260006','周淑华','1','1990-04-22',36,'330102199004220066','13600010006','杭州市余杭区文一西路600号','O','1','头孢过敏','0','admin','2026-02-03 09:00:00'],
    [7,'MZ20260007','吴国强','0','1978-12-08',47,'330102197812080077','13600010007','杭州市萧山区市心中路700号','B','1','','0','admin','2026-02-04 11:00:00'],
    [8,'MZ20260008','郑美玲','1','1995-06-15',31,'330102199506150088','13600010008','杭州市上城区中山中路800号','A','0','','0','admin','2026-02-04 14:00:00'],
    [9,'MZ20260009','孙志远','0','2000-02-14',26,'330102200002140099','13600010009','杭州市富阳区桂花西路900号','O','0','','0','admin','2026-02-05 08:00:00'],
    [10,'MZ20260010','林小妹','1','2018-08-30',8,'330102201808300100','13600010010','杭州市临安区钱王街1000号','A','2','','0','admin','2026-02-05 09:30:00'],
    [11,'MZ20260011','黄建军','0','1960-10-01',65,'330102196010010111','13600010011','杭州市西湖区转塘街道11号','B','1','磺胺类过敏','0','admin','2026-02-06 08:00:00'],
    [12,'MZ20260012','徐丽丽','1','1988-05-20',38,'330102198805200122','13600010012','杭州市拱墅区上塘路1200号','AB','1','','0','admin','2026-02-06 10:00:00'],
    [13,'MZ20260013','马文轩','0','2015-03-08',11,'330102201503080133','13600010013','杭州市江干区艮山西路1300号','O','2','','0','admin','2026-02-07 09:00:00'],
    [14,'MZ20260014','朱桂香','1','1955-11-11',70,'330102195511110144','13600010014','杭州市下城区环城北路1400号','A','1','','0','admin','2026-02-07 15:00:00'],
    [15,'MZ20260015','胡永强','0','1982-07-07',44,'330102198207070155','13600010015','杭州市滨江区浦沿街道1500号','B','3','','0','admin','2026-02-08 08:30:00'],
    [16,'MZ20260016','高秀兰','1','1993-09-25',32,'330102199309250166','13600010016','杭州市余杭区临平街道1600号','O','1','','0','admin','2026-02-08 16:00:00'],
  ]);

  s += ins('his_drug', ['drug_id','drug_code','drug_name','category','specification','unit','price','stock','stock_warn','manufacturer','approval_no','is_prescription','status','create_by','create_time'], [
    [1,'YP001','阿莫西林胶囊','0','0.25g*24粒','盒',12.50,300,50,'华北制药','国药准字H20003035','Y','0','admin','2026-01-05 08:00:00'],
    [2,'YP002','布洛芬缓释胶囊','2','0.3g*20粒','盒',15.80,260,50,'中美天津史克','国药准字H10900089','N','0','admin','2026-01-05 08:00:00'],
    [3,'YP003','连花清瘟胶囊','3','0.35g*24粒','盒',14.80,180,60,'以岭药业','国药准字Z20040063','N','0','admin','2026-01-05 08:00:00'],
    [4,'YP004','头孢克肟分散片','0','0.1g*12片','盒',22.00,150,50,'广州白云山','国药准字H20041663','Y','0','admin','2026-01-05 08:00:00'],
    [5,'YP005','二甲双胍片','1','0.5g*60片','瓶',18.50,220,50,'中美上海施贵宝','国药准字H20023370','Y','0','admin','2026-01-05 08:00:00'],
    [6,'YP006','硝苯地平控释片','1','30mg*7片','盒',28.00,90,40,'拜耳医药','国药准字J20180025','Y','0','admin','2026-01-05 08:00:00'],
    [7,'YP007','阿托伐他汀钙片','1','20mg*7片','盒',32.00,120,40,'辉瑞制药','国药准字H20051407','Y','0','admin','2026-01-05 08:00:00'],
    [8,'YP008','奥美拉唑肠溶胶囊','1','20mg*14粒','盒',16.50,200,50,'阿斯利康','国药准字H20030412','Y','0','admin','2026-01-05 08:00:00'],
    [9,'YP009','氯雷他定片','9','10mg*6片','盒',11.00,160,50,'拜耳医药','国药准字H20070030','N','0','admin','2026-01-05 08:00:00'],
    [10,'YP010','维生素C片','9','0.1g*100片','瓶',8.00,300,80,'东北制药','国药准字H21020713','N','0','admin','2026-01-05 08:00:00'],
    [11,'YP011','板蓝根颗粒','3','10g*20袋','盒',9.80,240,60,'白云山医药','国药准字Z44023485','N','0','admin','2026-01-05 08:00:00'],
    [12,'YP012','葡萄糖注射液','4','5% 250ml','瓶',5.50,500,100,'科伦药业','国药准字H51020634','Y','0','admin','2026-01-05 08:00:00'],
    [13,'YP013','氯化钠注射液','4','0.9% 250ml','瓶',4.80,600,100,'科伦药业','国药准字H51021156','Y','0','admin','2026-01-05 08:00:00'],
    [14,'YP014','碘伏消毒液','5','100ml','瓶',6.50,150,40,'利尔康','卫消字2010第0015号','N','0','admin','2026-01-05 08:00:00'],
    [15,'YP015','云南白药气雾剂','5','85g+30g','盒',35.00,80,30,'云南白药','国药准字Z53021107','N','0','admin','2026-01-05 08:00:00'],
    [16,'YP016','藿香正气水','3','10ml*10支','盒',12.00,45,50,'同仁堂','国药准字Z11020377','N','0','admin','2026-01-05 08:00:00'],
    [17,'YP017','地塞米松片','0','0.75mg*100片','瓶',15.00,35,40,'天津力生','国药准字H12020136','Y','0','admin','2026-01-05 08:00:00'],
    [18,'YP018','门冬胰岛素注射液','4','3ml:300单位','支',68.00,60,30,'诺和诺德','国药准字J20150066','Y','0','admin','2026-01-05 08:00:00'],
    [19,'YP019','孟鲁司特钠咀嚼片','1','5mg*14片','盒',25.50,110,40,'默沙东','国药准字J20130053','Y','0','admin','2026-01-05 08:00:00'],
    [20,'YP020','蒙脱石散','3','3g*10袋','盒',13.50,190,50,'博福-益普生','国药准字H20000690','N','0','admin','2026-01-05 08:00:00'],
    [21,'YP021','左氧氟沙星片','0','0.5g*7片','盒',19.80,130,50,'第一三共','国药准字H20040091','Y','0','admin','2026-01-05 08:00:00'],
    [22,'YP022','复方氨酚烷胺片','2','10片','盒',8.50,25,50,'感康药业','国药准字H22026193','N','0','admin','2026-01-05 08:00:00'],
    [23,'YP023','复方甘草片','3','100片','瓶',11.50,170,50,'太极集团','国药准字Z20093007','N','0','admin','2026-01-05 08:00:00'],
    [24,'YP024','注射用头孢曲松钠','4','1g/支','支',22.00,200,60,'罗氏制药','国药准字H10983037','Y','0','admin','2026-01-05 08:00:00'],
  ]);

  s += ins('his_supplier', ['supplier_id','supplier_name','contact','phone','address','status','create_by','create_time'], [
    [1,'国药控股股份有限公司','张经理','021-23050001','上海市黄浦区福州路221号','0','admin','2026-01-06 08:00:00'],
    [2,'华润医药商业集团','李经理','010-65199000','北京市东城区安定门内大街257号','0','admin','2026-01-06 08:00:00'],
    [3,'九州通医药集团','王经理','027-84683001','武汉市汉阳区龙阳大道特8号','0','admin','2026-01-06 08:00:00'],
    [4,'上海医药集团','陈经理','021-63730900','上海市黄浦区太仓路200号','0','admin','2026-01-06 08:00:00'],
    [5,'广州医药有限公司','刘经理','020-81803000','广州市荔湾区大同路103号','0','admin','2026-01-06 08:00:00'],
  ]);

  s += ins('his_fee_item', ['item_id','item_name','category','price','unit','status','create_by','create_time'], [
    [1,'血常规五分类','0',25.00,'次','0','admin','2026-01-07 08:00:00'],
    [2,'尿常规十一项','0',15.00,'次','0','admin','2026-01-07 08:00:00'],
    [3,'肝功能全套','0',85.00,'次','0','admin','2026-01-07 08:00:00'],
    [4,'肾功能三项','0',45.00,'次','0','admin','2026-01-07 08:00:00'],
    [5,'血糖检测','0',12.00,'次','0','admin','2026-01-07 08:00:00'],
    [6,'CT平扫(单部位)','1',280.00,'次','0','admin','2026-01-07 08:00:00'],
    [7,'MRI核磁共振','1',680.00,'次','0','admin','2026-01-07 08:00:00'],
    [8,'数字化X光摄影','1',80.00,'次','0','admin','2026-01-07 08:00:00'],
    [9,'彩色B超','1',120.00,'次','0','admin','2026-01-07 08:00:00'],
    [10,'十二导联心电图','1',40.00,'次','0','admin','2026-01-07 08:00:00'],
    [11,'普通门诊诊查费','5',10.00,'次','0','admin','2026-01-07 08:00:00'],
    [12,'静脉输液费','4',15.00,'次','0','admin','2026-01-07 08:00:00'],
    [13,'肌肉注射费','4',10.00,'次','0','admin','2026-01-07 08:00:00'],
    [14,'一级护理费','4',30.00,'天','0','admin','2026-01-07 08:00:00'],
    [15,'换药费','9',20.00,'次','0','admin','2026-01-07 08:00:00'],
  ]);

  s += ins('his_ward', ['ward_id','ward_name','dept_id','location','nurse_count','status','create_by','create_time'], [
    [1,'内科一病区',1,'住院楼3F',8,'0','admin','2026-01-08 08:00:00'],
    [2,'内科二病区',1,'住院楼4F',8,'0','admin','2026-01-08 08:00:00'],
    [3,'外科病区',2,'住院楼5F',10,'0','admin','2026-01-08 08:00:00'],
    [4,'儿科病区',3,'住院楼6F',6,'0','admin','2026-01-08 08:00:00'],
    [5,'重症ICU病区',2,'住院楼7F',12,'0','admin','2026-01-08 08:00:00'],
  ]);

  // 床位: 每病区8张 (ICU 6张)，部分占用与在院登记对应
  const bedRows = []; let bedId = 1;
  const wardConf = [[1,'01',8,'0',60],[2,'02',8,'0',60],[3,'03',8,'0',60],[4,'04',8,'0',60],[5,'ICU',6,'1',180]];
  const occupiedBeds = [1,2,3,9,10,17,18,25,33,34]; // 与 admission 对应
  for (const [wid,prefix,cnt,btype,price] of wardConf) {
    for (let i = 1; i <= cnt; i++) {
      const st = occupiedBeds.includes(bedId) ? '1' : '0';
      const vip = (wid !== 5 && i > cnt - 2) ? '2' : btype; // 每病区最后2张VIP
      const vipPrice = vip === '2' ? 300 : price;
      bedRows.push([bedId, wid, `${prefix}-${String(i).padStart(2,'0')}`, vip, vipPrice, st, 'admin','2026-01-08 09:00:00']);
      bedId++;
    }
  }
  s += ins('his_bed', ['bed_id','ward_id','bed_no','bed_type','price_per_day','status','create_by','create_time'], bedRows);

  // 排班: 医生1-8, 过去6天~未来2天, 上午/下午各一班
  const schRows = []; let schId = 1;
  const scheduleMap = {}; // key doctorId_day_slot -> schId
  for (let d = -6; d <= 2; d++) {
    for (let doc = 1; doc <= 8; doc++) {
      for (let slot = 0; slot <= 1; slot++) {
        const st = (d === 2 && doc === 3) ? '1' : '0'; // 一条停诊演示
        schRows.push([schId, doc <= 3 ? 1 : doc <= 5 ? 2 : doc <= 7 ? 3 : 4, doc, raw(dexpr(d)), String(slot), 20, 20, st, 'admin', '2026-01-08 09:00:00']);
        scheduleMap[`${doc}_${d}_${slot}`] = schId;
        schId++;
      }
    }
  }
  s += insRaw('his_schedule', ['schedule_id','dept_id','doctor_id','work_date','time_slot','quota','remain_quota','status','create_by','create_time'], schRows);

  // 挂号: 历史6天每天约5条(已诊毕+已缴费), 今天10条(各种状态)
  const regRows = []; let regId = 1;
  const docDept = {1:1,2:1,3:1,4:2,5:2,6:3,7:3,8:4};
  const docFee = {1:50,2:30,3:20,4:50,5:20,6:30,7:10,8:30};
  const pats = [1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16];
  let pi = 0, chargeId = 1;
  const chargeRows = [];
  for (let d = -6; d <= -1; d++) {
    for (let k = 0; k < 5; k++) {
      const doc = ((d + k) % 8 + 8) % 8 + 1;
      const slot = k % 2;
      const pat = pats[pi++ % 16];
      const sch = scheduleMap[`${doc}_${d}_${slot}`];
      schRows[sch-1][6] = schRows[sch-1][6] - 1; // remain_quota -1
      regRows.push([regId, `GH2026090${regId}`, pat, docDept[doc], doc, sch, raw(dexpr(d)), String(slot), k+1, docFee[doc], '2', '1', 'admin', raw(texpr(d))]);
      chargeRows.push([chargeId++, `GHF${regId}`, pat, '0', regId, '挂号费', docFee[doc], 1, docFee[doc], '1', String(k % 3), raw(texpr(d)), 'admin', 'admin', raw(texpr(d))]);
      regId++;
    }
  }
  // 今天的挂号: 状态混合 0待就诊/1就诊中/2已诊毕/4过号
  const todaySpecs = [[1,0,'2','1'],[2,0,'2','1'],[3,0,'0','1'],[4,1,'0','1'],[5,1,'1','1'],[6,0,'0','0'],[7,0,'0','1'],[8,1,'4','1'],[1,1,'2','1'],[2,1,'0','0']];
  todaySpecs.forEach(([doc, slot, vst, pay], idx) => {
    const pat = pats[(idx * 3 + 1) % 16];
    const sch = scheduleMap[`${doc}_0_${slot}`];
    schRows[sch-1][6] = schRows[sch-1][6] - 1;
    regRows.push([regId, `GH2026091${regId}`, pat, docDept[doc], doc, sch, raw('curdate()'), String(slot), idx+1, docFee[doc], vst, pay, 'admin', raw('now()')]);
    chargeRows.push([chargeId++, `GHF${regId}`, pat, '0', regId, '挂号费', docFee[doc], 1, docFee[doc], pay === '1' ? '1' : '0', pay === '1' ? String(idx % 3) : null, pay === '1' ? raw('now()') : null, pay === '1' ? 'admin' : null, 'admin', raw('now()')]);
    regId++;
  });
  s += insRaw('his_registration', ['reg_id','reg_no','patient_id','dept_id','doctor_id','schedule_id','reg_date','time_slot','queue_no','reg_fee','visit_status','pay_status','create_by','create_time'], regRows);
  s += insRaw('his_charge', ['charge_id','charge_no','patient_id','source_type','source_id','item_name','price','quantity','amount','charge_status','pay_type','settle_time','operator','create_by','create_time'], chargeRows);

  // 就诊记录: 已诊毕的挂号各一条
  const visitRows = []; let visitId = 1;
  const diagPool = [['头痛发热3天','急性上呼吸道感染','对症处理，多饮水休息'],['反复胸闷1周','冠心病待查','心电图+心脏彩超检查'],['腹痛腹泻2天','急性肠胃炎','清淡饮食，口服补液盐'],['咳嗽咳痰5天','支气管炎','抗感染对症治疗'],['血压升高半年','原发性高血压','规律服药，低盐饮食'],['腰痛1月','腰椎间盘突出','理疗+止痛，避免负重'],['咽痛2天','急性咽炎','含片+抗感染'],['失眠多梦1月','神经衰弱','作息规律，中药调理']];
  regRows.forEach((r, i) => {
    if (r[10] === '2') {
      const dg = diagPool[i % 8];
      visitRows.push([visitId, `JZ${String(visitId).padStart(6,'0')}`, r[0], r[2], r[4], r[3], r[6], dg[0], dg[0] + '，伴乏力', '高血压病史' + (i % 3) + '年', dg[1], dg[2], '1', 'admin', r[6]]);
      visitId++;
    }
  });
  s += insRaw('his_visit', ['visit_id','visit_no','reg_id','patient_id','doctor_id','dept_id','visit_time','chief_complaint','present_illness','past_illness','diagnosis','treatment','status','create_by','create_time'], visitRows);

  // 处方+明细: 前12条就诊各一处方
  const rxRows = []; const rxItemRows = []; let rxId = 1, rxItemId = 1;
  const rxSpec = [
    [[1,2],[2,1],[10,1]], [[4,1],[11,2]], [[20,2],[13,2]], [[5,1],[7,1]], [[8,2],[9,1]],
    [[3,2],[23,1]], [[2,1],[15,1]], [[1,1],[12,3]], [[6,1]], [[4,2],[20,1]], [[19,1],[2,1]], [[21,1],[13,1]],
  ];
  const freq = ['每日三次','每日两次','每日一次'];
  visitRows.slice(0, 12).forEach((v, i) => {
    const items = rxSpec[i];
    let total = 0;
    items.forEach(([did, qty]) => { total += [0,12.5,15.8,14.8,22,18.5,28,32,16.5,11,8,9.8,5.5,4.8,6.5,35,12,15,68,25.5,13.5,19.8,8.5,11.5,22][did] * qty; });
    const st = i < 8 ? '2' : (i < 10 ? '1' : '0'); // 大部分已发药，2已收费，2已开立
    rxRows.push([rxId, `CF${String(rxId).padStart(6,'0')}`, v[0], v[2], v[3], '0', total.toFixed(2), st, st === '2' ? 'admin' : null, st === '2' ? v[6] : null, 'admin', v[6]]);
    items.forEach(([did, qty]) => {
      const dr = [[1,'阿莫西林胶囊','0.25g*24粒','盒',12.5],[2,'布洛芬缓释胶囊','0.3g*20粒','盒',15.8],[3,'连花清瘟胶囊','0.35g*24粒','盒',14.8],[4,'头孢克肟分散片','0.1g*12片','盒',22],[5,'二甲双胍片','0.5g*60片','瓶',18.5],[6,'硝苯地平控释片','30mg*7片','盒',28],[7,'阿托伐他汀钙片','20mg*7片','盒',32],[8,'奥美拉唑肠溶胶囊','20mg*14粒','盒',16.5],[9,'氯雷他定片','10mg*6片','盒',11],[10,'维生素C片','0.1g*100片','瓶',8],[11,'板蓝根颗粒','10g*20袋','盒',9.8],[12,'葡萄糖注射液','5% 250ml','瓶',5.5],[13,'氯化钠注射液','0.9% 250ml','瓶',4.8],[14,'碘伏消毒液','100ml','瓶',6.5],[15,'云南白药气雾剂','85g+30g','盒',35],[16,'藿香正气水','10ml*10支','盒',12],[17,'地塞米松片','0.75mg*100片','瓶',15],[18,'门冬胰岛素注射液','3ml:300单位','支',68],[19,'孟鲁司特钠咀嚼片','5mg*14片','盒',25.5],[20,'蒙脱石散','3g*10袋','盒',13.5],[21,'左氧氟沙星片','0.5g*7片','盒',19.8],[22,'复方氨酚烷胺片','10片','盒',8.5],[23,'复方甘草片','100片','瓶',11.5],[24,'注射用头孢曲松钠','1g/支','支',22]][did-1];
      rxItemRows.push([rxItemId++, rxId, did, dr[1], dr[2], dr[3], dr[4], qty, (dr[4]*qty).toFixed(2), freq[qty % 3], '口服', 7]);
    });
    rxId++;
  });
  s += insRaw('his_prescription', ['rx_id','rx_no','visit_id','patient_id','doctor_id','rx_type','total_amount','status','dispense_by','dispense_time','create_by','create_time'], rxRows);
  s += insRaw('his_prescription_item', ['item_id','rx_id','drug_id','drug_name','specification','unit','price','quantity','amount','usage_dose','frequency','days'], rxItemRows);

  // 住院: 8在院(占用床位) + 2已出院
  const admRows = [];
  const admSpec = [
    [1,1,1,1,1,-4,0,5000,'高血压3级(高危)，头晕待查'],
    [2,2,1,2,2,-3,0,3000,'冠心病，心功能II级'],
    [4,4,1,3,3,-2,0,3000,'慢性胃炎急性发作'],
    [5,5,2,9,4,-5,0,8000,'胆囊结石伴胆囊炎，待手术'],
    [6,6,2,10,4,-1,0,5000,'右下腹痛：阑尾炎？'],
    [7,7,3,17,6,-3,0,2000,'小儿支气管肺炎'],
    [9,9,3,18,6,-2,0,2000,'小儿腹泻病伴脱水'],
    [10,10,2,25,5,-6,0,10000,'脑出血恢复期'],
    [11,11,1,4,1,-10,-6,4000,'2型糖尿病，血糖控制不佳'],
    [13,13,4,26,8,-15,-9,3000,'妊娠38周，待产已分娩'],
  ];
  admSpec.forEach(([aid, pat, dept, bed, doc, inD, outD, dep, dg]) => {
    admRows.push([aid, `ZY${String(aid).padStart(6,'0')}`, pat, dept, bed <= 8 ? 1 : bed <= 16 ? 2 : bed <= 24 ? 3 : bed <= 32 ? 4 : 5, bed, doc, raw(texpr(inD)), outD !== 0 && outD < 0 ? raw(texpr(outD)) : null, dep, outD === 0 ? '0' : '1', dg, 'admin', raw(texpr(inD))]);
  });
  s += insRaw('his_admission', ['adm_id','adm_no','patient_id','dept_id','ward_id','bed_id','doctor_id','in_date','out_date','deposit','adm_status','diagnosis_in','create_by','create_time'], admRows);

  // 医嘱
  s += insRaw('his_medical_order', ['order_id','adm_id','patient_id','doctor_id','order_type','content','drug_id','dose','frequency','start_time','end_time','order_status','exec_by','exec_time','create_by','create_time'], [
    [1,1,1,1,'0','低盐低脂饮食，持续心电监护',null,null,'每日一次',raw(texpr(-4)),null,'1','李护士',raw(texpr(-4)),'admin',raw(texpr(-4))],
    [2,1,1,1,'0','硝苯地平控释片 30mg 口服',6,'30mg','每日一次',raw(texpr(-4)),null,'1','李护士',raw(texpr(-4)),'admin',raw(texpr(-4))],
    [3,2,2,2,'0','阿托伐他汀钙片 20mg 睡前服',7,'20mg','每晚一次',raw(texpr(-3)),null,'1','王护士',raw(texpr(-3)),'admin',raw(texpr(-3))],
    [4,2,2,2,'1','急查心电图+心肌酶谱',null,null,'立即',raw(texpr(-3)),raw(texpr(-3)),'1','王护士',raw(texpr(-3)),'admin',raw(texpr(-3))],
    [5,4,4,4,'0','头孢曲松钠 2g+0.9%氯化钠250ml 静滴',24,'2g','每日一次',raw(texpr(-2)),null,'1','张护士',raw(texpr(-2)),'admin',raw(texpr(-2))],
    [6,5,5,4,'1','术前禁食禁水8小时',null,null,'立即',raw(texpr(-1)),raw(texpr(-1)),'1','张护士',raw(texpr(-1)),'admin',raw(texpr(-1))],
    [7,7,9,6,'0','布洛芬混悬液 10ml 发热时服',2,'10ml','必要时',raw(texpr(-3)),null,'0',null,null,'admin',raw(texpr(-3))],
    [8,7,9,6,'1','急查血常规+CRP',null,null,'立即',raw(texpr(-3)),raw(texpr(-3)),'1','刘护士',raw(texpr(-3)),'admin',raw(texpr(-3))],
    [9,10,13,5,'0','甘露醇125ml 静滴 降颅压',13,'125ml','每8小时一次',raw(texpr(-6)),raw(texpr(-2)),'2','赵护士',raw(texpr(-2)),'admin',raw(texpr(-6))],
    [10,10,13,5,'0','气垫床、翻身拍背每2小时',null,null,'每2小时',raw(texpr(-6)),null,'1','赵护士',raw(texpr(-6)),'admin',raw(texpr(-6))],
    [11,3,4,3,'0','奥美拉唑20mg 口服',8,'20mg','每日两次',raw(texpr(-2)),null,'0',null,null,'admin',raw(texpr(-2))],
    [12,6,6,4,'1','头孢克肟100mg 口服',4,'100mg','每日两次',raw(texpr(-1)),null,'0',null,null,'admin',raw(texpr(-1))],
  ]);

  // 护理记录(体温单曲线演示: 住院1/2近几天)
  const nurRows = []; let nurId = 1;
  const temps = [[37.8,82,18],[37.2,78,17],[36.8,75,16],[36.5,72,16],[36.6,74,17],[36.4,71,16]];
  for (const [adm, pat] of [[1,1],[2,2]]) {
    temps.forEach(([tp, pu, br], i) => {
      nurRows.push([nurId++, adm, pat, adm === 1 ? '李护士' : '王护士', raw(`date_sub(now(), interval ${5 - i} day)`), tp, pu, br, 125 - i * 2, 82 - i, 97, '晨间护理，生命体征平稳', 'admin', raw(`date_sub(now(), interval ${5 - i} day)`)]);
    });
  }
  s += insRaw('his_nursing', ['record_id','adm_id','patient_id','nurse_name','record_time','temperature','pulse','breath','bp_high','bp_low','spo2','content','create_by','create_time'], nurRows);

  // 检验单+结果
  s += insRaw('his_lab_test', ['test_id','test_no','patient_id','visit_id','adm_id','doctor_id','test_item','sample_type','status','apply_time','sample_time','report_time','result_summary','report_by','create_by','create_time'], [
    [1,'JY000001',1,1,null,1,'血常规五分类','0','3',raw(texpr(-5)),raw(texpr(-5)),raw(texpr(-5)),'白细胞偏高，中性粒细胞比例升高','admin','admin',raw(texpr(-5))],
    [2,'JY000002',2,2,null,2,'肝功能全套','0','3',raw(texpr(-4)),raw(texpr(-4)),raw(texpr(-4)),'ALT轻度升高，余正常','admin','admin',raw(texpr(-4))],
    [3,'JY000003',4,null,3,4,'血糖+肾功能','0','3',raw(texpr(-2)),raw(texpr(-2)),raw(texpr(-1)),'空腹血糖8.2偏高','admin','admin',raw(texpr(-2))],
    [4,'JY000004',5,null,4,4,'术前四项','0','2',raw(texpr(-1)),raw(texpr(-1)),null,null,null,'admin',raw(texpr(-1))],
    [5,'JY000005',9,null,7,6,'血常规+CRP','0','1',raw('now()'),raw('now()'),null,null,null,'admin',raw('now()')],
    [6,'JY000006',3,null,8,6,'尿常规','1','0',raw('now()'),null,null,null,null,'admin',raw('now()')],
    [7,'JY000007',7,null,9,7,'血型鉴定','0','0',raw('now()'),null,null,null,null,'admin',raw('now()')],
  ]);
  s += insRaw('his_lab_result', ['result_id','test_id','item_name','result_value','unit','ref_range','flag'], [
    [1,1,'白细胞计数(WBC)','11.2','10^9/L','3.5-9.5','1'],
    [2,1,'中性粒细胞比例','78.5','%','40-75','1'],
    [3,1,'血红蛋白','135','g/L','130-175','0'],
    [4,1,'血小板计数','210','10^9/L','125-350','0'],
    [5,2,'谷丙转氨酶(ALT)','65','U/L','9-50','1'],
    [6,2,'谷草转氨酶(AST)','42','U/L','15-40','1'],
    [7,2,'总胆红素','15.2','umol/L','5-21','0'],
    [8,3,'空腹血糖','8.2','mmol/L','3.9-6.1','1'],
    [9,3,'肌酐','88','umol/L','57-111','0'],
    [10,3,'尿素氮','5.6','mmol/L','3.6-9.5','0'],
  ]);

  // 检查单
  s += insRaw('his_exam', ['exam_id','exam_no','patient_id','doctor_id','exam_type','body_part','purpose','status','apply_time','report_time','finding','conclusion','report_by','create_by','create_time'], [
    [1,'JC000001',1,1,'0','胸部','咳嗽发热待查','2',raw(texpr(-5)),raw(texpr(-5)),'双肺纹理增粗，右下肺见斑片状阴影','右下肺炎症','admin','admin',raw(texpr(-5))],
    [2,'JC000002',2,2,'4','心脏','胸闷待查','2',raw(texpr(-4)),raw(texpr(-4)),'窦性心律，ST段压低','心肌缺血改变','admin','admin',raw(texpr(-4))],
    [3,'JC000003',5,4,'3','右上腹','腹痛待查','2',raw(texpr(-3)),raw(texpr(-3)),'胆囊壁增厚，内见多发强回声','胆囊结石伴胆囊炎','admin','admin',raw(texpr(-3))],
    [4,'JC000004',10,5,'2','腰椎','腰痛待查','1',raw(texpr(-1)),null,null,null,null,'admin',raw(texpr(-1))],
    [5,'JC000005',7,7,'0','头颅','头晕待查','0',raw('now()'),null,null,null,null,'admin',raw('now()')],
    [6,'JC000006',12,8,'3','子宫附件','早孕检查','0',raw('now()'),null,null,null,null,'admin',raw('now()')],
  ]);

  // 手术
  s += insRaw('his_surgery', ['surgery_id','surgery_no','surgery_name','patient_id','dept_id','surgeon_id','anesthesia','room_no','level','plan_time','start_time','end_time','status','create_by','create_time'], [
    [1,'SS000001','腹腔镜胆囊切除术',5,2,4,'0','1号手术间','2',raw(texpr(1)),null,null,'0','admin',raw(texpr(-2))],
    [2,'SS000002','阑尾切除术',6,2,4,'1','2号手术间','1',raw('now()'),raw('now()'),null,'1','admin',raw(texpr(-1))],
    [3,'SS000003','剖宫产术',13,4,8,'3','3号手术间','2',raw(texpr(-9)),raw(texpr(-9)),raw(texpr(-9)),'2','admin',raw(texpr(-10))],
    [4,'SS000004','骨折切开复位内固定术',15,2,5,'1','1号手术间','2',raw(texpr(-7)),raw(texpr(-7)),raw(texpr(-7)),'2','admin',raw(texpr(-8))],
    [5,'SS000005','白内障超声乳化术',14,5,9,'1','4号手术间','1',raw(texpr(2)),null,null,'0','admin',raw(texpr(-1))],
  ]);

  // 采购单+明细
  s += ins('his_purchase', ['purchase_id','purchase_no','supplier_id','total_amount','status','in_time','create_by','create_time'], [
    [1,'CG000001',1,'4850.00','1','2026-02-10 10:00:00','admin','2026-02-09 09:00:00'],
    [2,'CG000002',2,'3240.00','1','2026-03-01 10:00:00','admin','2026-02-28 09:00:00'],
    [3,'CG000003',3,'2100.00','0',null,'admin','2026-09-10 09:00:00'],
  ]);
  s += ins('his_purchase_item', ['item_id','purchase_id','drug_id','quantity','price','amount'], [
    [1,1,1,100,12.00,'1200.00'],[2,1,4,80,20.50,'1640.00'],[3,1,24,90,21.00,'1890.00'],[4,1,10,15,7.00,'105.00'],
    [5,2,12,200,5.00,'1000.00'],[6,2,13,200,4.50,'900.00'],[7,2,18,20,62.00,'1240.00'],
    [8,3,16,100,11.00,'1100.00'],[9,3,17,50,14.00,'700.00'],[10,3,22,40,7.50,'300.00'],
  ]);

  // 出入库记录
  s += insRaw('his_stock_record', ['record_id','record_no','record_type','drug_id','quantity','before_stock','after_stock','source_id','operator','create_by','create_time'], [
    [1,'RK000001','0',1,100,200,300,1,'admin','admin',raw(texpr(-30))],
    [2,'RK000002','0',4,80,70,150,1,'admin','admin',raw(texpr(-30))],
    [3,'RK000003','0',24,90,110,200,1,'admin','admin',raw(texpr(-30))],
    [4,'RK000004','0',12,200,300,500,2,'admin','admin',raw(texpr(-15))],
    [5,'RK000005','0',13,200,400,600,2,'admin','admin',raw(texpr(-15))],
    [6,'CK000001','1',1,2,302,300,null,'admin','admin',raw(texpr(-4))],
    [7,'CK000002','1',2,1,261,260,null,'admin','admin',raw(texpr(-4))],
    [8,'CK000003','1',4,1,151,150,null,'admin','admin',raw(texpr(-3))],
    [9,'CK000004','1',20,2,192,190,null,'admin','admin',raw(texpr(-2))],
    [10,'CK000005','1',8,2,202,200,null,'admin','admin',raw(texpr(-1))],
  ]);

  // 处方费/检验费/检查费账单(联动收费页演示)
  const moreCharge = [];
  rxRows.forEach((r, i) => {
    if (r[7] !== '0') {
      moreCharge.push([chargeId++, `CFF${r[0]}`, r[3], '1', r[0], '处方药品费', r[6], 1, r[6], r[7] === '0' ? '0' : '1', '1', r[11], 'admin', 'admin', r[11]]);
    }
  });
  for (let tid = 1; tid <= 7; tid++) {
    const pat = [1,2,4,5,9,3,7][tid - 1];
    const paid = tid <= 4 ? '1' : '0';
    moreCharge.push([chargeId++, `JYF${tid}`, pat, '2', tid, '检验费', 25, 1, 25, paid, paid === '1' ? '1' : null, paid === '1' ? raw(texpr(-tid)) : null, paid === '1' ? 'admin' : null, 'admin', raw(texpr(-tid))]);
  }
  for (let eid = 1; eid <= 6; eid++) {
    const pat = [1,2,5,10,7,12][eid - 1];
    const paid = eid <= 3 ? '1' : '0';
    const price = [280,40,120,80,280,120][eid - 1];
    moreCharge.push([chargeId++, `JCF${eid}`, pat, '3', eid, '检查费', price, 1, price, paid, paid === '1' ? '1' : null, paid === '1' ? raw(texpr(-eid)) : null, paid === '1' ? 'admin' : null, 'admin', raw(texpr(-eid))]);
  }
  // 在院押金/医嘱费
  for (let aid = 1; aid <= 8; aid++) {
    const pat = admSpec[aid - 1][1];
    moreCharge.push([chargeId++, `ZYF-Y${aid}`, pat, '4', aid, '住院押金', admSpec[aid - 1][7], 1, admSpec[aid - 1][7], '1', '1', raw(texpr(admSpec[aid - 1][5])), 'admin', 'admin', raw(texpr(admSpec[aid - 1][5]))]);
  }
  moreCharge.push([chargeId++, 'YZF1', 1, '4', 1, '用药-硝苯地平控释片', 28, 1, 28, '0', null, null, null, 'admin', raw(texpr(-4))]);
  moreCharge.push([chargeId++, 'YZF2', 2, '4', 3, '用药-阿托伐他汀钙片', 32, 1, 32, '0', null, null, null, 'admin', raw(texpr(-3))]);
  moreCharge.push([chargeId++, 'YZF3', 4, '4', 5, '用药-注射用头孢曲松钠', 22, 2, 44, '0', null, null, null, 'admin', raw(texpr(-2))]);
  s += insRaw('his_charge', ['charge_id','charge_no','patient_id','source_type','source_id','item_name','price','quantity','amount','charge_status','pay_type','settle_time','operator','create_by','create_time'], moreCharge);
  return s;
}

/* ===================== main ===================== */
const ent = process.argv[2]; // 可选: 只生成某类 sql/java/vue/api

if (!ent || ent === 'java') {
  for (const e of ENTITIES) {
    w(path.join(SYS, 'domain', `${e.entity}.java`), genDomain(e));
    w(path.join(SYS, 'mapper', `${e.entity}Mapper.java`), genMapperJava(e));
    w(path.join(SYS, 'service', `I${e.entity}Service.java`), genService(e));
    w(path.join(SYS, 'service', 'impl', `${e.entity}ServiceImpl.java`), genServiceImpl(e));
    w(path.join(XMLDIR, `${e.entity}Mapper.xml`), genXml(e));
    if (!e.noController) w(path.join(CTRL, `${e.entity}Controller.java`), genController(e));
  }
  console.log('java backend generated:', ENTITIES.length, 'entities');
}
if (!ent || ent === 'api') {
  for (const e of ENTITIES) {
    if (e.noPage) continue;
    w(path.join(API, `${e.path}.js`), genApi(e));
  }
  console.log('api js generated');
}
if (!ent || ent === 'vue') {
  for (const e of ENTITIES) {
    if (e.noPage || e.complex) continue;
    w(path.join(VIEWS, e.group, e.view || e.path, 'index.vue'), genVue(e));
  }
  console.log('simple vue pages generated');
}
if (!ent || ent === 'sql') {
  w(SQLFILE, genSql());
  console.log('sql generated ->', SQLFILE);
}


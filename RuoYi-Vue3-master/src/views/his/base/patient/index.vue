<template>
   <div class="app-container">
      <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
         <el-form-item label="就诊卡号" prop="patientNo">
            <el-input v-model="queryParams.patientNo" placeholder="请输入就诊卡号" clearable style="width: 180px" @keyup.enter="handleQuery" />
         </el-form-item>
         <el-form-item label="患者姓名" prop="patientName">
            <el-input v-model="queryParams.patientName" placeholder="请输入患者姓名" clearable style="width: 180px" @keyup.enter="handleQuery" />
         </el-form-item>
         <el-form-item label="身份证号" prop="idCard">
            <el-input v-model="queryParams.idCard" placeholder="请输入身份证号" clearable style="width: 180px" @keyup.enter="handleQuery" />
         </el-form-item>
         <el-form-item label="联系电话" prop="phone">
            <el-input v-model="queryParams.phone" placeholder="请输入联系电话" clearable style="width: 180px" @keyup.enter="handleQuery" />
         </el-form-item>
         <el-form-item>
            <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
            <el-button icon="Refresh" @click="resetQuery">重置</el-button>
         </el-form-item>
      </el-form>

      <el-row :gutter="10" class="mb8">
         <el-col :span="1.5">
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['his:patient:add']">新增建档</el-button>
         </el-col>
         <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['his:patient:edit']">修改</el-button>
         </el-col>
         <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['his:patient:remove']">删除</el-button>
         </el-col>
         <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <el-table v-loading="loading" :data="patientList" @selection-change="handleSelectionChange">
         <el-table-column type="selection" width="55" align="center" />
         <el-table-column label="就诊卡号" align="center" prop="patientNo" width="120" />
         <el-table-column label="姓名" align="center" prop="patientName" width="100" />
         <el-table-column label="性别" align="center" prop="gender" width="70">
            <template #default="scope">
               <dict-tag :options="sys_user_sex" :value="scope.row.gender" />
            </template>
         </el-table-column>
         <el-table-column label="年龄" align="center" prop="age" width="70" />
         <el-table-column label="血型" align="center" prop="bloodType" width="80">
            <template #default="scope">
               <dict-tag :options="his_blood_type" :value="scope.row.bloodType" />
            </template>
         </el-table-column>
         <el-table-column label="医保类型" align="center" prop="insuranceType" width="100">
            <template #default="scope">
               <dict-tag :options="his_insurance_type" :value="scope.row.insuranceType" />
            </template>
         </el-table-column>
         <el-table-column label="联系电话" align="center" prop="phone" width="130" />
         <el-table-column label="过敏史" align="center" prop="allergy" :show-overflow-tooltip="true" />
         <el-table-column label="操作" width="200" align="center" class-name="small-padding fixed-width">
            <template #default="scope">
               <el-button link type="primary" icon="View" @click="handleDetail(scope.row)">档案</el-button>
               <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['his:patient:edit']">修改</el-button>
               <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['his:patient:remove']">删除</el-button>
            </template>
         </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

      <!-- 患者建档/编辑 -->
      <el-dialog :title="title" v-model="open" width="680px" append-to-body>
         <el-form ref="patientRef" :model="form" :rules="rules" label-width="100px">
            <el-row :gutter="12">
               <el-col :span="12"><el-form-item label="就诊卡号" prop="patientNo"><el-input v-model="form.patientNo" placeholder="不填自动生成" /></el-form-item></el-col>
               <el-col :span="12"><el-form-item label="姓名" prop="patientName"><el-input v-model="form.patientName" /></el-form-item></el-col>
               <el-col :span="12">
                  <el-form-item label="性别" prop="gender">
                     <el-radio-group v-model="form.gender">
                        <el-radio v-for="dict in sys_user_sex" :key="dict.value" :value="dict.value">{{ dict.label }}</el-radio>
                     </el-radio-group>
                  </el-form-item>
               </el-col>
               <el-col :span="12"><el-form-item label="出生日期" prop="birthDate"><el-date-picker v-model="form.birthDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" @change="calcAge" /></el-form-item></el-col>
               <el-col :span="12"><el-form-item label="年龄" prop="age"><el-input-number v-model="form.age" :min="0" :max="150" controls-position="right" style="width: 100%" /></el-form-item></el-col>
               <el-col :span="12"><el-form-item label="身份证号" prop="idCard"><el-input v-model="form.idCard" maxlength="18" /></el-form-item></el-col>
               <el-col :span="12"><el-form-item label="联系电话" prop="phone"><el-input v-model="form.phone" maxlength="11" /></el-form-item></el-col>
               <el-col :span="12">
                  <el-form-item label="血型" prop="bloodType">
                     <el-select v-model="form.bloodType" clearable>
                        <el-option v-for="dict in his_blood_type" :key="dict.value" :label="dict.label" :value="dict.value" />
                     </el-select>
                  </el-form-item>
               </el-col>
               <el-col :span="12">
                  <el-form-item label="医保类型" prop="insuranceType">
                     <el-select v-model="form.insuranceType">
                        <el-option v-for="dict in his_insurance_type" :key="dict.value" :label="dict.label" :value="dict.value" />
                     </el-select>
                  </el-form-item>
               </el-col>
               <el-col :span="24"><el-form-item label="住址" prop="address"><el-input v-model="form.address" /></el-form-item></el-col>
               <el-col :span="24"><el-form-item label="过敏史" prop="allergy"><el-input v-model="form.allergy" type="textarea" :rows="2" placeholder="药物/食物过敏史，无则不填" /></el-form-item></el-col>
            </el-row>
         </el-form>
         <template #footer>
            <div class="dialog-footer">
               <el-button type="primary" @click="submitForm">确 定</el-button>
               <el-button @click="cancel">取 消</el-button>
            </div>
         </template>
      </el-dialog>

      <!-- 患者健康档案抽屉: 联动就诊/处方/费用/住院 -->
      <el-drawer v-model="detailOpen" :title="`健康档案 - ${detail.patientName || ''}`" size="62%">
         <el-descriptions :column="4" border style="margin-bottom: 16px">
            <el-descriptions-item label="就诊卡号">{{ detail.patientNo }}</el-descriptions-item>
            <el-descriptions-item label="性别"><dict-tag :options="sys_user_sex" :value="detail.gender" /></el-descriptions-item>
            <el-descriptions-item label="年龄">{{ detail.age }}</el-descriptions-item>
            <el-descriptions-item label="血型"><dict-tag :options="his_blood_type" :value="detail.bloodType" /></el-descriptions-item>
            <el-descriptions-item label="医保"><dict-tag :options="his_insurance_type" :value="detail.insuranceType" /></el-descriptions-item>
            <el-descriptions-item label="电话">{{ detail.phone }}</el-descriptions-item>
            <el-descriptions-item label="过敏史" :span="2"><el-tag v-if="detail.allergy" type="danger">{{ detail.allergy }}</el-tag><span v-else>无</span></el-descriptions-item>
         </el-descriptions>

         <el-tabs v-model="detailTab">
            <el-tab-pane label="就诊记录" name="visit">
               <el-table :data="visitList" size="small" border>
                  <el-table-column label="就诊编号" prop="visitNo" width="110" />
                  <el-table-column label="科室" prop="deptName" width="90" />
                  <el-table-column label="医生" prop="doctorName" width="90" />
                  <el-table-column label="就诊时间" prop="visitTime" width="150">
                     <template #default="s"><span>{{ parseTime(s.row.visitTime) }}</span></template>
                  </el-table-column>
                  <el-table-column label="诊断" prop="diagnosis" :show-overflow-tooltip="true" />
                  <el-table-column label="状态" width="90">
                     <template #default="s"><dict-tag :options="his_visit_status" :value="s.row.status" /></template>
                  </el-table-column>
               </el-table>
            </el-tab-pane>
            <el-tab-pane label="处方记录" name="rx">
               <el-table :data="rxList" size="small" border>
                  <el-table-column label="处方号" prop="rxNo" width="110" />
                  <el-table-column label="医生" prop="doctorName" width="90" />
                  <el-table-column label="金额" prop="totalAmount" width="90" />
                  <el-table-column label="状态" width="90">
                     <template #default="s"><dict-tag :options="his_rx_status" :value="s.row.status" /></template>
                  </el-table-column>
                  <el-table-column label="开立时间" prop="createTime" width="150">
                     <template #default="s"><span>{{ parseTime(s.row.createTime) }}</span></template>
                  </el-table-column>
                  <el-table-column label="明细">
                     <template #default="s">
                        <el-button link type="primary" @click="showRxItems(s.row)">查看药品</el-button>
                     </template>
                  </el-table-column>
               </el-table>
            </el-tab-pane>
            <el-tab-pane label="费用记录" name="charge">
               <el-table :data="chargeList" size="small" border>
                  <el-table-column label="单据号" prop="chargeNo" width="120" />
                  <el-table-column label="类型" width="90">
                     <template #default="s"><dict-tag :options="his_charge_type" :value="s.row.sourceType" /></template>
                  </el-table-column>
                  <el-table-column label="项目" prop="itemName" :show-overflow-tooltip="true" />
                  <el-table-column label="金额" prop="amount" width="90" />
                  <el-table-column label="状态" width="90">
                     <template #default="s"><dict-tag :options="his_charge_status" :value="s.row.chargeStatus" /></template>
                  </el-table-column>
                  <el-table-column label="结算时间" prop="settleTime" width="150">
                     <template #default="s"><span>{{ parseTime(s.row.settleTime) }}</span></template>
                  </el-table-column>
               </el-table>
            </el-tab-pane>
            <el-tab-pane label="住院记录" name="adm">
               <el-table :data="admList" size="small" border>
                  <el-table-column label="住院号" prop="admNo" width="110" />
                  <el-table-column label="病区" prop="wardName" width="110" />
                  <el-table-column label="床号" prop="bedNo" width="80" />
                  <el-table-column label="主治医生" prop="doctorName" width="100" />
                  <el-table-column label="入院时间" prop="inDate" width="150">
                     <template #default="s"><span>{{ parseTime(s.row.inDate) }}</span></template>
                  </el-table-column>
                  <el-table-column label="状态" width="90">
                     <template #default="s"><dict-tag :options="his_adm_status" :value="s.row.admStatus" /></template>
                  </el-table-column>
               </el-table>
            </el-tab-pane>
         </el-tabs>
      </el-drawer>

      <!-- 处发明细弹窗 -->
      <el-dialog title="处方明细" v-model="rxItemsOpen" width="720px" append-to-body>
         <el-table :data="rxItemList" size="small" border>
            <el-table-column label="药品" prop="drugName" />
            <el-table-column label="规格" prop="specification" width="110" />
            <el-table-column label="单价" prop="price" width="80" />
            <el-table-column label="数量" prop="quantity" width="70" />
            <el-table-column label="金额" prop="amount" width="90" />
            <el-table-column label="用法" prop="usageDose" width="110" />
            <el-table-column label="频次" prop="frequency" width="90" />
         </el-table>
      </el-dialog>
   </div>
</template>

<script setup name="HisPatient">
import { listPatient, addPatient, delPatient, getPatient, updatePatient } from "@/api/his/patient"
import { listVisit } from "@/api/his/visit"
import { listPrescription, listRxItems } from "@/api/his/prescription"
import { listCharge } from "@/api/his/charge"
import { listAdmission } from "@/api/his/admission"

const { proxy } = getCurrentInstance()
const { sys_user_sex, his_blood_type, his_insurance_type, his_visit_status, his_rx_status, his_charge_type, his_charge_status, his_adm_status } =
   useDict("sys_user_sex", "his_blood_type", "his_insurance_type", "his_visit_status", "his_rx_status", "his_charge_type", "his_charge_status", "his_adm_status")

const patientList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")

const detailOpen = ref(false)
const detail = ref({})
const detailTab = ref("visit")
const visitList = ref([])
const rxList = ref([])
const chargeList = ref([])
const admList = ref([])
const rxItemsOpen = ref(false)
const rxItemList = ref([])

const data = reactive({
  form: {},
  queryParams: { pageNum: 1, pageSize: 10, patientNo: undefined, patientName: undefined, idCard: undefined, phone: undefined },
  rules: {
    patientName: [{ required: true, message: "患者姓名不能为空", trigger: "blur" }],
    gender: [{ required: true, message: "性别不能为空", trigger: "change" }],
    phone: [{ required: true, message: "联系电话不能为空", trigger: "blur" }]
  }
})
const { queryParams, form, rules } = toRefs(data)

function getList() {
  loading.value = true
  listPatient(queryParams.value).then(res => {
    patientList.value = res.data
    total.value = res.total
    loading.value = false
  })
}

function calcAge(val) {
  if (val) form.value.age = new Date().getFullYear() - new Date(val).getFullYear()
}

function cancel() { open.value = false; reset() }

function reset() {
  form.value = { patientId: undefined, patientNo: undefined, patientName: undefined, gender: "0", birthDate: undefined, age: undefined, idCard: undefined, phone: undefined, address: undefined, bloodType: undefined, insuranceType: "0", allergy: undefined, status: "0", remark: undefined }
  proxy.resetForm("patientRef")
}

function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm("queryRef"); handleQuery() }
function handleSelectionChange(sel) { ids.value = sel.map(i => i.patientId); single.value = sel.length != 1; multiple.value = !sel.length }

function handleAdd() {
  reset()
  form.value.patientNo = "MZ" + new Date().toISOString().slice(0, 10).replace(/-/g, "") + String(Math.floor(Math.random() * 900) + 100)
  open.value = true
  title.value = "患者建档"
}

function handleUpdate(row) {
  reset()
  getPatient(row.patientId || ids.value).then(res => { form.value = res.data; open.value = true; title.value = "修改患者档案" })
}

function submitForm() {
  proxy.$refs["patientRef"].validate(valid => {
    if (!valid) return
    if (!form.value.patientNo) form.value.patientNo = "MZ" + Date.now()
    const fn = form.value.patientId ? updatePatient : addPatient
    fn(form.value).then(() => { proxy.$modal.msgSuccess("保存成功"); open.value = false; getList() })
  })
}

function handleDelete(row) {
  const delIds = row.patientId || ids.value
  proxy.$modal.confirm('是否确认删除患者编号为"' + delIds + '"的数据项？').then(() => delPatient(delIds)).then(() => { getList(); proxy.$modal.msgSuccess("删除成功") }).catch(() => {})
}

/** 打开患者360档案: 联动加载就诊/处方/费用/住院 */
function handleDetail(row) {
  detail.value = row
  detailOpen.value = true
  detailTab.value = "visit"
  const pid = row.patientId
  listVisit({ pageNum: 1, pageSize: 50, patientId: pid }).then(res => visitList.value = res.data)
  listPrescription({ pageNum: 1, pageSize: 50, patientId: pid }).then(res => rxList.value = res.data)
  listCharge({ pageNum: 1, pageSize: 100, patientId: pid }).then(res => chargeList.value = res.data)
  listAdmission({ pageNum: 1, pageSize: 50, patientId: pid }).then(res => admList.value = res.data)
}

function showRxItems(row) {
  listRxItems(row.rxId).then(res => { rxItemList.value = res.data; rxItemsOpen.value = true })
}

getList()
</script>

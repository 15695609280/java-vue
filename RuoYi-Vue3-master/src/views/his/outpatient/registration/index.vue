<template>
   <div class="app-container">
      <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
         <el-form-item label="挂号单号" prop="regNo">
            <el-input v-model="queryParams.regNo" placeholder="请输入挂号单号" clearable style="width: 170px" @keyup.enter="handleQuery" />
         </el-form-item>
         <el-form-item label="科室" prop="deptId">
            <el-select v-model="queryParams.deptId" placeholder="请选择科室" clearable style="width: 150px">
               <el-option v-for="d in deptOptions" :key="d.deptId" :label="d.deptName" :value="d.deptId" />
            </el-select>
         </el-form-item>
         <el-form-item label="就诊日期" prop="regDate">
            <el-date-picker v-model="queryParams.regDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择" style="width: 150px" />
         </el-form-item>
         <el-form-item label="状态" prop="visitStatus">
            <el-select v-model="queryParams.visitStatus" placeholder="状态" clearable style="width: 130px">
               <el-option v-for="d in his_reg_status" :key="d.value" :label="d.label" :value="d.value" />
            </el-select>
         </el-form-item>
         <el-form-item>
            <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
            <el-button icon="Refresh" @click="resetQuery">重置</el-button>
         </el-form-item>
      </el-form>

      <el-row :gutter="10" class="mb8">
         <el-col :span="1.5">
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['his:registration:add']">门诊挂号</el-button>
         </el-col>
         <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <el-table v-loading="loading" :data="regList" border>
         <el-table-column label="单号" align="center" prop="regNo" width="170" />
         <el-table-column label="排队号" align="center" prop="queueNo" width="70" />
         <el-table-column label="患者" align="center" prop="patientName" width="90" />
         <el-table-column label="就诊卡号" align="center" prop="patientNo" width="110" />
         <el-table-column label="科室" align="center" prop="deptName" width="90" />
         <el-table-column label="医生" align="center" prop="doctorName" width="90" />
         <el-table-column label="就诊日期" align="center" prop="regDate" width="105">
            <template #default="s"><span>{{ parseTime(s.row.regDate, '{y}-{m}-{d}') }}</span></template>
         </el-table-column>
         <el-table-column label="时段" align="center" width="80">
            <template #default="s"><dict-tag :options="his_time_slot" :value="s.row.timeSlot" /></template>
         </el-table-column>
         <el-table-column label="挂号费" align="center" prop="regFee" width="80" />
         <el-table-column label="缴费" align="center" width="80">
            <template #default="s">
               <el-tag :type="s.row.payStatus === '1' ? 'success' : 'danger'" size="small">{{ s.row.payStatus === '1' ? '已缴' : '未缴' }}</el-tag>
            </template>
         </el-table-column>
         <el-table-column label="状态" align="center" width="90">
            <template #default="s"><dict-tag :options="his_reg_status" :value="s.row.visitStatus" /></template>
         </el-table-column>
         <el-table-column label="操作" width="170" align="center" fixed="right">
            <template #default="scope">
               <el-button v-if="scope.row.visitStatus === '0' || scope.row.visitStatus === '4'" link type="danger" @click="handleRefund(scope.row)" v-hasPermi="['his:registration:refund']">退号</el-button>
               <el-button v-if="scope.row.visitStatus === '4'" link type="primary" @click="handleRecover(scope.row)" v-hasPermi="['his:registration:refund']">恢复待诊</el-button>
               <el-button v-if="scope.row.visitStatus === '0'" link type="warning" @click="handlePass(scope.row)" v-hasPermi="['his:registration:refund']">过号</el-button>
            </template>
         </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

      <!-- 门诊挂号: 患者->科室->排班(号源) 三级联动 -->
      <el-dialog title="门诊挂号" v-model="open" width="720px" append-to-body>
         <el-steps :active="regStep" align-center finish-status="success" style="margin-bottom: 20px">
            <el-step title="选择患者" />
            <el-step title="选择科室排班" />
            <el-step title="确认挂号" />
         </el-steps>
         <el-form ref="regRef" :model="regForm" :rules="regRules" label-width="100px">
            <el-form-item label="患者" prop="patientId">
               <el-select v-model="regForm.patientId" filterable remote :remote-method="searchPatient" placeholder="输入姓名/卡号搜索" style="width: 60%">
                  <el-option v-for="p in patientOptions" :key="p.patientId" :label="p.patientName + ' (' + p.patientNo + ')'" :value="p.patientId" />
               </el-select>
               <el-button link type="primary" @click="quickAddPatient">没有档案？快速建档</el-button>
            </el-form-item>
            <el-form-item label="就诊科室" prop="deptId">
               <el-select v-model="regForm.deptId" placeholder="先选科室" style="width: 60%" @change="onDeptChange">
                  <el-option v-for="d in outDeptOptions" :key="d.deptId" :label="d.deptName" :value="d.deptId" />
               </el-select>
            </el-form-item>
            <el-form-item label="就诊日期" prop="regDate">
               <el-date-picker v-model="regForm.regDate" type="date" value-format="YYYY-MM-DD" :disabled-date="d => d.getTime() < Date.now() - 86400000" @change="loadSchedules" />
            </el-form-item>
            <el-form-item label="选择排班" prop="scheduleId">
               <div v-if="scheduleOptions.length === 0" style="color:#909399">请先选择科室和日期</div>
               <el-radio-group v-model="regForm.scheduleId" v-else>
                  <el-radio-button v-for="s in scheduleOptions" :key="s.scheduleId" :value="s.scheduleId" style="margin: 4px 8px 4px 0">
                     {{ s.doctorName }} · {{ slotText(s.timeSlot) }} · 余{{ s.remainQuota }}/{{ s.quota }}
                  </el-radio-button>
               </el-radio-group>
            </el-form-item>
            <el-form-item label="费用预览" v-if="selSchedule">
               <el-descriptions :column="3" border size="small">
                  <el-descriptions-item label="医生">{{ selSchedule.doctorName }}({{ titleText(selSchedule.doctorTitle) }})</el-descriptions-item>
                  <el-descriptions-item label="科室">{{ selSchedule.deptName }}</el-descriptions-item>
                  <el-descriptions-item label="挂号费"><span style="color:#f56c6c;font-weight:700">¥ {{ selSchedule.regFee || feeOf(selSchedule.doctorId) }}</span></el-descriptions-item>
               </el-descriptions>
            </el-form-item>
         </el-form>
         <template #footer>
            <div class="dialog-footer">
               <el-button type="primary" @click="submitReg" :disabled="!regForm.scheduleId" v-hasPermi="['his:registration:add']">确认挂号</el-button>
               <el-button @click="open = false">取 消</el-button>
            </div>
         </template>
      </el-dialog>
   </div>
</template>

<script setup name="HisRegistration">
import { listRegistration, register, changeRegStatus } from "@/api/his/registration"
import { listPatient, addPatient } from "@/api/his/patient"
import { listDepartment } from "@/api/his/dept"
import { listDoctor } from "@/api/his/doctor"
import { listAvailableSchedule } from "@/api/his/schedule"

const { proxy } = getCurrentInstance()
const { his_reg_status, his_time_slot, his_doctor_title } = useDict("his_reg_status", "his_time_slot", "his_doctor_title")

const regList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const total = ref(0)

const deptOptions = ref([])
const doctorOptions = ref([])
const patientOptions = ref([])
const scheduleOptions = ref([])

const data = reactive({
  queryParams: { pageNum: 1, pageSize: 10, regNo: undefined, deptId: undefined, regDate: undefined, visitStatus: undefined }
})
const { queryParams } = toRefs(data)

const regForm = ref({ patientId: undefined, deptId: undefined, regDate: undefined, scheduleId: undefined })
const regRules = {
  patientId: [{ required: true, message: "请选择患者", trigger: "change" }],
  deptId: [{ required: true, message: "请选择科室", trigger: "change" }],
  regDate: [{ required: true, message: "请选择日期", trigger: "change" }],
  scheduleId: [{ required: true, message: "请选择排班", trigger: "change" }]
}
const regStep = computed(() => regForm.value.scheduleId ? 3 : (regForm.value.patientId ? 1 : 0))
const selSchedule = computed(() => scheduleOptions.value.find(s => s.scheduleId === regForm.value.scheduleId))

function slotText(v) { return (his_time_slot.value.find(d => d.value === v) || {}).label || v }
function titleText(v) { return (his_doctor_title.value.find(d => d.value === v) || {}).label || '' }
function feeOf(doctorId) { return (doctorOptions.value.find(d => d.doctorId === doctorId) || {}).regFee }

function getList() {
  loading.value = true
  listRegistration(queryParams.value).then(res => { regList.value = res.data; total.value = res.total; loading.value = false })
}

function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm("queryRef"); handleQuery() }

function handleAdd() {
  regForm.value = { patientId: undefined, deptId: undefined, regDate: new Date().toISOString().slice(0, 10), scheduleId: undefined }
  scheduleOptions.value = []
  open.value = true
  searchPatient("")
}

function searchPatient(kw) {
  listPatient({ pageNum: 1, pageSize: 50, patientName: kw || undefined }).then(res => patientOptions.value = res.data)
}

/** 快速建档：引导先建患者档案 */
function quickAddPatient() {
  proxy.$modal.prompt('请输入患者姓名（将创建简易档案）').then(({ value }) => {
    if (!value) return
    return addPatient({ patientName: value, gender: '0', phone: '-', insuranceType: '0', status: '0', patientNo: 'MZ' + Date.now() })
  }).then(() => { proxy.$modal.msgSuccess("建档成功，请在患者档案中补全信息"); searchPatient("") }).catch(() => {})
}

function onDeptChange() {
  regForm.value.scheduleId = undefined
  loadSchedules()
}

function loadSchedules() {
  if (!regForm.value.deptId || !regForm.value.regDate) return
  listAvailableSchedule({ deptId: regForm.value.deptId, workDate: regForm.value.regDate }).then(res => {
    scheduleOptions.value = res.data
  })
}

function submitReg() {
  proxy.$refs["regRef"].validate(valid => {
    if (!valid) return
    register({ patientId: regForm.value.patientId, scheduleId: regForm.value.scheduleId }).then(res => {
      const reg = res.data
      proxy.$modal.msgSuccess(`挂号成功！单号 ${reg.regNo}，排队号 ${reg.queueNo}，请到门诊收费处缴费`)
      open.value = false
      getList()
    })
  })
}

function handleRefund(row) {
  proxy.$modal.confirm('确认退号？将退回号源并作废未缴挂号费。').then(() => changeRegStatus(row.regId, '3')).then(() => { getList(); proxy.$modal.msgSuccess("已退号") }).catch(() => {})
}
function handlePass(row) {
  changeRegStatus(row.regId, '4').then(() => { getList(); proxy.$modal.msgSuccess("已过号") })
}
function handleRecover(row) {
  changeRegStatus(row.regId, '0').then(() => { getList(); proxy.$modal.msgSuccess("已恢复待诊") })
}

listDepartment({ pageNum: 1, pageSize: 100 }).then(res => deptOptions.value = res.data)
listDoctor({ pageNum: 1, pageSize: 200 }).then(res => doctorOptions.value = res.data)
const outDeptOptions = computed(() => deptOptions.value.filter(d => d.deptType === '0'))
getList()
</script>

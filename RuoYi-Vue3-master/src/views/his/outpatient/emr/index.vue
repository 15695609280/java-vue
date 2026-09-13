<template>
   <div class="app-container">
      <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
         <el-form-item label="就诊编号" prop="visitNo">
            <el-input v-model="queryParams.visitNo" placeholder="请输入" clearable style="width: 150px" @keyup.enter="handleQuery" />
         </el-form-item>
         <el-form-item label="患者姓名">
            <el-select v-model="queryParams.patientId" filterable remote :remote-method="searchPatient" clearable placeholder="搜索患者" style="width: 180px" @change="handleQuery">
               <el-option v-for="p in patientOptions" :key="p.patientId" :label="p.patientName" :value="p.patientId" />
            </el-select>
         </el-form-item>
         <el-form-item label="医生" prop="doctorId">
            <el-select v-model="queryParams.doctorId" clearable filterable style="width: 130px">
               <el-option v-for="d in doctorOptions" :key="d.doctorId" :label="d.doctorName" :value="d.doctorId" />
            </el-select>
         </el-form-item>
         <el-form-item label="状态" prop="status">
            <el-select v-model="queryParams.status" clearable style="width: 110px">
               <el-option v-for="d in his_visit_status" :key="d.value" :label="d.label" :value="d.value" />
            </el-select>
         </el-form-item>
         <el-form-item>
            <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
            <el-button icon="Refresh" @click="resetQuery">重置</el-button>
         </el-form-item>
      </el-form>

      <el-row :gutter="10" class="mb8">
         <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <el-table v-loading="loading" :data="visitList" border @row-click="openDetail">
         <el-table-column label="就诊编号" align="center" prop="visitNo" width="140" />
         <el-table-column label="患者" align="center" prop="patientName" width="90" />
         <el-table-column label="科室" align="center" prop="deptName" width="90" />
         <el-table-column label="医生" align="center" prop="doctorName" width="90" />
         <el-table-column label="就诊时间" align="center" prop="visitTime" width="150">
            <template #default="s"><span>{{ parseTime(s.row.visitTime) }}</span></template>
         </el-table-column>
         <el-table-column label="诊断" prop="diagnosis" :show-overflow-tooltip="true" min-width="140" />
         <el-table-column label="状态" align="center" width="90">
            <template #default="s"><dict-tag :options="his_visit_status" :value="s.row.status" /></template>
         </el-table-column>
         <el-table-column label="操作" width="150" align="center" fixed="right">
            <template #default="scope">
               <el-button link type="primary" @click.stop="openDetail(scope.row)" v-hasPermi="['his:visit:query']">查看病历</el-button>
               <el-button v-if="scope.row.status === '0'" link type="success" @click.stop="doFinish(scope.row)" v-hasPermi="['his:visit:edit']">完诊</el-button>
            </template>
         </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

      <!-- 病历详情抽屉: 病历 + 关联处方/检验/检查 -->
      <el-drawer v-model="open" :title="`门诊病历 - ${cur.patientName || ''}`" size="52%">
         <template v-if="cur.visitId">
            <el-descriptions :column="3" border size="small" style="margin-bottom:14px">
               <el-descriptions-item label="就诊编号">{{ cur.visitNo }}</el-descriptions-item>
               <el-descriptions-item label="科室">{{ cur.deptName }}</el-descriptions-item>
               <el-descriptions-item label="医生">{{ cur.doctorName }}</el-descriptions-item>
               <el-descriptions-item label="就诊时间">{{ parseTime(cur.visitTime) }}</el-descriptions-item>
               <el-descriptions-item label="状态"><dict-tag :options="his_visit_status" :value="cur.status" /></el-descriptions-item>
            </el-descriptions>

            <el-tabs v-model="tab">
               <el-tab-pane label="病历内容" name="emr">
                  <el-form :model="cur" label-width="90px" :disabled="cur.status !== '0'">
                     <el-form-item label="主诉"><el-input v-model="cur.chiefComplaint" type="textarea" :rows="2" /></el-form-item>
                     <el-form-item label="现病史"><el-input v-model="cur.presentIllness" type="textarea" :rows="3" /></el-form-item>
                     <el-form-item label="既往史"><el-input v-model="cur.pastIllness" type="textarea" :rows="2" /></el-form-item>
                     <el-form-item label="诊断"><el-input v-model="cur.diagnosis" /></el-form-item>
                     <el-form-item label="处理意见"><el-input v-model="cur.treatment" type="textarea" :rows="2" /></el-form-item>
                     <el-form-item v-if="cur.status === '0'">
                        <el-button type="primary" @click="saveEmr" v-hasPermi="['his:visit:edit']">保存修改</el-button>
                     </el-form-item>
                  </el-form>
               </el-tab-pane>
               <el-tab-pane :label="`处方(${rxList.length})`" name="rx">
                  <el-table :data="rxList" size="small" border>
                     <el-table-column prop="rxNo" label="处方号" width="130" />
                     <el-table-column prop="totalAmount" label="金额" width="90" />
                     <el-table-column label="状态" width="90"><template #default="s"><dict-tag :options="his_rx_status" :value="s.row.status" /></template></el-table-column>
                     <el-table-column prop="createTime" label="时间"><template #default="s"><span>{{ parseTime(s.row.createTime) }}</span></template></el-table-column>
                  </el-table>
               </el-tab-pane>
               <el-tab-pane :label="`检验(${labList.length})`" name="lab">
                  <el-table :data="labList" size="small" border>
                     <el-table-column prop="testNo" label="单号" width="120" />
                     <el-table-column prop="testItem" label="项目" />
                     <el-table-column label="状态" width="90"><template #default="s"><dict-tag :options="his_lab_status" :value="s.row.status" /></template></el-table-column>
                     <el-table-column prop="resultSummary" label="结论" :show-overflow-tooltip="true" />
                  </el-table>
               </el-tab-pane>
               <el-tab-pane :label="`检查(${examList.length})`" name="exam">
                  <el-table :data="examList" size="small" border>
                     <el-table-column prop="examNo" label="单号" width="120" />
                     <el-table-column label="类型" width="90"><template #default="s"><dict-tag :options="his_exam_type" :value="s.row.examType" /></template></el-table-column>
                     <el-table-column prop="bodyPart" label="部位" width="90" />
                     <el-table-column label="状态" width="90"><template #default="s"><dict-tag :options="his_exam_status" :value="s.row.status" /></template></el-table-column>
                     <el-table-column prop="conclusion" label="结论" :show-overflow-tooltip="true" />
                  </el-table>
               </el-tab-pane>
            </el-tabs>
         </template>
      </el-drawer>
   </div>
</template>

<script setup name="HisEmr">
import { listVisit, updateVisit, finishVisit } from "@/api/his/visit"
import { listPatient } from "@/api/his/patient"
import { listDoctor } from "@/api/his/doctor"
import { listPrescription } from "@/api/his/prescription"
import { listLabTest } from "@/api/his/lab"
import { listExam } from "@/api/his/exam"

const { proxy } = getCurrentInstance()
const { his_visit_status, his_rx_status, his_lab_status, his_exam_status, his_exam_type } =
   useDict("his_visit_status", "his_rx_status", "his_lab_status", "his_exam_status", "his_exam_type")

const visitList = ref([])
const loading = ref(true)
const showSearch = ref(true)
const total = ref(0)
const open = ref(false)
const tab = ref("emr")
const cur = ref({})
const rxList = ref([]), labList = ref([]), examList = ref([])
const patientOptions = ref([]), doctorOptions = ref([])

const data = reactive({ queryParams: { pageNum: 1, pageSize: 10, visitNo: undefined, patientId: undefined, doctorId: undefined, status: undefined } })
const { queryParams } = toRefs(data)

function getList() {
  loading.value = true
  listVisit(queryParams.value).then(res => { visitList.value = res.data; total.value = res.total; loading.value = false })
}
function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm("queryRef"); queryParams.value.patientId = undefined; handleQuery() }

function searchPatient(kw) { listPatient({ pageNum: 1, pageSize: 50, patientName: kw || undefined }).then(res => patientOptions.value = res.data) }

/** 打开病历抽屉并联动加载本次就诊的处方/检验/检查 */
function openDetail(row) {
  cur.value = { ...row }
  tab.value = "emr"
  open.value = true
  // 处方/检验/检查均无 visitId 查询条件，按患者维度联动展示
  listPrescription({ patientId: row.patientId, pageSize: 50 }).then(res => rxList.value = res.data)
  listLabTest({ patientId: row.patientId, pageSize: 50 }).then(res => labList.value = res.data)
  listExam({ patientId: row.patientId, pageSize: 50 }).then(res => examList.value = res.data)
}

function saveEmr() {
  updateVisit(cur.value).then(() => { proxy.$modal.msgSuccess("病历已更新"); getList() })
}

function doFinish(row) {
  proxy.$modal.confirm(`确认完诊患者 ${row.patientName}？挂号单将标记为已诊毕。`).then(() => finishVisit(row.visitId)).then(() => { proxy.$modal.msgSuccess("已完诊"); getList() }).catch(() => {})
}

listDoctor({ pageNum: 1, pageSize: 200 }).then(res => doctorOptions.value = res.data)
searchPatient("")
getList()
</script>

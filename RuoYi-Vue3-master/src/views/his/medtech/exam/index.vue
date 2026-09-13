<template>
   <div class="app-container">
      <el-form :inline="true">
         <el-form-item label="状态">
            <el-select v-model="queryParams.status" clearable placeholder="全部" style="width: 130px" @change="getList">
               <el-option v-for="d in his_exam_status" :key="d.value" :label="d.label" :value="d.value" />
            </el-select>
         </el-form-item>
         <el-form-item label="检查类型">
            <el-select v-model="queryParams.examType" clearable placeholder="全部" style="width: 130px" @change="getList">
               <el-option v-for="d in his_exam_type" :key="d.value" :label="d.label" :value="d.value" />
            </el-select>
         </el-form-item>
         <el-form-item><el-button type="primary" icon="Refresh" @click="getList">刷新</el-button></el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="rows" border>
         <el-table-column label="检查单号" align="center" prop="examNo" width="130" />
         <el-table-column label="患者" align="center" prop="patientName" width="90" />
         <el-table-column label="申请医生" align="center" prop="doctorName" width="90" />
         <el-table-column label="类型" align="center" width="100">
            <template #default="s"><dict-tag :options="his_exam_type" :value="s.row.examType" /></template>
         </el-table-column>
         <el-table-column label="检查部位" prop="bodyPart" width="120" />
         <el-table-column label="检查目的" prop="purpose" :show-overflow-tooltip="true" min-width="140" />
         <el-table-column label="申请时间" align="center" prop="applyTime" width="150">
            <template #default="s"><span>{{ parseTime(s.row.applyTime) }}</span></template>
         </el-table-column>
         <el-table-column label="状态" align="center" width="90">
            <template #default="s"><dict-tag :options="his_exam_status" :value="s.row.status" /></template>
         </el-table-column>
         <el-table-column label="诊断结论" prop="conclusion" :show-overflow-tooltip="true" min-width="140" />
         <el-table-column label="操作" width="170" align="center" fixed="right">
            <template #default="scope">
               <el-button v-if="scope.row.status === '0'" link type="primary" @click="flow(scope.row, '1', '登记执行')" v-hasPermi="['his:exam:flow']">执行</el-button>
               <el-button v-if="scope.row.status === '0'" link type="danger" @click="flow(scope.row, '3', '取消申请')" v-hasPermi="['his:exam:flow']">取消</el-button>
               <el-button v-if="scope.row.status === '1'" link type="success" @click="openReport(scope.row)" v-hasPermi="['his:exam:flow']">出具报告</el-button>
               <el-button v-if="scope.row.status === '2'" link type="info" @click="viewReport(scope.row)">查看报告</el-button>
            </template>
         </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

      <el-dialog title="出具检查报告" v-model="open" width="620px" append-to-body>
         <el-descriptions :column="2" border size="small" style="margin-bottom:12px">
            <el-descriptions-item label="检查单号">{{ cur.examNo }}</el-descriptions-item>
            <el-descriptions-item label="患者">{{ cur.patientName }}</el-descriptions-item>
            <el-descriptions-item label="类型"><dict-tag :options="his_exam_type" :value="cur.examType" /></el-descriptions-item>
            <el-descriptions-item label="部位">{{ cur.bodyPart }}</el-descriptions-item>
         </el-descriptions>
         <el-form :model="form" label-width="100px">
            <el-form-item label="影像所见" required><el-input v-model="form.finding" type="textarea" :rows="4" placeholder="描述影像学所见" /></el-form-item>
            <el-form-item label="诊断结论" required><el-input v-model="form.conclusion" type="textarea" :rows="2" placeholder="报告结论" /></el-form-item>
            <el-form-item label="报告医生"><el-input v-model="form.reportBy" /></el-form-item>
         </el-form>
         <template #footer>
            <el-button type="primary" @click="submitReport">出具报告</el-button>
            <el-button @click="open = false">取消</el-button>
         </template>
      </el-dialog>

      <el-drawer v-model="viewOpen" :title="`检查报告 - ${cur.examNo || ''}`" size="40%">
         <el-descriptions :column="1" border>
            <el-descriptions-item label="患者">{{ cur.patientName }}</el-descriptions-item>
            <el-descriptions-item label="检查部位">{{ cur.bodyPart }}</el-descriptions-item>
            <el-descriptions-item label="影像所见">{{ cur.finding }}</el-descriptions-item>
            <el-descriptions-item label="诊断结论">{{ cur.conclusion }}</el-descriptions-item>
            <el-descriptions-item label="报告医生">{{ cur.reportBy }}</el-descriptions-item>
            <el-descriptions-item label="报告时间">{{ parseTime(cur.reportTime) }}</el-descriptions-item>
         </el-descriptions>
      </el-drawer>
   </div>
</template>

<script setup name="HisExam">
import { listExam, examFlow, reportExam } from "@/api/his/exam"

const { proxy } = getCurrentInstance()
const { his_exam_status, his_exam_type } = useDict("his_exam_status", "his_exam_type")

const queryParams = reactive({ pageNum: 1, pageSize: 10, status: undefined, examType: undefined })
const rows = ref([])
const loading = ref(false)
const total = ref(0)
const open = ref(false)
const viewOpen = ref(false)
const cur = ref({})
const form = ref({})

function getList() {
  loading.value = true
  listExam(queryParams).then(res => { rows.value = res.data; total.value = res.total; loading.value = false })
}

function flow(row, status, action) {
  proxy.$modal.confirm(`确认${action}？`).then(() => examFlow(row.examId, status)).then(() => { proxy.$modal.msgSuccess(`${action}成功`); getList() }).catch(() => {})
}

function openReport(row) {
  cur.value = row
  form.value = { finding: undefined, conclusion: undefined, reportBy: undefined }
  open.value = true
}

function submitReport() {
  if (!form.value.finding || !form.value.conclusion) return proxy.$modal.msgWarning("请填写影像所见和诊断结论")
  reportExam({ examId: cur.value.examId, ...form.value }).then(() => { proxy.$modal.msgSuccess("报告已出具"); open.value = false; getList() })
}

function viewReport(row) { cur.value = row; viewOpen.value = true }

getList()
</script>

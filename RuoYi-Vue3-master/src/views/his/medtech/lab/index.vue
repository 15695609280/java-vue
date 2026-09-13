<template>
   <div class="app-container">
      <el-form :inline="true">
         <el-form-item label="状态">
            <el-select v-model="queryParams.status" clearable placeholder="全部" style="width: 130px" @change="getList">
               <el-option v-for="d in his_lab_status" :key="d.value" :label="d.label" :value="d.value" />
            </el-select>
         </el-form-item>
         <el-form-item label="检验单号">
            <el-input v-model="queryParams.testNo" placeholder="搜索" clearable style="width:150px" @keyup.enter="getList" />
         </el-form-item>
         <el-form-item><el-button type="primary" icon="Refresh" @click="getList">刷新</el-button></el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="rows" border>
         <el-table-column label="检验单号" align="center" prop="testNo" width="130" />
         <el-table-column label="患者" align="center" width="110">
            <template #default="s">{{ s.row.patientName }}
               <span style="color:#909399;font-size:12px">({{ s.row.gender === '0' ? '男' : '女' }}/{{ s.row.age }}岁)</span>
            </template>
         </el-table-column>
         <el-table-column label="申请医生" align="center" prop="doctorName" width="90" />
         <el-table-column label="检验项目" prop="testItem" min-width="140" :show-overflow-tooltip="true" />
         <el-table-column label="标本" align="center" width="80">
            <template #default="s"><dict-tag :options="his_sample_type" :value="s.row.sampleType" /></template>
         </el-table-column>
         <el-table-column label="申请时间" align="center" prop="applyTime" width="150">
            <template #default="s"><span>{{ parseTime(s.row.applyTime) }}</span></template>
         </el-table-column>
         <el-table-column label="状态" align="center" width="90">
            <template #default="s"><dict-tag :options="his_lab_status" :value="s.row.status" /></template>
         </el-table-column>
         <el-table-column label="检验结论" prop="resultSummary" :show-overflow-tooltip="true" min-width="140" />
         <el-table-column label="操作" width="200" align="center" fixed="right">
            <template #default="scope">
               <el-button v-if="scope.row.status === '0'" link type="primary" @click="flow(scope.row, '1', '采样')" v-hasPermi="['his:lab:flow']">采样</el-button>
               <el-button v-if="scope.row.status === '0'" link type="danger" @click="flow(scope.row, '4', '取消申请')" v-hasPermi="['his:lab:flow']">取消</el-button>
               <el-button v-if="scope.row.status === '1'" link type="warning" @click="flow(scope.row, '2', '开始检验')" v-hasPermi="['his:lab:flow']">开始检验</el-button>
               <el-button v-if="scope.row.status === '2'" link type="success" @click="openReport(scope.row)" v-hasPermi="['his:lab:flow']">录入结果</el-button>
               <el-button v-if="scope.row.status === '3'" link type="info" @click="viewResult(scope.row)">查看结果</el-button>
            </template>
         </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

      <!-- 录入结果: 明细行编辑 + 结论 -->
      <el-dialog title="录入检验结果" v-model="open" width="780px" append-to-body>
         <el-descriptions :column="3" border size="small" style="margin-bottom:12px">
            <el-descriptions-item label="检验单号">{{ cur.testNo }}</el-descriptions-item>
            <el-descriptions-item label="患者">{{ cur.patientName }}</el-descriptions-item>
            <el-descriptions-item label="标本"><dict-tag :options="his_sample_type" :value="cur.sampleType" /></el-descriptions-item>
            <el-descriptions-item label="检验项目" :span="3">{{ cur.testItem }}</el-descriptions-item>
         </el-descriptions>

         <div style="display:flex;justify-content:space-between;margin-bottom:8px">
            <b>结果明细</b>
            <el-button size="small" icon="Plus" @click="form.resultList.push({ itemName: '', resultValue: '', unit: '', refRange: '', flag: '0' })">添加项目</el-button>
         </div>
         <el-table :data="form.resultList" size="small" border>
            <el-table-column label="项目名称" min-width="140">
               <template #default="s"><el-input v-model="s.row.itemName" size="small" placeholder="如 白细胞WBC" /></template>
            </el-table-column>
            <el-table-column label="结果值" width="110">
               <template #default="s"><el-input v-model="s.row.resultValue" size="small" /></template>
            </el-table-column>
            <el-table-column label="单位" width="110">
               <template #default="s"><el-input v-model="s.row.unit" size="small" placeholder="×10⁹/L" /></template>
            </el-table-column>
            <el-table-column label="参考范围" width="110">
               <template #default="s"><el-input v-model="s.row.refRange" size="small" /></template>
            </el-table-column>
            <el-table-column label="标识" width="110">
               <template #default="s">
                  <el-select v-model="s.row.flag" size="small">
                     <el-option v-for="d in his_lab_flag" :key="d.value" :label="d.label" :value="d.value" />
                  </el-select>
               </template>
            </el-table-column>
            <el-table-column width="60" align="center">
               <template #default="s"><el-button link type="danger" icon="Delete" @click="form.resultList.splice(s.$index, 1)" /></template>
            </el-table-column>
         </el-table>

         <el-form :model="form" label-width="100px" style="margin-top:14px">
            <el-form-item label="检验结论" required><el-input v-model="form.resultSummary" type="textarea" :rows="2" /></el-form-item>
            <el-form-item label="报告人"><el-input v-model="form.reportBy" /></el-form-item>
         </el-form>
         <template #footer>
            <el-button type="primary" @click="submitReport">出具报告</el-button>
            <el-button @click="open = false">取消</el-button>
         </template>
      </el-dialog>

      <!-- 查看结果 -->
      <el-drawer v-model="viewOpen" :title="`检验报告 - ${cur.testNo || ''}`" size="44%">
         <el-descriptions :column="2" border size="small" style="margin-bottom:12px">
            <el-descriptions-item label="患者">{{ cur.patientName }}</el-descriptions-item>
            <el-descriptions-item label="报告时间">{{ parseTime(cur.reportTime) }}</el-descriptions-item>
            <el-descriptions-item label="检验项目" :span="2">{{ cur.testItem }}</el-descriptions-item>
            <el-descriptions-item label="检验结论" :span="2">{{ cur.resultSummary }}</el-descriptions-item>
            <el-descriptions-item label="报告人" :span="2">{{ cur.reportBy }}</el-descriptions-item>
         </el-descriptions>
         <el-table :data="resultList" size="small" border>
            <el-table-column label="项目" prop="itemName" />
            <el-table-column label="结果" prop="resultValue" width="90" />
            <el-table-column label="单位" prop="unit" width="90" />
            <el-table-column label="参考范围" prop="refRange" width="100" />
            <el-table-column label="标识" width="80">
               <template #default="s"><dict-tag :options="his_lab_flag" :value="s.row.flag" /></template>
            </el-table-column>
         </el-table>
      </el-drawer>
   </div>
</template>

<script setup name="HisLab">
import { listLabTest, labFlow, reportLab, listLabResult } from "@/api/his/lab"

const { proxy } = getCurrentInstance()
const { his_lab_status, his_sample_type, his_lab_flag } = useDict("his_lab_status", "his_sample_type", "his_lab_flag")

const queryParams = reactive({ pageNum: 1, pageSize: 10, status: undefined, testNo: undefined })
const rows = ref([])
const loading = ref(false)
const total = ref(0)
const open = ref(false)
const viewOpen = ref(false)
const cur = ref({})
const form = ref({ resultList: [] })
const resultList = ref([])

function getList() {
  loading.value = true
  listLabTest(queryParams).then(res => { rows.value = res.data; total.value = res.total; loading.value = false })
}

function flow(row, status, action) {
  proxy.$modal.confirm(`确认${action}？`).then(() => labFlow(row.testId, status)).then(() => { proxy.$modal.msgSuccess(`${action}成功`); getList() }).catch(() => {})
}

function openReport(row) {
  cur.value = row
  form.value = { resultList: [{ itemName: '', resultValue: '', unit: '', refRange: '', flag: '0' }], resultSummary: undefined, reportBy: undefined }
  open.value = true
}

function submitReport() {
  const list = form.value.resultList.filter(r => r.itemName && r.resultValue)
  if (!form.value.resultSummary) return proxy.$modal.msgWarning("请填写检验结论")
  reportLab({ testId: cur.value.testId, resultSummary: form.value.resultSummary, reportBy: form.value.reportBy, resultList: list }).then(() => {
    proxy.$modal.msgSuccess("报告已出具"); open.value = false; getList()
  })
}

function viewResult(row) {
  cur.value = row
  listLabResult(row.testId).then(res => { resultList.value = res.data || []; viewOpen.value = true })
}

getList()
</script>

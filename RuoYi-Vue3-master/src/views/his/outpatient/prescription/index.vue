<template>
   <div class="app-container">
      <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
         <el-form-item label="处方号" prop="rxNo">
            <el-input v-model="queryParams.rxNo" placeholder="请输入处方号" clearable style="width: 160px" @keyup.enter="handleQuery" />
         </el-form-item>
         <el-form-item label="状态" prop="status">
            <el-select v-model="queryParams.status" clearable placeholder="状态" style="width: 130px">
               <el-option v-for="d in his_rx_status" :key="d.value" :label="d.label" :value="d.value" />
            </el-select>
         </el-form-item>
         <el-form-item>
            <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
            <el-button icon="Refresh" @click="resetQuery">重置</el-button>
         </el-form-item>
      </el-form>

      <el-row :gutter="10" class="mb8">
         <el-col :span="1.5">
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['his:prescription:add']">手动开方</el-button>
         </el-col>
         <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <el-table v-loading="loading" :data="rxList" border>
         <el-table-column type="expand">
            <template #default="scope">
               <div style="padding: 4px 40px">
                  <el-table :data="itemMap[scope.row.rxId] || []" size="small" border>
                     <el-table-column prop="drugName" label="药品" />
                     <el-table-column prop="specification" label="规格" width="120" />
                     <el-table-column prop="price" label="单价" width="80" />
                     <el-table-column prop="quantity" label="数量" width="70" />
                     <el-table-column prop="amount" label="金额" width="90" />
                     <el-table-column prop="usageDose" label="用法" width="120" />
                     <el-table-column prop="frequency" label="频次" width="90" />
                     <el-table-column prop="days" label="天数" width="60" />
                  </el-table>
               </div>
            </template>
         </el-table-column>
         <el-table-column label="处方号" align="center" prop="rxNo" width="130" />
         <el-table-column label="患者" align="center" prop="patientName" width="100" />
         <el-table-column label="医生" align="center" prop="doctorName" width="100" />
         <el-table-column label="类型" align="center" width="90">
            <template #default="s"><dict-tag :options="his_rx_type" :value="s.row.rxType" /></template>
         </el-table-column>
         <el-table-column label="合计金额" align="center" prop="totalAmount" width="100">
            <template #default="s"><span style="color:#f56c6c">¥{{ s.row.totalAmount }}</span></template>
         </el-table-column>
         <el-table-column label="状态" align="center" width="90">
            <template #default="s"><dict-tag :options="his_rx_status" :value="s.row.status" /></template>
         </el-table-column>
         <el-table-column label="开立时间" align="center" prop="createTime" width="160">
            <template #default="s"><span>{{ parseTime(s.row.createTime) }}</span></template>
         </el-table-column>
         <el-table-column label="操作" width="120" align="center">
            <template #default="scope">
               <el-button v-if="scope.row.status !== '2' && scope.row.status !== '3'" link type="danger" @click="handleCancel(scope.row)" v-hasPermi="['his:prescription:cancel']">作废</el-button>
            </template>
         </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

      <!-- 手动开方: 患者 + 明细编辑(主从联动) -->
      <el-dialog title="手动开方" v-model="open" width="900px" append-to-body>
         <el-form :model="rxForm" label-width="90px">
            <el-row :gutter="12">
               <el-col :span="10">
                  <el-form-item label="患者" required>
                     <el-select v-model="rxForm.patientId" filterable remote :remote-method="searchPatient" placeholder="搜索患者" style="width:100%">
                        <el-option v-for="p in patientOptions" :key="p.patientId" :label="p.patientName + '(' + p.patientNo + ')'" :value="p.patientId" />
                     </el-select>
                  </el-form-item>
               </el-col>
               <el-col :span="8">
                  <el-form-item label="处方类型">
                     <el-select v-model="rxForm.rxType" style="width:100%">
                        <el-option v-for="d in his_rx_type" :key="d.value" :label="d.label" :value="d.value" />
                     </el-select>
                  </el-form-item>
               </el-col>
            </el-row>
            <el-table :data="rxForm.itemList" border size="small" style="margin-bottom:10px">
               <el-table-column label="药品" min-width="230">
                  <template #default="s">
                     <el-select v-model="s.row.drugId" filterable placeholder="选择药品" @change="onDrugPick(s.row)">
                        <el-option v-for="d in drugOptions" :key="d.drugId" :label="`${d.drugName} ${d.specification} ¥${d.price} (存${d.stock})`" :value="d.drugId" />
                     </el-select>
                  </template>
               </el-table-column>
               <el-table-column label="单价" width="80" prop="price" />
               <el-table-column label="数量" width="110"><template #default="s"><el-input-number v-model="s.row.quantity" :min="1" size="small" controls-position="right" @change="calcItem(s.row)" /></template></el-table-column>
               <el-table-column label="用法用量" width="130"><template #default="s"><el-input v-model="s.row.usageDose" size="small" /></template></el-table-column>
               <el-table-column label="频次" width="110"><template #default="s"><el-input v-model="s.row.frequency" size="small" /></template></el-table-column>
               <el-table-column label="金额" width="90"><template #default="s">¥{{ s.row.amount || 0 }}</template></el-table-column>
               <el-table-column width="60"><template #default="s"><el-button link type="danger" @click="rxForm.itemList.splice(s.$index, 1)">删</el-button></template></el-table-column>
            </el-table>
            <div style="display:flex;justify-content:space-between">
               <el-button icon="Plus" @click="rxForm.itemList.push({ quantity: 1, frequency: '每日三次', days: 7 })">添加药品</el-button>
               <span style="font-size:16px">合计 <b style="color:#f56c6c">¥{{ rxTotal }}</b></span>
            </div>
         </el-form>
         <template #footer>
            <el-button type="primary" @click="submitRx">提交处方</el-button>
            <el-button @click="open = false">取 消</el-button>
         </template>
      </el-dialog>
   </div>
</template>

<script setup name="HisPrescription">
import { listPrescription, createPrescription, cancelRx, listRxItems } from "@/api/his/prescription"
import { listPatient } from "@/api/his/patient"
import { listDoctor } from "@/api/his/doctor"
import { listDrug } from "@/api/his/drug"

const { proxy } = getCurrentInstance()
const { his_rx_status, his_rx_type } = useDict("his_rx_status", "his_rx_type")

const rxList = ref([])
const loading = ref(true)
const showSearch = ref(true)
const total = ref(0)
const open = ref(false)
const itemMap = ref({})
const patientOptions = ref([])
const drugOptions = ref([])
const rxForm = ref({ patientId: undefined, rxType: '0', itemList: [] })

const data = reactive({ queryParams: { pageNum: 1, pageSize: 10, rxNo: undefined, status: undefined } })
const { queryParams } = toRefs(data)
const rxTotal = computed(() => (rxForm.value.itemList || []).reduce((s, i) => s + Number(i.amount || 0), 0).toFixed(2))

function getList() {
  loading.value = true
  listPrescription(queryParams.value).then(res => {
    rxList.value = res.data
    total.value = res.total
    loading.value = false
    res.data.forEach(r => listRxItems(r.rxId).then(x => itemMap.value[r.rxId] = x.data || []))
  })
}
function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm("queryRef"); handleQuery() }

function searchPatient(kw) {
  listPatient({ pageNum: 1, pageSize: 50, patientName: kw || undefined }).then(res => patientOptions.value = res.data)
}
function onDrugPick(row) {
  const d = drugOptions.value.find(x => x.drugId === row.drugId)
  if (d) { row.drugName = d.drugName; row.specification = d.specification; row.unit = d.unit; row.price = d.price }
  calcItem(row)
}
function calcItem(row) { row.amount = ((row.price || 0) * (row.quantity || 0)).toFixed(2) }

function handleAdd() {
  rxForm.value = { patientId: undefined, rxType: '0', itemList: [{ quantity: 1, frequency: '每日三次', days: 7 }] }
  searchPatient("")
  open.value = true
}

function submitRx() {
  if (!rxForm.value.patientId) { proxy.$modal.msgWarning("请选择患者"); return }
  if (!rxForm.value.itemList.length || rxForm.value.itemList.some(i => !i.drugId)) { proxy.$modal.msgWarning("请添加药品明细"); return }
  createPrescription(rxForm.value).then(() => { proxy.$modal.msgSuccess("处方已开立"); open.value = false; getList() })
}

function handleCancel(row) {
  proxy.$modal.confirm('确认作废该处方？已收费的将自动退费。').then(() => cancelRx(row.rxId)).then(() => { getList(); proxy.$modal.msgSuccess("已作废") }).catch(() => {})
}

listDrug({ pageNum: 1, pageSize: 500, status: '0' }).then(res => drugOptions.value = res.data)
listDoctor({ pageNum: 1, pageSize: 200 })
getList()
</script>

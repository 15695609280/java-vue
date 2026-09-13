<template>
   <div class="app-container">
      <el-row :gutter="16">
         <el-col :span="14">
            <el-card shadow="never" header="待发药处方(已收费)">
               <el-form :inline="true">
                  <el-form-item label="患者姓名">
                     <el-input v-model="kw" placeholder="模糊搜索" clearable style="width:160px" @keyup.enter="load" />
                  </el-form-item>
                  <el-form-item><el-button type="primary" icon="Refresh" @click="load">刷新</el-button></el-form-item>
               </el-form>
               <el-table :data="rxList" size="small" border v-loading="loading" highlight-current-row @current-change="onPick">
                  <el-table-column label="处方号" prop="rxNo" width="120" />
                  <el-table-column label="患者" prop="patientName" width="90" />
                  <el-table-column label="医生" prop="doctorName" width="90" />
                  <el-table-column label="金额" prop="totalAmount" width="90" />
                  <el-table-column label="开立时间" prop="createTime">
                     <template #default="s"><span>{{ parseTime(s.row.createTime) }}</span></template>
                  </el-table-column>
                  <el-table-column label="操作" width="90" fixed="right">
                     <template #default="s">
                        <el-button type="primary" size="small" @click.stop="doDispense(s.row)" v-hasPermi="['his:dispense:dispense']">发药</el-button>
                     </template>
                  </el-table-column>
               </el-table>
               <el-empty v-if="!loading && !rxList.length" description="无待发药处方" :image-size="60" />
            </el-card>
         </el-col>
         <el-col :span="10">
            <el-card shadow="never" header="处方明细核对">
               <template v-if="curRx">
                  <el-descriptions :column="2" border size="small" style="margin-bottom:10px">
                     <el-descriptions-item label="处方号">{{ curRx.rxNo }}</el-descriptions-item>
                     <el-descriptions-item label="患者">{{ curRx.patientName }}</el-descriptions-item>
                     <el-descriptions-item label="医生">{{ curRx.doctorName }}</el-descriptions-item>
                     <el-descriptions-item label="合计">¥{{ curRx.totalAmount }}</el-descriptions-item>
                  </el-descriptions>
                  <el-table :data="items" size="small" border>
                     <el-table-column prop="drugName" label="药品" />
                     <el-table-column prop="specification" label="规格" width="110" />
                     <el-table-column prop="quantity" label="数量" width="60" />
                     <el-table-column label="库存" width="90">
                        <template #default="s">
                           <el-tag :type="stockEnough(s.row) ? 'success' : 'danger'" size="small">{{ stockOf(s.row.drugId) }}</el-tag>
                        </template>
                     </el-table-column>
                     <el-table-column prop="usageDose" label="用法" width="100" />
                  </el-table>
                  <el-alert v-if="shortage" title="存在库存不足的药品，发药将失败" type="error" :closable="false" style="margin-top:10px" />
               </template>
               <el-empty v-else description="点击左侧处方查看明细" :image-size="60" />
            </el-card>
         </el-col>
      </el-row>
   </div>
</template>

<script setup name="HisDispense">
import { listPaidRx, dispenseRx, listRxItems } from "@/api/his/prescription"
import { listDrug } from "@/api/his/drug"

const { proxy } = getCurrentInstance()
const rxList = ref([])
const loading = ref(false)
const kw = ref("")
const curRx = ref()
const items = ref([])
const drugMap = ref({})
const shortage = computed(() => items.value.some(i => !stockEnough(i)))

function stockOf(drugId) { return (drugMap.value[drugId] || {}).stock ?? '-' }
function stockEnough(item) { const d = drugMap.value[item.drugId]; return !d || d.stock >= item.quantity }

function load() {
  loading.value = true
  listPaidRx({ pageNum: 1, pageSize: 100 }).then(res => {
    rxList.value = kw.value ? res.data.filter(r => (r.patientName || '').includes(kw.value)) : res.data
    loading.value = false
  })
  listDrug({ pageNum: 1, pageSize: 500 }).then(res => {
    res.data.forEach(d => drugMap.value[d.drugId] = d)
  })
}

function onPick(row) {
  curRx.value = row
  if (row) listRxItems(row.rxId).then(res => items.value = res.data || [])
}

function doDispense(row) {
  proxy.$modal.confirm(`确认发放处方 ${row.rxNo} 的药品？将扣减对应库存。`).then(() => dispenseRx(row.rxId)).then(() => {
    proxy.$modal.msgSuccess("发药完成，库存已扣减")
    curRx.value = null
    items.value = []
    load()
  }).catch(() => {})
}

load()
</script>

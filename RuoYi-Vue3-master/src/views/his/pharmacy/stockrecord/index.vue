<template>
   <div class="app-container">
      <!-- 台账筛选条 -->
      <el-card shadow="never" style="margin-bottom:12px">
         <div class="filterbar">
            <el-radio-group v-model="queryParams.recordType" @change="handleQuery">
               <el-radio-button value="">全部</el-radio-button>
               <el-radio-button v-for="d in his_stock_type" :key="d.value" :value="d.value">{{ d.label }}</el-radio-button>
            </el-radio-group>
            <div class="right">
               <el-input v-model="queryParams.recordNo" placeholder="单据号" clearable style="width: 150px" @keyup.enter="handleQuery" />
               <el-select v-model="queryParams.drugId" placeholder="药品" clearable filterable style="width: 170px">
                  <el-option v-for="d in drugOptions" :key="d.drugId" :label="d.drugName" :value="d.drugId" />
               </el-select>
               <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
               <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['his:stockrecord:export']">导出</el-button>
            </div>
         </div>
      </el-card>

      <!-- 汇总条 -->
      <div class="summary">
         <span>本页 <b>{{ stockRecordList.length }}</b> 条流水</span>
         <span>入库合计 <b class="in">+{{ inSum }}</b></span>
         <span>出库合计 <b class="out">-{{ outSum }}</b></span>
      </div>

      <!-- 台账流水表: 台账只读, 不允许增删改 -->
      <el-table v-loading="loading" :data="stockRecordList" border>
         <el-table-column label="单据号" align="center" prop="recordNo" width="150" />
         <el-table-column label="变动类型" align="center" width="110">
            <template #default="s"><dict-tag :options="his_stock_type" :value="s.row.recordType" /></template>
         </el-table-column>
         <el-table-column label="方向" align="center" width="80">
            <template #default="s">
               <el-tag :type="isIn(s.row.recordType) ? 'success' : 'danger'" effect="plain" size="small">
                  {{ isIn(s.row.recordType) ? '入库' : '出库' }}
               </el-tag>
            </template>
         </el-table-column>
         <el-table-column label="药品" prop="drugName" min-width="150" :show-overflow-tooltip="true" />
         <el-table-column label="规格" align="center" prop="specification" width="130" />
         <el-table-column label="变动数量" align="center" width="100">
            <template #default="s">
               <b :style="{ color: isIn(s.row.recordType) ? '#67c23a' : '#f56c6c' }">
                  {{ isIn(s.row.recordType) ? '+' : '-' }}{{ s.row.quantity }}
               </b>
            </template>
         </el-table-column>
         <el-table-column label="变动前" align="center" prop="beforeStock" width="90" />
         <el-table-column label="变动后" align="center" prop="afterStock" width="90" />
         <el-table-column label="经办人" align="center" prop="operator" width="100" />
         <el-table-column label="时间" align="center" prop="createTime" width="160">
            <template #default="s"><span>{{ parseTime(s.row.createTime) }}</span></template>
         </el-table-column>
         <el-table-column label="备注" prop="remark" :show-overflow-tooltip="true" min-width="120" />
      </el-table>

      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
   </div>
</template>

<script setup name="Stockrecord">
import { listStockRecord } from "@/api/his/stockrecord"
import { listDrug } from "@/api/his/drug"

const { proxy } = getCurrentInstance()
const { his_stock_type } = useDict("his_stock_type")

const stockRecordList = ref([])
const drugOptions = ref([])
const loading = ref(true)
const total = ref(0)

const queryParams = reactive({ pageNum: 1, pageSize: 20, recordNo: undefined, recordType: "", drugId: undefined })

/** 入库类型: 0采购入库 2退药入库 3盘盈; 出库: 1发药出库 4盘亏 */
const IN_TYPES = ["0", "2", "3"]
function isIn(t) { return IN_TYPES.includes(t) }

const inSum = computed(() => stockRecordList.value.filter(r => isIn(r.recordType)).reduce((s, r) => s + Number(r.quantity), 0))
const outSum = computed(() => stockRecordList.value.filter(r => !isIn(r.recordType)).reduce((s, r) => s + Number(r.quantity), 0))

function getList() {
   loading.value = true
   listStockRecord(queryParams).then(res => { stockRecordList.value = res.data; total.value = res.total; loading.value = false })
}
function handleQuery() { queryParams.pageNum = 1; getList() }
function handleExport() {
   proxy.download("his/stockrecord/export", { ...queryParams }, "stockrecord_" + new Date().getTime() + ".xlsx")
}

listDrug({ pageNum: 1, pageSize: 500 }).then(res => drugOptions.value = res.data)
getList()
</script>

<style scoped>
.filterbar { display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 10px; }
.filterbar .right { display: flex; gap: 10px; }
.summary { display: flex; gap: 24px; font-size: 13px; color: #606266; margin-bottom: 10px; padding: 0 4px; }
.summary b { font-size: 15px; }
.summary .in { color: #67c23a; }
.summary .out { color: #f56c6c; }
</style>

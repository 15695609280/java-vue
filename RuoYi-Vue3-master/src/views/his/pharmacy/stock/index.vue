<template>
   <div class="app-container">
      <!-- 库存看板 -->
      <el-row :gutter="12" style="margin-bottom:14px">
         <el-col :span="6"><el-card shadow="hover"><div class="stat"><div class="num">{{ drugList.length }}</div><div class="lbl">药品种类</div></div></el-card></el-col>
         <el-col :span="6"><el-card shadow="hover"><div class="stat"><div class="num warn">{{ lowStock.length }}</div><div class="lbl">低库存预警</div></div></el-card></el-col>
         <el-col :span="6"><el-card shadow="hover"><div class="stat"><div class="num">{{ totalStock }}</div><div class="lbl">库存总量</div></div></el-card></el-col>
         <el-col :span="6"><el-card shadow="hover"><div class="stat"><div class="num">¥{{ stockValue }}</div><div class="lbl">库存货值</div></div></el-card></el-col>
      </el-row>

      <el-form :inline="true">
         <el-form-item label="药品名称">
            <el-input v-model="kw" placeholder="模糊搜索" clearable style="width: 160px" />
         </el-form-item>
         <el-form-item label="分类">
            <el-select v-model="cat" clearable placeholder="全部" style="width: 130px">
               <el-option v-for="d in his_drug_category" :key="d.value" :label="d.label" :value="d.value" />
            </el-select>
         </el-form-item>
         <el-form-item>
            <el-checkbox v-model="onlyLow">仅看低库存</el-checkbox>
         </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="filtered" border @row-click="pick">
         <el-table-column label="药品编码" align="center" prop="drugCode" width="110" />
         <el-table-column label="药品名称" prop="drugName" min-width="150" />
         <el-table-column label="分类" align="center" width="100">
            <template #default="s"><dict-tag :options="his_drug_category" :value="s.row.category" /></template>
         </el-table-column>
         <el-table-column label="规格" align="center" prop="specification" width="130" />
         <el-table-column label="零售价" align="center" prop="price" width="90" />
         <el-table-column label="库存" align="center" width="130">
            <template #default="s">
               <el-tag :type="s.row.stock <= (s.row.stockWarn || 50) ? 'danger' : 'success'">{{ s.row.stock }}</el-tag>
               <span style="font-size:12px;color:#909399"> / 下限{{ s.row.stockWarn || 50 }}</span>
            </template>
         </el-table-column>
         <el-table-column label="库存状态" align="center" width="150">
            <template #default="s">
               <el-progress :percentage="Math.min(100, Math.round(s.row.stock / Math.max(s.row.stockWarn || 50, 1) * 20))" :status="s.row.stock <= (s.row.stockWarn || 50) ? 'exception' : ''" :stroke-width="10" />
            </template>
         </el-table-column>
      </el-table>

      <!-- 药品流水抽屉 -->
      <el-drawer v-model="open" :title="`出入库流水 - ${cur.drugName || ''}`" size="46%">
         <el-table :data="records" size="small" border v-loading="recLoading">
            <el-table-column label="单据号" prop="recordNo" width="130" />
            <el-table-column label="类型" width="90">
               <template #default="s"><dict-tag :options="his_stock_type" :value="s.row.recordType" /></template>
            </el-table-column>
            <el-table-column label="数量" prop="quantity" width="70" />
            <el-table-column label="变动前" prop="beforeStock" width="80" />
            <el-table-column label="变动后" prop="afterStock" width="80" />
            <el-table-column label="经办人" prop="operator" width="90" />
            <el-table-column label="时间" prop="createTime">
               <template #default="s"><span>{{ parseTime(s.row.createTime) }}</span></template>
            </el-table-column>
         </el-table>
      </el-drawer>
   </div>
</template>

<script setup name="HisStock">
import { listDrug } from "@/api/his/drug"
import { listStockRecord } from "@/api/his/stockrecord"

const { his_drug_category, his_stock_type } = useDict("his_drug_category", "his_stock_type")

const drugList = ref([])
const loading = ref(false)
const kw = ref("")
const cat = ref()
const onlyLow = ref(false)
const open = ref(false)
const cur = ref({})
const records = ref([])
const recLoading = ref(false)

const lowStock = computed(() => drugList.value.filter(d => d.stock <= (d.stockWarn || 50)))
const totalStock = computed(() => drugList.value.reduce((s, d) => s + (d.stock || 0), 0))
const stockValue = computed(() => drugList.value.reduce((s, d) => s + (d.stock || 0) * (d.price || 0), 0).toFixed(2))

const filtered = computed(() => drugList.value.filter(d => {
  if (kw.value && !(d.drugName || '').includes(kw.value)) return false
  if (cat.value && d.category !== cat.value) return false
  if (onlyLow.value && d.stock > (d.stockWarn || 50)) return false
  return true
}))

function load() {
  loading.value = true
  listDrug({ pageNum: 1, pageSize: 500 }).then(res => { drugList.value = res.data; loading.value = false })
}

function pick(row) {
  cur.value = row
  open.value = true
  recLoading.value = true
  listStockRecord({ pageNum: 1, pageSize: 100, drugId: row.drugId }).then(res => { records.value = res.data; recLoading.value = false })
}

load()
</script>

<style scoped>
.stat { text-align: center; padding: 8px 0; }
.num { font-size: 26px; font-weight: bold; color: #409eff; }
.num.warn { color: #f56c6c; }
.lbl { font-size: 13px; color: #909399; margin-top: 4px; }
</style>

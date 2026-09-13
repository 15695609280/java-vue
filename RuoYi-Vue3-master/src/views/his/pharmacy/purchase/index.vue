<template>
   <div class="app-container">
      <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
         <el-form-item label="采购单号" prop="purchaseNo">
            <el-input v-model="queryParams.purchaseNo" placeholder="请输入" clearable style="width: 160px" @keyup.enter="handleQuery" />
         </el-form-item>
         <el-form-item label="状态" prop="status">
            <el-select v-model="queryParams.status" clearable placeholder="状态" style="width: 120px">
               <el-option v-for="d in his_purchase_status" :key="d.value" :label="d.label" :value="d.value" />
            </el-select>
         </el-form-item>
         <el-form-item>
            <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
            <el-button icon="Refresh" @click="resetQuery">重置</el-button>
         </el-form-item>
      </el-form>

      <el-row :gutter="10" class="mb8">
         <el-col :span="1.5">
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['his:purchase:add']">新建采购单</el-button>
         </el-col>
         <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <!-- 低库存预警 -->
      <el-alert v-if="lowStock.length" type="warning" :closable="false" style="margin-bottom:12px">
         <template #title>
            低库存预警: {{ lowStock.map(d => d.drugName).join('、') }} 等 {{ lowStock.length }} 种药品库存不足，请及时采购
         </template>
      </el-alert>

      <el-table v-loading="loading" :data="rows" border>
         <el-table-column type="expand">
            <template #default="s">
               <el-table :data="itemMap[s.row.purchaseId] || []" size="small" border style="margin:0 20px">
                  <el-table-column label="药品" prop="drugName" />
                  <el-table-column label="规格" prop="specification" width="140" />
                  <el-table-column label="数量" prop="quantity" width="80" />
                  <el-table-column label="单价" prop="price" width="90" />
                  <el-table-column label="金额" prop="amount" width="100" />
               </el-table>
            </template>
         </el-table-column>
         <el-table-column label="采购单号" align="center" prop="purchaseNo" width="140" />
         <el-table-column label="供应商" prop="supplierName" min-width="160" />
         <el-table-column label="合计金额" align="center" prop="totalAmount" width="110" />
         <el-table-column label="状态" align="center" width="90">
            <template #default="s"><dict-tag :options="his_purchase_status" :value="s.row.status" /></template>
         </el-table-column>
         <el-table-column label="创建时间" align="center" prop="createTime" width="150">
            <template #default="s"><span>{{ parseTime(s.row.createTime) }}</span></template>
         </el-table-column>
         <el-table-column label="入库时间" align="center" prop="inTime" width="150">
            <template #default="s"><span>{{ parseTime(s.row.inTime) }}</span></template>
         </el-table-column>
         <el-table-column label="操作" width="150" align="center" fixed="right">
            <template #default="scope">
               <el-button v-if="scope.row.status === '0'" link type="success" @click="doInbound(scope.row)" v-hasPermi="['his:purchase:inbound']">确认入库</el-button>
               <el-button v-if="scope.row.status === '0'" link type="danger" @click="doCancel(scope.row)" v-hasPermi="['his:purchase:inbound']">取消</el-button>
            </template>
         </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

      <!-- 新建采购单: 供应商 + 明细行编辑 -->
      <el-dialog title="新建采购单" v-model="open" width="820px" append-to-body>
         <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
            <el-form-item label="供应商" prop="supplierId">
               <el-select v-model="form.supplierId" filterable style="width:320px">
                  <el-option v-for="s in supplierOptions" :key="s.supplierId" :label="s.supplierName" :value="s.supplierId" />
               </el-select>
            </el-form-item>
         </el-form>
         <div style="display:flex;justify-content:space-between;margin-bottom:8px">
            <b>采购明细</b>
            <el-button size="small" icon="Plus" @click="form.itemList.push({ drugId: undefined, quantity: 1, price: 0 })">添加药品</el-button>
         </div>
         <el-table :data="form.itemList" size="small" border>
            <el-table-column label="药品" min-width="240">
               <template #default="s">
                  <el-select v-model="s.row.drugId" filterable size="small" placeholder="选择药品" style="width:100%" @change="onDrugPick(s.row)">
                     <el-option v-for="d in drugOptions" :key="d.drugId" :label="`${d.drugName} ${d.specification} (现库存${d.stock})`" :value="d.drugId" />
                  </el-select>
               </template>
            </el-table-column>
            <el-table-column label="数量" width="120">
               <template #default="s"><el-input-number v-model="s.row.quantity" :min="1" size="small" controls-position="right" style="width:100%" /></template>
            </el-table-column>
            <el-table-column label="采购单价" width="130">
               <template #default="s"><el-input-number v-model="s.row.price" :min="0" :precision="2" size="small" controls-position="right" style="width:100%" /></template>
            </el-table-column>
            <el-table-column label="金额" width="100" align="right">
               <template #default="s">¥{{ (s.row.quantity * s.row.price).toFixed(2) }}</template>
            </el-table-column>
            <el-table-column width="60" align="center">
               <template #default="s"><el-button link type="danger" icon="Delete" @click="form.itemList.splice(s.$index, 1)" /></template>
            </el-table-column>
         </el-table>
         <div style="text-align:right;margin-top:10px;font-size:15px">合计: <b style="color:#f56c6c;font-size:18px">¥{{ totalAmount }}</b></div>
         <template #footer>
            <el-button type="primary" @click="submit">创建采购单</el-button>
            <el-button @click="open = false">取 消</el-button>
         </template>
      </el-dialog>
   </div>
</template>

<script setup name="HisPurchase">
import { listPurchase, createPurchase, inboundPurchase, updatePurchase, listPurchaseItem } from "@/api/his/purchase"
import { listSupplier } from "@/api/his/supplier"
import { listDrug } from "@/api/his/drug"

const { proxy } = getCurrentInstance()
const { his_purchase_status } = useDict("his_purchase_status")

const rows = ref([])
const loading = ref(true)
const showSearch = ref(true)
const total = ref(0)
const open = ref(false)
const form = ref({ itemList: [] })
const supplierOptions = ref([]), drugOptions = ref([])
const itemMap = ref({})
const lowStock = ref([])

const data = reactive({ queryParams: { pageNum: 1, pageSize: 10, purchaseNo: undefined, status: undefined } })
const { queryParams } = toRefs(data)
const rules = { supplierId: [{ required: true, message: "请选择供应商", trigger: "change" }] }

const totalAmount = computed(() => form.value.itemList.reduce((s, i) => s + i.quantity * i.price, 0).toFixed(2))

function getList() {
  loading.value = true
  listPurchase(queryParams.value).then(res => {
    rows.value = res.data; total.value = res.total; loading.value = false
    res.data.forEach(r => listPurchaseItem(r.purchaseId).then(ir => itemMap.value[r.purchaseId] = ir.data || []))
  })
}
function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm("queryRef"); handleQuery() }

function handleAdd() {
  form.value = { supplierId: undefined, itemList: [{ drugId: undefined, quantity: 1, price: 0 }] }
  open.value = true
}

function onDrugPick(item) {
  const d = drugOptions.value.find(x => x.drugId === item.drugId)
  if (d && !item.price) item.price = d.price || 0
}

function submit() {
  proxy.$refs["formRef"].validate(valid => {
    if (!valid) return
    const itemList = form.value.itemList.filter(i => i.drugId && i.quantity > 0)
    if (!itemList.length) return proxy.$modal.msgWarning("请至少添加一条采购明细")
    createPurchase({ supplierId: form.value.supplierId, itemList }).then(() => { proxy.$modal.msgSuccess("采购单已创建"); open.value = false; getList() })
  })
}

function doInbound(row) {
  proxy.$modal.confirm(`确认入库采购单 ${row.purchaseNo}？将自动增加药品库存并记录流水。`).then(() => inboundPurchase(row.purchaseId)).then(() => { proxy.$modal.msgSuccess("入库完成"); getList(); loadLowStock() }).catch(() => {})
}
function doCancel(row) {
  proxy.$modal.confirm('确认取消该采购单？').then(() => updatePurchase({ purchaseId: row.purchaseId, status: '2' })).then(() => { proxy.$modal.msgSuccess("已取消"); getList() }).catch(() => {})
}

function loadLowStock() {
  listDrug({ pageNum: 1, pageSize: 500 }).then(res => {
    lowStock.value = res.data.filter(d => d.stock <= (d.stockWarn || 50))
  })
}

listSupplier({ pageNum: 1, pageSize: 100 }).then(res => supplierOptions.value = res.data)
listDrug({ pageNum: 1, pageSize: 500 }).then(res => drugOptions.value = res.data)
loadLowStock()
getList()
</script>

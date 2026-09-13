<template>
   <div class="app-container">
      <!-- 统计头 -->
      <el-row :gutter="12" class="stat-row">
         <el-col :span="4"><div class="stat"><b>{{ allDrugs.length }}</b><span>药品总数</span></div></el-col>
         <el-col :span="4"><div class="stat warn"><b>{{ lowCount }}</b><span>库存预警</span></div></el-col>
         <el-col :span="4"><div class="stat"><b>{{ rxCount }}</b><span>处方药</span></div></el-col>
         <el-col :span="4"><div class="stat"><b>¥{{ totalValue }}</b><span>库存总值</span></div></el-col>
         <el-col :span="8" style="display:flex;align-items:center;justify-content:flex-end;gap:10px">
            <el-input v-model="kw" placeholder="搜索编码/名称/厂家" clearable prefix-icon="Search" style="width: 220px" />
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['his:drug:add']">新增药品</el-button>
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['his:drug:export']">导出</el-button>
         </el-col>
      </el-row>

      <!-- 分类 tabs -->
      <el-tabs v-model="cat" class="cat-tabs">
         <el-tab-pane label="全部药品" name="" />
         <el-tab-pane label="库存预警" name="__low">
            <template #label><el-badge :value="lowCount" :hidden="!lowCount" type="danger">库存预警</el-badge></template>
         </el-tab-pane>
         <el-tab-pane v-for="d in his_drug_category" :key="d.value" :label="d.label" :name="d.value" />
      </el-tabs>

      <el-table v-loading="loading" :data="filtered" border :row-class-name="rowCls">
         <el-table-column label="药品编码" align="center" prop="drugCode" width="110" />
         <el-table-column label="药品名称" prop="drugName" min-width="150" :show-overflow-tooltip="true">
            <template #default="s">
               {{ s.row.drugName }}
               <el-tag v-if="s.row.isPrescription === 'Y'" size="small" type="danger" effect="plain" style="margin-left:4px">Rx</el-tag>
            </template>
         </el-table-column>
         <el-table-column label="分类" align="center" width="100">
            <template #default="s"><dict-tag :options="his_drug_category" :value="s.row.category" /></template>
         </el-table-column>
         <el-table-column label="规格" align="center" prop="specification" width="130" />
         <el-table-column label="单价" align="center" width="90">
            <template #default="s"><span style="color:#f56c6c;font-weight:600">¥{{ s.row.price }}</span></template>
         </el-table-column>
         <el-table-column label="库存" min-width="170">
            <template #default="s">
               <div class="stock-cell">
                  <el-progress
                     :percentage="stockPct(s.row)"
                     :status="s.row.stock <= s.row.stockWarn ? 'exception' : 'success'"
                     :stroke-width="10" style="flex:1" />
                  <span class="stock-num" :class="{ low: s.row.stock <= s.row.stockWarn }">{{ s.row.stock }} {{ s.row.unit }}</span>
               </div>
            </template>
         </el-table-column>
         <el-table-column label="预警线" align="center" prop="stockWarn" width="80" />
         <el-table-column label="生产厂家" prop="manufacturer" min-width="140" :show-overflow-tooltip="true" />
         <el-table-column label="状态" align="center" width="80">
            <template #default="s"><dict-tag :options="sys_normal_disable" :value="s.row.status" /></template>
         </el-table-column>
         <el-table-column label="操作" width="130" align="center" fixed="right">
            <template #default="scope">
               <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['his:drug:edit']">修改</el-button>
               <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['his:drug:remove']">删除</el-button>
            </template>
         </el-table-column>
      </el-table>

      <!-- 添加或修改药品对话框 -->
      <el-dialog :title="title" v-model="open" width="640px" append-to-body>
         <el-form ref="drugRef" :model="form" :rules="rules" label-width="110px">
            <el-row :gutter="12">
               <el-col :span="12"><el-form-item label="药品编码" prop="drugCode"><el-input v-model="form.drugCode" placeholder="请输入药品编码" /></el-form-item></el-col>
               <el-col :span="12"><el-form-item label="药品名称" prop="drugName"><el-input v-model="form.drugName" placeholder="请输入药品名称" /></el-form-item></el-col>
               <el-col :span="12">
                  <el-form-item label="药品分类" prop="category">
                     <el-select v-model="form.category" style="width:100%">
                        <el-option v-for="dict in his_drug_category" :key="dict.value" :label="dict.label" :value="dict.value" />
                     </el-select>
                  </el-form-item>
               </el-col>
               <el-col :span="12"><el-form-item label="规格" prop="specification"><el-input v-model="form.specification" placeholder="如 0.25g*24粒" /></el-form-item></el-col>
               <el-col :span="12"><el-form-item label="单位" prop="unit"><el-input v-model="form.unit" placeholder="盒/瓶/支" /></el-form-item></el-col>
               <el-col :span="12"><el-form-item label="单价(元)" prop="price"><el-input-number v-model="form.price" :precision="2" :min="0" controls-position="right" style="width: 100%" /></el-form-item></el-col>
               <el-col :span="12"><el-form-item label="库存" prop="stock"><el-input-number v-model="form.stock" :min="0" controls-position="right" style="width: 100%" /></el-form-item></el-col>
               <el-col :span="12"><el-form-item label="预警库存" prop="stockWarn"><el-input-number v-model="form.stockWarn" :min="0" controls-position="right" style="width: 100%" /></el-form-item></el-col>
               <el-col :span="12"><el-form-item label="生产厂家" prop="manufacturer"><el-input v-model="form.manufacturer" /></el-form-item></el-col>
               <el-col :span="12"><el-form-item label="批准文号" prop="approvalNo"><el-input v-model="form.approvalNo" /></el-form-item></el-col>
               <el-col :span="12">
                  <el-form-item label="处方药" prop="isPrescription">
                     <el-radio-group v-model="form.isPrescription">
                        <el-radio v-for="dict in sys_yes_no" :key="dict.value" :value="dict.value">{{ dict.label }}</el-radio>
                     </el-radio-group>
                  </el-form-item>
               </el-col>
               <el-col :span="12">
                  <el-form-item label="状态" prop="status">
                     <el-radio-group v-model="form.status">
                        <el-radio v-for="dict in sys_normal_disable" :key="dict.value" :value="dict.value">{{ dict.label }}</el-radio>
                     </el-radio-group>
                  </el-form-item>
               </el-col>
               <el-col :span="24"><el-form-item label="备注" prop="remark"><el-input v-model="form.remark" type="textarea" /></el-form-item></el-col>
            </el-row>
         </el-form>
         <template #footer>
            <div class="dialog-footer">
               <el-button type="primary" @click="submitForm">确 定</el-button>
               <el-button @click="cancel">取 消</el-button>
            </div>
         </template>
      </el-dialog>
   </div>
</template>

<script setup name="Drug">
import { listDrug, addDrug, delDrug, getDrug, updateDrug } from "@/api/his/drug"

const { proxy } = getCurrentInstance()
const { his_drug_category, sys_yes_no, sys_normal_disable } = useDict("his_drug_category", "sys_yes_no", "sys_normal_disable")

const allDrugs = ref([])
const cat = ref("")
const kw = ref("")
const open = ref(false)
const loading = ref(true)
const title = ref("")

const data = reactive({
   form: {},
   rules: {
      drugCode: [{ required: true, message: "药品编码不能为空", trigger: "blur" }],
      drugName: [{ required: true, message: "药品名称不能为空", trigger: "blur" }],
      category: [{ required: true, message: "药品分类不能为空", trigger: "change" }],
      price: [{ required: true, message: "单价(元)不能为空", trigger: "blur" }],
      stock: [{ required: true, message: "库存不能为空", trigger: "blur" }],
   }
})
const { form, rules } = toRefs(data)

const isLow = d => d.stock <= (d.stockWarn ?? 0)
const lowCount = computed(() => allDrugs.value.filter(isLow).length)
const rxCount = computed(() => allDrugs.value.filter(d => d.isPrescription === "Y").length)
const totalValue = computed(() => allDrugs.value.reduce((s, d) => s + Number(d.price) * Number(d.stock), 0).toLocaleString())

const filtered = computed(() => allDrugs.value.filter(d => {
   if (cat.value === "__low" && !isLow(d)) return false
   if (cat.value && cat.value !== "__low" && d.category !== cat.value) return false
   if (kw.value && !(d.drugName?.includes(kw.value) || d.drugCode?.includes(kw.value) || d.manufacturer?.includes(kw.value))) return false
   return true
}))

function stockPct(d) {
   const base = Math.max(d.stockWarn * 3, 1)
   return Math.min(100, Math.round(d.stock / base * 100))
}
function rowCls({ row }) { return isLow(row) ? "low-row" : "" }

function getList() {
   loading.value = true
   listDrug({ pageNum: 1, pageSize: 1000 }).then(res => { allDrugs.value = res.data; loading.value = false })
}

function cancel() { open.value = false; reset() }
function reset() {
   form.value = { drugId: undefined, drugCode: undefined, drugName: undefined, category: "6", specification: undefined, unit: "盒", price: undefined, stock: 0, stockWarn: 50, manufacturer: undefined, approvalNo: undefined, isPrescription: "N", status: "0", remark: undefined }
   proxy.resetForm("drugRef")
}
function handleAdd() { reset(); open.value = true; title.value = "添加药品" }
function handleUpdate(row) {
   reset()
   getDrug(row.drugId).then(res => { form.value = res.data; open.value = true; title.value = "修改药品" })
}
function submitForm() {
   proxy.$refs["drugRef"].validate(valid => {
      if (!valid) return
      const fn = form.value.drugId ? updateDrug : addDrug
      fn(form.value).then(() => { proxy.$modal.msgSuccess("保存成功"); open.value = false; getList() })
   })
}
function handleDelete(row) {
   proxy.$modal.confirm('是否确认删除药品"' + row.drugName + '"？').then(() => delDrug(row.drugId)).then(() => { getList(); proxy.$modal.msgSuccess("删除成功") }).catch(() => {})
}
function handleExport() {
   proxy.download("his/drug/export", { drugName: kw.value }, "drug_" + new Date().getTime() + ".xlsx")
}

getList()
</script>

<style scoped>
.stat-row { margin-bottom: 6px; }
.stat { background: #fff; border: 1px solid #e4e7ed; border-radius: 8px; padding: 14px; text-align: center; }
.stat b { display: block; font-size: 22px; color: #409eff; }
.stat.warn b { color: #f56c6c; }
.stat span { font-size: 12px; color: #909399; }
.cat-tabs { margin-bottom: 4px; }
.stock-cell { display: flex; align-items: center; gap: 8px; }
.stock-num { font-size: 12px; white-space: nowrap; }
.stock-num.low { color: #f56c6c; font-weight: 700; }
:deep(.low-row) { background: #fef0f0; }
:deep(.low-row:hover > td) { background: #fde2e2 !important; }
</style>

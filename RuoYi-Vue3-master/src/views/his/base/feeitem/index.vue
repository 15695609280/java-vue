<template>
   <div class="app-container">
      <div class="topbar">
         <el-radio-group v-model="cat">
            <el-radio-button value="">全部类别</el-radio-button>
            <el-radio-button v-for="d in his_fee_category" :key="d.value" :value="d.value">{{ d.label }}</el-radio-button>
         </el-radio-group>
         <div class="right">
            <el-input v-model="kw" placeholder="搜索项目名称" clearable prefix-icon="Search" style="width: 180px" />
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['his:feeitem:add']">新增项目</el-button>
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['his:feeitem:export']">导出</el-button>
         </div>
      </div>

      <div v-loading="loading">
         <el-card v-for="g in groups" :key="g.value" shadow="never" class="grp" :header="`${g.label} (${g.items.length}项)`">
            <el-table :data="g.items" size="small" :show-header="false">
               <el-table-column prop="itemName" min-width="220">
                  <template #default="s">
                     <b>{{ s.row.itemName }}</b>
                     <el-tag v-if="s.row.status === '1'" size="small" type="info" style="margin-left:6px">停用</el-tag>
                  </template>
               </el-table-column>
               <el-table-column label="单位" align="center" width="100">
                  <template #default="s"><span style="color:#909399">{{ s.row.unit }}</span></template>
               </el-table-column>
               <el-table-column label="单价" align="right" width="140">
                  <template #default="s"><span class="price">¥{{ Number(s.row.price).toFixed(2) }}</span></template>
               </el-table-column>
               <el-table-column label="备注" prop="remark" min-width="140" :show-overflow-tooltip="true">
                  <template #default="s"><span style="color:#909399;font-size:12px">{{ s.row.remark || '—' }}</span></template>
               </el-table-column>
               <el-table-column width="110" align="center">
                  <template #default="s">
                     <el-button link type="primary" icon="Edit" @click="handleUpdate(s.row)" v-hasPermi="['his:feeitem:edit']" />
                     <el-button link type="danger" icon="Delete" @click="handleDelete(s.row)" v-hasPermi="['his:feeitem:remove']" />
                  </template>
               </el-table-column>
            </el-table>
         </el-card>
         <el-empty v-if="!loading && !groups.length" description="暂无收费项目" :image-size="90" />
      </div>

      <!-- 添加或修改收费项目对话框 -->
      <el-dialog :title="title" v-model="open" width="560px" append-to-body>
         <el-form ref="feeItemRef" :model="form" :rules="rules" label-width="100px">
            <el-form-item label="项目名称" prop="itemName"><el-input v-model="form.itemName" /></el-form-item>
            <el-form-item label="项目类别" prop="category">
               <el-select v-model="form.category" style="width:100%">
                  <el-option v-for="dict in his_fee_category" :key="dict.value" :label="dict.label" :value="dict.value" />
               </el-select>
            </el-form-item>
            <el-form-item label="单价(元)" prop="price"><el-input-number v-model="form.price" :precision="2" :min="0" controls-position="right" style="width:100%" /></el-form-item>
            <el-form-item label="单位" prop="unit"><el-input v-model="form.unit" placeholder="次/天/项" /></el-form-item>
            <el-form-item label="状态" prop="status">
               <el-radio-group v-model="form.status">
                  <el-radio v-for="dict in sys_normal_disable" :key="dict.value" :value="dict.value">{{ dict.label }}</el-radio>
               </el-radio-group>
            </el-form-item>
            <el-form-item label="备注" prop="remark"><el-input v-model="form.remark" type="textarea" /></el-form-item>
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

<script setup name="Feeitem">
import { listFeeItem, addFeeItem, delFeeItem, getFeeItem, updateFeeItem } from "@/api/his/feeitem"

const { proxy } = getCurrentInstance()
const { his_fee_category, sys_normal_disable } = useDict("his_fee_category", "sys_normal_disable")

const allItems = ref([])
const cat = ref("")
const kw = ref("")
const open = ref(false)
const loading = ref(true)
const title = ref("")

const data = reactive({
   form: {},
   rules: {
      itemName: [{ required: true, message: "项目名称不能为空", trigger: "blur" }],
      category: [{ required: true, message: "项目类别不能为空", trigger: "change" }],
      price: [{ required: true, message: "单价(元)不能为空", trigger: "blur" }],
   }
})
const { form, rules } = toRefs(data)

const filtered = computed(() => allItems.value.filter(i =>
   (!cat.value || i.category === cat.value) &&
   (!kw.value || i.itemName?.includes(kw.value))
))

const groups = computed(() => {
   const cats = cat.value ? his_fee_category.value.filter(c => c.value === cat.value) : his_fee_category.value
   return cats.map(c => ({
      value: c.value, label: c.label,
      items: filtered.value.filter(i => i.category === c.value).sort((a, b) => b.price - a.price)
   })).filter(g => g.items.length)
})

function getList() {
   loading.value = true
   listFeeItem({ pageNum: 1, pageSize: 1000 }).then(res => { allItems.value = res.data; loading.value = false })
}
function cancel() { open.value = false; reset() }
function reset() {
   form.value = { itemId: undefined, itemName: undefined, category: "6", price: undefined, unit: "次", status: "0", remark: undefined }
   proxy.resetForm("feeItemRef")
}
function handleAdd() { reset(); open.value = true; title.value = "添加收费项目" }
function handleUpdate(row) {
   reset()
   getFeeItem(row.itemId).then(res => { form.value = res.data; open.value = true; title.value = "修改收费项目" })
}
function submitForm() {
   proxy.$refs["feeItemRef"].validate(valid => {
      if (!valid) return
      const fn = form.value.itemId ? updateFeeItem : addFeeItem
      fn(form.value).then(() => { proxy.$modal.msgSuccess("保存成功"); open.value = false; getList() })
   })
}
function handleDelete(row) {
   proxy.$modal.confirm('是否确认删除项目"' + row.itemName + '"？').then(() => delFeeItem(row.itemId)).then(() => { getList(); proxy.$modal.msgSuccess("删除成功") }).catch(() => {})
}
function handleExport() {
   proxy.download("his/feeitem/export", { itemName: kw.value }, "feeitem_" + new Date().getTime() + ".xlsx")
}

getList()
</script>

<style scoped>
.topbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px; flex-wrap: wrap; gap: 10px; }
.topbar .right { display: flex; gap: 10px; }
.grp { margin-bottom: 12px; }
.price { color: #f56c6c; font-weight: 700; font-size: 15px; }
</style>

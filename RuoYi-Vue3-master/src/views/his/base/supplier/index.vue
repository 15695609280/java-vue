<template>
   <div class="app-container">
      <div class="topbar">
         <el-input v-model="kw" placeholder="搜索供应商名称" clearable prefix-icon="Search" style="width: 220px" />
         <div>
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['his:supplier:add']">新增供应商</el-button>
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['his:supplier:export']">导出</el-button>
         </div>
      </div>

      <div v-loading="loading" class="sup-grid">
         <div v-for="s in filtered" :key="s.supplierId" class="sup-card" :class="{ disabled: s.status === '1' }">
            <div class="s-head">
               <div class="s-icon"><el-icon :size="22"><OfficeBuilding /></el-icon></div>
               <div>
                  <div class="s-name">{{ s.supplierName }}</div>
                  <el-tag size="small" :type="s.status === '0' ? 'success' : 'info'" effect="plain">{{ s.status === '0' ? '合作中' : '已停用' }}</el-tag>
               </div>
            </div>
            <div class="s-body">
               <div class="line"><el-icon><User /></el-icon>{{ s.contact || '—' }}</div>
               <div class="line"><el-icon><Phone /></el-icon>{{ s.phone || '—' }}</div>
               <div class="line"><el-icon><Location /></el-icon>{{ s.address || '—' }}</div>
            </div>
            <div class="s-stat">
               <span>采购单 <b>{{ supStat(s.supplierId).count }}</b></span>
               <span>采购金额 <b class="amt">¥{{ supStat(s.supplierId).amount.toLocaleString() }}</b></span>
            </div>
            <div class="s-foot">
               <el-button link type="primary" icon="Tickets" @click="openPurchases(s)">采购记录</el-button>
               <el-button link type="primary" icon="Edit" @click="handleUpdate(s)" v-hasPermi="['his:supplier:edit']">编辑</el-button>
               <el-button link type="danger" icon="Delete" @click="handleDelete(s)" v-hasPermi="['his:supplier:remove']">删除</el-button>
            </div>
         </div>
         <el-empty v-if="!loading && !filtered.length" description="暂无供应商" :image-size="90" style="grid-column:1/-1" />
      </div>

      <!-- 采购记录抽屉 -->
      <el-drawer v-model="purOpen" :title="`采购记录 - ${cur.supplierName || ''}`" size="46%">
         <el-table :data="curPurchases" size="small" border v-loading="purLoading">
            <el-table-column label="采购单号" prop="purchaseNo" width="150" />
            <el-table-column label="总金额" align="right" width="110">
               <template #default="s"><b style="color:#f56c6c">¥{{ s.row.totalAmount }}</b></template>
            </el-table-column>
            <el-table-column label="状态" align="center" width="100">
               <template #default="s"><dict-tag :options="his_purchase_status" :value="s.row.status" /></template>
            </el-table-column>
            <el-table-column label="创建时间" prop="createTime">
               <template #default="s"><span>{{ parseTime(s.row.createTime) }}</span></template>
            </el-table-column>
         </el-table>
         <el-empty v-if="!purLoading && !curPurchases.length" description="暂无采购记录" :image-size="80" />
      </el-drawer>

      <!-- 添加或修改供应商对话框 -->
      <el-dialog :title="title" v-model="open" width="560px" append-to-body>
         <el-form ref="supplierRef" :model="form" :rules="rules" label-width="100px">
            <el-form-item label="供应商名称" prop="supplierName"><el-input v-model="form.supplierName" /></el-form-item>
            <el-form-item label="联系人" prop="contact"><el-input v-model="form.contact" /></el-form-item>
            <el-form-item label="联系电话" prop="phone"><el-input v-model="form.phone" /></el-form-item>
            <el-form-item label="地址" prop="address"><el-input v-model="form.address" /></el-form-item>
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

<script setup name="Supplier">
import { listSupplier, addSupplier, delSupplier, getSupplier, updateSupplier } from "@/api/his/supplier"
import { listPurchase } from "@/api/his/purchase"
import { OfficeBuilding, User, Phone, Location } from "@element-plus/icons-vue"

const { proxy } = getCurrentInstance()
const { sys_normal_disable, his_purchase_status } = useDict("sys_normal_disable", "his_purchase_status")

const supplierList = ref([])
const purchaseList = ref([])
const kw = ref("")
const open = ref(false)
const loading = ref(true)
const title = ref("")
const purOpen = ref(false)
const purLoading = ref(false)
const cur = ref({})
const curPurchases = ref([])

const data = reactive({
   form: {},
   rules: { supplierName: [{ required: true, message: "供应商名称不能为空", trigger: "blur" }] }
})
const { form, rules } = toRefs(data)

const filtered = computed(() => supplierList.value.filter(s => !kw.value || s.supplierName?.includes(kw.value)))

function supStat(supplierId) {
   const list = purchaseList.value.filter(p => p.supplierId === supplierId)
   return { count: list.length, amount: list.reduce((s, p) => s + Number(p.totalAmount || 0), 0) }
}

function getList() {
   loading.value = true
   Promise.all([
      listSupplier({ pageNum: 1, pageSize: 200 }),
      listPurchase({ pageNum: 1, pageSize: 500 })
   ]).then(([s, p]) => {
      supplierList.value = s.data
      purchaseList.value = p.data
      loading.value = false
   })
}

function openPurchases(s) {
   cur.value = s
   purOpen.value = true
   purLoading.value = true
   listPurchase({ pageNum: 1, pageSize: 100, supplierId: s.supplierId }).then(res => {
      curPurchases.value = res.data
      purLoading.value = false
   })
}

function cancel() { open.value = false; reset() }
function reset() {
   form.value = { supplierId: undefined, supplierName: undefined, contact: undefined, phone: undefined, address: undefined, status: "0", remark: undefined }
   proxy.resetForm("supplierRef")
}
function handleAdd() { reset(); open.value = true; title.value = "添加供应商" }
function handleUpdate(row) {
   reset()
   getSupplier(row.supplierId).then(res => { form.value = res.data; open.value = true; title.value = "修改供应商" })
}
function submitForm() {
   proxy.$refs["supplierRef"].validate(valid => {
      if (!valid) return
      const fn = form.value.supplierId ? updateSupplier : addSupplier
      fn(form.value).then(() => { proxy.$modal.msgSuccess("保存成功"); open.value = false; getList() })
   })
}
function handleDelete(row) {
   proxy.$modal.confirm('是否确认删除供应商"' + row.supplierName + '"？').then(() => delSupplier(row.supplierId)).then(() => { getList(); proxy.$modal.msgSuccess("删除成功") }).catch(() => {})
}
function handleExport() {
   proxy.download("his/supplier/export", { supplierName: kw.value }, "supplier_" + new Date().getTime() + ".xlsx")
}

getList()
</script>

<style scoped>
.topbar { display: flex; justify-content: space-between; margin-bottom: 14px; }
.sup-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 14px; }
.sup-card { background: #fff; border: 1px solid #e4e7ed; border-radius: 8px; padding: 16px; transition: box-shadow .15s; }
.sup-card:hover { box-shadow: 0 4px 16px rgba(0,0,0,.08); }
.sup-card.disabled { opacity: .55; }
.s-head { display: flex; gap: 12px; align-items: center; }
.s-icon { width: 44px; height: 44px; border-radius: 8px; background: #ecf5ff; color: #409eff; display: flex; align-items: center; justify-content: center; }
.s-name { font-size: 15px; font-weight: 700; margin-bottom: 3px; }
.s-body { margin: 12px 0; display: flex; flex-direction: column; gap: 6px; font-size: 13px; color: #606266; }
.line { display: flex; align-items: center; gap: 6px; }
.s-stat { display: flex; justify-content: space-between; background: #f5f7fa; border-radius: 6px; padding: 8px 12px; font-size: 12px; color: #909399; margin-bottom: 10px; }
.s-stat b { font-size: 14px; color: #303133; }
.s-stat .amt { color: #f56c6c; }
.s-foot { border-top: 1px solid #f0f0f0; padding-top: 8px; display: flex; justify-content: flex-end; }
</style>

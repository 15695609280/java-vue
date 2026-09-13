<template>
   <div class="app-container">
      <div class="topbar">
         <el-input v-model="kw" placeholder="搜索病区名称" clearable prefix-icon="Search" style="width: 200px" />
         <div>
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['his:ward:add']">新增病区</el-button>
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['his:ward:export']">导出</el-button>
         </div>
      </div>

      <div v-loading="loading" class="ward-grid">
         <div v-for="w in filtered" :key="w.wardId" class="ward-card" :class="{ disabled: w.status === '1' }">
            <div class="w-head">
               <div>
                  <div class="w-name">{{ w.wardName }}</div>
                  <div class="w-sub">{{ w.deptName }} · {{ w.location || '位置未设置' }}</div>
               </div>
               <el-progress type="dashboard" :width="64" :stroke-width="7"
                  :percentage="usage(w).total ? Math.round(usage(w).used / usage(w).total * 100) : 0"
                  :color="usageColor(w)" />
            </div>
            <div class="w-body">
               <div class="cell"><b>{{ usage(w).total }}</b><span>总床位</span></div>
               <div class="cell used"><b>{{ usage(w).used }}</b><span>已占用</span></div>
               <div class="cell free"><b>{{ usage(w).free }}</b><span>空闲</span></div>
               <div class="cell"><b>{{ w.nurseCount || 0 }}</b><span>护士</span></div>
            </div>
            <!-- 床位示意格 -->
            <div class="bed-strip">
               <span v-for="b in bedsOf(w.wardId)" :key="b.bedId" class="bed-dot" :class="bedCls(b.status)" :title="`${b.bedNo} - ${bedStatusLabel(b.status)}`"></span>
               <span v-if="!bedsOf(w.wardId).length" class="nobed">未配置床位</span>
            </div>
            <div class="w-foot">
               <el-button link type="primary" icon="Edit" @click="handleUpdate(w)" v-hasPermi="['his:ward:edit']">编辑</el-button>
               <el-button link type="danger" icon="Delete" @click="handleDelete(w)" v-hasPermi="['his:ward:remove']">删除</el-button>
            </div>
         </div>
         <el-empty v-if="!loading && !filtered.length" description="暂无病区" :image-size="90" style="grid-column:1/-1" />
      </div>

      <div class="legend">
         <span><i class="bed-dot free"></i>空闲</span>
         <span><i class="bed-dot used"></i>占用</span>
         <span><i class="bed-dot repair"></i>维修</span>
         <span><i class="bed-dot reserved"></i>预留</span>
      </div>

      <!-- 添加或修改病区对话框 -->
      <el-dialog :title="title" v-model="open" width="560px" append-to-body>
         <el-form ref="wardRef" :model="form" :rules="rules" label-width="100px">
            <el-form-item label="病区名称" prop="wardName"><el-input v-model="form.wardName" /></el-form-item>
            <el-form-item label="所属科室" prop="deptId">
               <el-select v-model="form.deptId" filterable style="width:100%">
                  <el-option v-for="d in deptOptions" :key="d.deptId" :label="d.deptName" :value="d.deptId" />
               </el-select>
            </el-form-item>
            <el-form-item label="位置" prop="location"><el-input v-model="form.location" placeholder="如 住院部3楼东区" /></el-form-item>
            <el-form-item label="护士人数" prop="nurseCount"><el-input-number v-model="form.nurseCount" :min="0" controls-position="right" style="width:100%" /></el-form-item>
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

<script setup name="Ward">
import { listWard, addWard, delWard, getWard, updateWard } from "@/api/his/ward"
import { listDepartment } from "@/api/his/dept"
import { listBed } from "@/api/his/bed"

const { proxy } = getCurrentInstance()
const { sys_normal_disable, his_bed_status } = useDict("sys_normal_disable", "his_bed_status")

const wardList = ref([])
const bedList = ref([])
const deptOptions = ref([])
const kw = ref("")
const open = ref(false)
const loading = ref(true)
const title = ref("")

const data = reactive({
   form: {},
   rules: {
      wardName: [{ required: true, message: "病区名称不能为空", trigger: "blur" }],
      deptId: [{ required: true, message: "所属科室不能为空", trigger: "change" }],
   }
})
const { form, rules } = toRefs(data)

const filtered = computed(() => wardList.value.filter(w => !kw.value || w.wardName?.includes(kw.value)))

function bedsOf(wardId) { return bedList.value.filter(b => b.wardId === wardId) }
function usage(w) {
   const beds = bedsOf(w.wardId)
   const used = beds.filter(b => b.status === "1").length
   return { total: beds.length, used, free: beds.filter(b => b.status === "0").length }
}
function usageColor(w) {
   const u = usage(w)
   const pct = u.total ? u.used / u.total : 0
   return pct > 0.9 ? "#f56c6c" : pct > 0.7 ? "#e6a23c" : "#67c23a"
}
function bedCls(s) { return ({ "0": "free", "1": "used", "2": "repair", "3": "reserved" })[s] || "free" }
function bedStatusLabel(s) { return his_bed_status.value.find(x => x.value === s)?.label || s }

function getList() {
   loading.value = true
   Promise.all([
      listWard({ pageNum: 1, pageSize: 200 }),
      listBed({ pageNum: 1, pageSize: 1000 }),
      listDepartment({ pageNum: 1, pageSize: 200 })
   ]).then(([w, b, d]) => {
      wardList.value = w.data
      bedList.value = b.data
      deptOptions.value = d.data
      loading.value = false
   })
}

function cancel() { open.value = false; reset() }
function reset() {
   form.value = { wardId: undefined, wardName: undefined, deptId: undefined, location: undefined, nurseCount: 5, status: "0", remark: undefined }
   proxy.resetForm("wardRef")
}
function handleAdd() { reset(); open.value = true; title.value = "添加病区" }
function handleUpdate(row) {
   reset()
   getWard(row.wardId).then(res => { form.value = res.data; open.value = true; title.value = "修改病区" })
}
function submitForm() {
   proxy.$refs["wardRef"].validate(valid => {
      if (!valid) return
      const fn = form.value.wardId ? updateWard : addWard
      fn(form.value).then(() => { proxy.$modal.msgSuccess("保存成功"); open.value = false; getList() })
   })
}
function handleDelete(row) {
   proxy.$modal.confirm('是否确认删除病区"' + row.wardName + '"？').then(() => delWard(row.wardId)).then(() => { getList(); proxy.$modal.msgSuccess("删除成功") }).catch(() => {})
}
function handleExport() {
   proxy.download("his/ward/export", { wardName: kw.value }, "ward_" + new Date().getTime() + ".xlsx")
}

getList()
</script>

<style scoped>
.topbar { display: flex; justify-content: space-between; margin-bottom: 14px; }
.ward-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 14px; }
.ward-card { background: #fff; border: 1px solid #e4e7ed; border-radius: 8px; padding: 16px; transition: box-shadow .15s; }
.ward-card:hover { box-shadow: 0 4px 16px rgba(0,0,0,.08); }
.ward-card.disabled { opacity: .55; }
.w-head { display: flex; justify-content: space-between; align-items: center; }
.w-name { font-size: 16px; font-weight: 700; }
.w-sub { font-size: 12px; color: #909399; margin-top: 3px; }
.w-body { display: flex; margin: 14px 0 10px; }
.cell { flex: 1; text-align: center; }
.cell b { display: block; font-size: 20px; }
.cell span { font-size: 12px; color: #909399; }
.cell.used b { color: #f56c6c; }
.cell.free b { color: #67c23a; }
.bed-strip { display: flex; flex-wrap: wrap; gap: 4px; min-height: 18px; margin-bottom: 10px; }
.bed-dot { width: 12px; height: 12px; border-radius: 3px; display: inline-block; }
.bed-dot.free { background: #c2e7b0; }
.bed-dot.used { background: #fab6b6; }
.bed-dot.repair { background: #f5dab1; }
.bed-dot.reserved { background: #d3dce6; }
.nobed { font-size: 12px; color: #c0c4cc; }
.w-foot { border-top: 1px solid #f0f0f0; padding-top: 8px; display: flex; justify-content: flex-end; }
.legend { display: flex; gap: 18px; margin-top: 12px; font-size: 12px; color: #606266; }
.legend span { display: flex; align-items: center; gap: 5px; }
</style>

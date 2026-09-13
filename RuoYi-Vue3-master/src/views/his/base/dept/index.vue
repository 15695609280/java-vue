<template>
   <div class="app-container">
      <div class="topbar">
         <el-radio-group v-model="typeFilter">
            <el-radio-button value="">全部</el-radio-button>
            <el-radio-button v-for="d in his_dept_type" :key="d.value" :value="d.value">{{ d.label }}</el-radio-button>
         </el-radio-group>
         <div class="right">
            <el-input v-model="kw" placeholder="搜索科室名称/编码" clearable prefix-icon="Search" style="width: 200px" />
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['his:dept:add']">新增科室</el-button>
         </div>
      </div>

      <div v-loading="loading" class="dept-grid">
         <div v-for="d in filtered" :key="d.deptId" class="dept-card" :class="{ disabled: d.status === '1' }">
            <div class="card-head" :style="{ background: typeColor(d.deptType).bg }">
               <div class="dname">{{ d.deptName }}</div>
               <el-tag size="small" effect="dark" :color="typeColor(d.deptType).tag" style="border:none">{{ typeLabel(d.deptType) }}</el-tag>
            </div>
            <div class="card-body">
               <div class="code">{{ d.deptCode }}</div>
               <div class="stats">
                  <div class="stat"><b>{{ statOf(d.deptId).doctors }}</b><span>医生</span></div>
                  <div class="stat"><b>{{ statOf(d.deptId).beds }}</b><span>床位</span></div>
                  <div class="stat"><b>{{ statOf(d.deptId).used }}</b><span>占用</span></div>
               </div>
               <div class="meta">
                  <div v-if="d.location"><el-icon><Location /></el-icon>{{ d.location }}</div>
                  <div v-if="d.phone"><el-icon><Phone /></el-icon>{{ d.phone }}</div>
               </div>
               <div v-if="statOf(d.deptId).beds" class="usage">
                  床位使用率
                  <el-progress :percentage="Math.round(statOf(d.deptId).used / statOf(d.deptId).beds * 100)" :stroke-width="10" :status="statOf(d.deptId).used / statOf(d.deptId).beds > 0.9 ? 'exception' : 'success'" />
               </div>
            </div>
            <div class="card-foot">
               <el-button link type="primary" icon="View" @click="openDetail(d)">详情</el-button>
               <el-button link type="primary" icon="Edit" @click="handleUpdate(d)" v-hasPermi="['his:dept:edit']">编辑</el-button>
               <el-button link type="danger" icon="Delete" @click="handleDelete(d)" v-hasPermi="['his:dept:remove']">删除</el-button>
            </div>
         </div>
         <el-empty v-if="!loading && !filtered.length" description="暂无科室" :image-size="90" style="grid-column: 1/-1" />
      </div>

      <!-- 科室详情抽屉: 医生/病区/床位一览 -->
      <el-drawer v-model="detailOpen" :title="`${cur.deptName || ''} - 科室详情`" size="46%">
         <el-descriptions :column="2" border size="small" style="margin-bottom:14px">
            <el-descriptions-item label="科室编码">{{ cur.deptCode }}</el-descriptions-item>
            <el-descriptions-item label="类型"><dict-tag :options="his_dept_type" :value="cur.deptType" /></el-descriptions-item>
            <el-descriptions-item label="位置">{{ cur.location }}</el-descriptions-item>
            <el-descriptions-item label="电话">{{ cur.phone }}</el-descriptions-item>
            <el-descriptions-item label="简介" :span="2">{{ cur.intro || '—' }}</el-descriptions-item>
         </el-descriptions>
         <el-tabs>
            <el-tab-pane :label="`医生 (${deptDoctors.length})`">
               <el-table :data="deptDoctors" size="small" border>
                  <el-table-column label="姓名" prop="doctorName" width="100" />
                  <el-table-column label="职称" width="110">
                     <template #default="s"><dict-tag :options="his_doctor_title" :value="s.row.title" /></template>
                  </el-table-column>
                  <el-table-column label="擅长" prop="specialty" :show-overflow-tooltip="true" />
                  <el-table-column label="挂号费" prop="regFee" width="80" align="center" />
               </el-table>
            </el-tab-pane>
            <el-tab-pane :label="`病区/床位 (${deptBeds.length})`">
               <el-table :data="deptBeds" size="small" border>
                  <el-table-column label="病区" prop="wardName" width="130" />
                  <el-table-column label="床号" prop="bedNo" width="90" />
                  <el-table-column label="类型" width="100">
                     <template #default="s"><dict-tag :options="his_bed_type" :value="s.row.bedType" /></template>
                  </el-table-column>
                  <el-table-column label="状态" width="90">
                     <template #default="s"><dict-tag :options="his_bed_status" :value="s.row.status" /></template>
                  </el-table-column>
               </el-table>
            </el-tab-pane>
         </el-tabs>
      </el-drawer>

      <!-- 添加或修改科室对话框 -->
      <el-dialog :title="title" v-model="open" width="640px" append-to-body>
         <el-form ref="departmentRef" :model="form" :rules="rules" label-width="110px">
            <el-form-item label="科室名称" prop="deptName">
               <el-input v-model="form.deptName" placeholder="请输入科室名称" />
            </el-form-item>
            <el-form-item label="科室编码" prop="deptCode">
               <el-input v-model="form.deptCode" placeholder="请输入科室编码" />
            </el-form-item>
            <el-form-item label="科室类型" prop="deptType">
               <el-select v-model="form.deptType" placeholder="请选择科室类型" clearable>
                  <el-option v-for="dict in his_dept_type" :key="dict.value" :label="dict.label" :value="dict.value" />
               </el-select>
            </el-form-item>
            <el-form-item label="位置" prop="location">
               <el-input v-model="form.location" placeholder="请输入位置" />
            </el-form-item>
            <el-form-item label="联系电话" prop="phone">
               <el-input v-model="form.phone" placeholder="请输入联系电话" />
            </el-form-item>
            <el-form-item label="科室简介" prop="intro">
               <el-input v-model="form.intro" type="textarea" :rows="3" placeholder="请输入科室简介" />
            </el-form-item>
            <el-form-item label="排序" prop="orderNum">
               <el-input-number v-model="form.orderNum" :min="0" controls-position="right" style="width: 100%" />
            </el-form-item>
            <el-form-item label="状态" prop="status">
               <el-radio-group v-model="form.status">
                  <el-radio v-for="dict in sys_normal_disable" :key="dict.value" :value="dict.value">{{ dict.label }}</el-radio>
               </el-radio-group>
            </el-form-item>
            <el-form-item label="备注" prop="remark">
               <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
            </el-form-item>
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

<script setup name="Dept">
import { listDepartment, addDepartment, delDepartment, getDepartment, updateDepartment } from "@/api/his/dept"
import { listDoctor } from "@/api/his/doctor"
import { listBed } from "@/api/his/bed"
import { listWard } from "@/api/his/ward"
import { Location, Phone } from "@element-plus/icons-vue"

const { proxy } = getCurrentInstance()
const { his_dept_type, sys_normal_disable, his_doctor_title, his_bed_type, his_bed_status } =
   useDict("his_dept_type", "sys_normal_disable", "his_doctor_title", "his_bed_type", "his_bed_status")

const departmentList = ref([])
const doctorList = ref([])
const bedList = ref([])
const wardMap = ref({})
const typeFilter = ref("")
const kw = ref("")
const open = ref(false)
const loading = ref(true)
const title = ref("")
const detailOpen = ref(false)
const cur = ref({})
const deptDoctors = ref([])
const deptBeds = ref([])

const data = reactive({
   form: {},
   rules: {
      deptName: [{ required: true, message: "科室名称不能为空", trigger: "blur" }],
      deptCode: [{ required: true, message: "科室编码不能为空", trigger: "blur" }],
      deptType: [{ required: true, message: "科室类型不能为空", trigger: "change" }],
   }
})
const { form, rules } = toRefs(data)

const TYPE_COLORS = {
   "0": { bg: "#ecf5ff", tag: "#409eff" },
   "1": { bg: "#f0f9eb", tag: "#67c23a" },
   "2": { bg: "#fdf6ec", tag: "#e6a23c" },
   "3": { bg: "#f4f4f5", tag: "#909399" },
}
function typeColor(t) { return TYPE_COLORS[t] || TYPE_COLORS["3"] }
function typeLabel(t) { return his_dept_type.value.find(x => x.value === t)?.label || "其他" }

const filtered = computed(() => departmentList.value.filter(d =>
   (!typeFilter.value || d.deptType === typeFilter.value) &&
   (!kw.value || d.deptName?.includes(kw.value) || d.deptCode?.includes(kw.value))
))

/** 科室维度聚合: 医生数/床位数/占用数 */
function statOf(deptId) {
   const doctors = doctorList.value.filter(d => d.deptId === deptId).length
   const beds = bedList.value.filter(b => wardMap.value[b.wardId] === deptId)
   return { doctors, beds: beds.length, used: beds.filter(b => b.status === "1").length }
}

function getList() {
   loading.value = true
   Promise.all([
      listDepartment({ pageNum: 1, pageSize: 500 }),
      listDoctor({ pageNum: 1, pageSize: 500 }),
      listBed({ pageNum: 1, pageSize: 1000 }),
      listWard({ pageNum: 1, pageSize: 200 })
   ]).then(([dept, doc, bed, ward]) => {
      departmentList.value = dept.data
      doctorList.value = doc.data
      bedList.value = bed.data
      wardMap.value = Object.fromEntries((ward.data || []).map(w => [w.wardId, w.deptId]))
      loading.value = false
   })
}

function openDetail(d) {
   cur.value = d
   deptDoctors.value = doctorList.value.filter(x => x.deptId === d.deptId)
   deptBeds.value = bedList.value.filter(x => wardMap.value[x.wardId] === d.deptId)
   detailOpen.value = true
}

function cancel() { open.value = false; reset() }
function reset() {
   form.value = { deptId: undefined, parentId: 0, deptName: undefined, deptCode: undefined, deptType: "0", location: undefined, phone: undefined, intro: undefined, orderNum: 0, status: "0", remark: undefined }
   proxy.resetForm("departmentRef")
}
function handleAdd() { reset(); open.value = true; title.value = "添加科室" }
function handleUpdate(row) {
   reset()
   getDepartment(row.deptId).then(res => { form.value = res.data; open.value = true; title.value = "修改科室" })
}
function submitForm() {
   proxy.$refs["departmentRef"].validate(valid => {
      if (!valid) return
      const fn = form.value.deptId ? updateDepartment : addDepartment
      fn(form.value).then(() => { proxy.$modal.msgSuccess("保存成功"); open.value = false; getList() })
   })
}
function handleDelete(row) {
   proxy.$modal.confirm('是否确认删除科室"' + row.deptName + '"？').then(() => delDepartment(row.deptId)).then(() => { getList(); proxy.$modal.msgSuccess("删除成功") }).catch(() => {})
}

getList()
</script>

<style scoped>
.topbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px; flex-wrap: wrap; gap: 10px; }
.topbar .right { display: flex; gap: 10px; }

.dept-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 14px; }
.dept-card { background: #fff; border-radius: 8px; border: 1px solid #e4e7ed; overflow: hidden; display: flex; flex-direction: column; transition: box-shadow .15s; }
.dept-card:hover { box-shadow: 0 4px 16px rgba(0,0,0,.08); }
.dept-card.disabled { opacity: .55; }
.card-head { padding: 14px 16px; display: flex; justify-content: space-between; align-items: center; }
.dname { font-size: 16px; font-weight: 700; }
.card-body { padding: 12px 16px; flex: 1; }
.code { font-size: 12px; color: #909399; margin-bottom: 10px; }
.stats { display: flex; gap: 8px; margin-bottom: 10px; }
.stat { flex: 1; background: #f5f7fa; border-radius: 6px; padding: 8px 0; text-align: center; }
.stat b { display: block; font-size: 18px; }
.stat span { font-size: 12px; color: #909399; }
.meta { font-size: 12px; color: #606266; display: flex; flex-direction: column; gap: 4px; margin-bottom: 8px; }
.meta div { display: flex; align-items: center; gap: 4px; }
.usage { font-size: 12px; color: #909399; }
.card-foot { border-top: 1px solid #f0f0f0; padding: 6px 10px; display: flex; justify-content: flex-end; }
</style>

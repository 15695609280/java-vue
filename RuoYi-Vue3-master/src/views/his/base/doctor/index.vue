<template>
   <div class="app-container">
      <div class="topbar">
         <div class="filters">
            <el-input v-model="queryParams.doctorName" placeholder="搜索医生姓名" clearable prefix-icon="Search" style="width: 170px" @input="pageNum = 1" />
            <el-select v-model="queryParams.deptId" placeholder="全部科室" clearable filterable style="width: 160px">
               <el-option v-for="d in deptOptions" :key="d.deptId" :label="d.deptName" :value="d.deptId" />
            </el-select>
            <el-select v-model="titleFilter" placeholder="全部职称" clearable style="width: 140px">
               <el-option v-for="d in his_doctor_title" :key="d.value" :label="d.label" :value="d.value" />
            </el-select>
         </div>
         <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['his:doctor:add']">新增医生</el-button>
      </div>

      <div v-loading="loading" class="doc-grid">
         <div v-for="doc in filtered" :key="doc.doctorId" class="doc-card" :class="{ disabled: doc.status === '1' }" @click="openProfile(doc)">
            <div class="avatar" :style="{ background: avatarBg(doc) }">{{ doc.doctorName?.slice(0, 1) }}</div>
            <div class="info">
               <div class="row1">
                  <span class="name">{{ doc.doctorName }}</span>
                  <el-tag size="small" :type="titleType(doc.title)" effect="dark">{{ titleLabel(doc.title) }}</el-tag>
                  <el-tag v-if="doc.status === '1'" size="small" type="info">停用</el-tag>
               </div>
               <div class="row2">{{ doc.deptName }} · {{ doc.gender === '0' ? '男' : '女' }}</div>
               <div class="row3">{{ doc.specialty || '暂无擅长领域' }}</div>
               <div class="row4">
                  <span class="fee">¥{{ doc.regFee }}</span>
                  <span class="sched" v-if="schedCount(doc.doctorId)">本周 {{ schedCount(doc.doctorId) }} 个班次</span>
                  <span class="sched off" v-else>本周未排班</span>
               </div>
            </div>
            <div class="ops" @click.stop>
               <el-button link type="primary" icon="Edit" @click="handleUpdate(doc)" v-hasPermi="['his:doctor:edit']" />
               <el-button link type="danger" icon="Delete" @click="handleDelete(doc)" v-hasPermi="['his:doctor:remove']" />
            </div>
         </div>
         <el-empty v-if="!loading && !filtered.length" description="暂无医生" :image-size="90" style="grid-column: 1/-1" />
      </div>

      <!-- 医生档案抽屉: 基本信息 + 本周排班 -->
      <el-drawer v-model="profileOpen" :title="`医生档案 - ${cur.doctorName || ''}`" size="40%">
         <div class="pf-head">
            <div class="avatar big" :style="{ background: avatarBg(cur) }">{{ cur.doctorName?.slice(0, 1) }}</div>
            <div>
               <div style="font-size:18px;font-weight:700">{{ cur.doctorName }}
                  <el-tag size="small" :type="titleType(cur.title)" effect="dark" style="margin-left:6px">{{ titleLabel(cur.title) }}</el-tag>
               </div>
               <div style="color:#909399;margin-top:4px">{{ cur.deptName }} · 挂号费 ¥{{ cur.regFee }}</div>
            </div>
         </div>
         <el-descriptions :column="1" border size="small" style="margin:14px 0">
            <el-descriptions-item label="联系电话">{{ cur.phone || '—' }}</el-descriptions-item>
            <el-descriptions-item label="擅长领域">{{ cur.specialty || '—' }}</el-descriptions-item>
            <el-descriptions-item label="个人简介">{{ cur.intro || '—' }}</el-descriptions-item>
         </el-descriptions>
         <el-divider content-position="left">本周排班</el-divider>
         <el-table :data="curSchedules" size="small" border>
            <el-table-column label="日期" prop="workDate" width="110" />
            <el-table-column label="时段" width="80">
               <template #default="s"><dict-tag :options="his_time_slot" :value="s.row.timeSlot" /></template>
            </el-table-column>
            <el-table-column label="号源" width="110">
               <template #default="s">{{ s.row.remainQuota }}/{{ s.row.quota }}</template>
            </el-table-column>
            <el-table-column label="状态">
               <template #default="s"><dict-tag :options="his_schedule_status" :value="s.row.status" /></template>
            </el-table-column>
         </el-table>
         <el-empty v-if="!curSchedules.length" description="本周暂无排班" :image-size="70" />
      </el-drawer>

      <!-- 添加或修改医生对话框 -->
      <el-dialog :title="title" v-model="open" width="640px" append-to-body>
         <el-form ref="doctorRef" :model="form" :rules="rules" label-width="110px">
            <el-form-item label="医生姓名" prop="doctorName">
               <el-input v-model="form.doctorName" placeholder="请输入医生姓名" />
            </el-form-item>
            <el-form-item label="所属科室" prop="deptId">
               <el-select v-model="form.deptId" placeholder="请选择所属科室" clearable filterable>
                  <el-option v-for="item in deptOptions" :key="item.deptId" :label="item.deptName" :value="item.deptId" />
               </el-select>
            </el-form-item>
            <el-form-item label="职称" prop="title">
               <el-select v-model="form.title" placeholder="请选择职称" clearable>
                  <el-option v-for="dict in his_doctor_title" :key="dict.value" :label="dict.label" :value="dict.value" />
               </el-select>
            </el-form-item>
            <el-form-item label="性别" prop="gender">
               <el-radio-group v-model="form.gender">
                  <el-radio v-for="dict in sys_user_sex" :key="dict.value" :value="dict.value">{{ dict.label }}</el-radio>
               </el-radio-group>
            </el-form-item>
            <el-form-item label="联系电话" prop="phone">
               <el-input v-model="form.phone" placeholder="请输入联系电话" />
            </el-form-item>
            <el-form-item label="擅长领域" prop="specialty">
               <el-input v-model="form.specialty" placeholder="请输入擅长领域" />
            </el-form-item>
            <el-form-item label="挂号费(元)" prop="regFee">
               <el-input-number v-model="form.regFee" :precision="2" :min="0" controls-position="right" style="width: 100%" />
            </el-form-item>
            <el-form-item label="个人简介" prop="intro">
               <el-input v-model="form.intro" type="textarea" :rows="3" placeholder="请输入个人简介" />
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

<script setup name="Doctor">
import { listDoctor, addDoctor, delDoctor, getDoctor, updateDoctor } from "@/api/his/doctor"
import { listDepartment } from "@/api/his/dept"
import { listSchedule } from "@/api/his/schedule"

const { proxy } = getCurrentInstance()
const { his_doctor_title, sys_user_sex, sys_normal_disable, his_time_slot, his_schedule_status } =
   useDict("his_doctor_title", "sys_user_sex", "sys_normal_disable", "his_time_slot", "his_schedule_status")

const doctorList = ref([])
const deptOptions = ref([])
const weekSchedules = ref([])
const titleFilter = ref()
const open = ref(false)
const loading = ref(true)
const title = ref("")
const profileOpen = ref(false)
const cur = ref({})
const curSchedules = ref([])

const data = reactive({
   form: {},
   queryParams: { doctorName: undefined, deptId: undefined },
   rules: {
      doctorName: [{ required: true, message: "医生姓名不能为空", trigger: "blur" }],
      deptId: [{ required: true, message: "所属科室不能为空", trigger: "change" }],
      title: [{ required: true, message: "职称不能为空", trigger: "change" }],
      regFee: [{ required: true, message: "挂号费(元)不能为空", trigger: "blur" }],
   }
})
const { queryParams, form, rules } = toRefs(data)

const filtered = computed(() => doctorList.value.filter(d =>
   (!queryParams.value.doctorName || d.doctorName?.includes(queryParams.value.doctorName)) &&
   (!queryParams.value.deptId || d.deptId === queryParams.value.deptId) &&
   (!titleFilter.value || d.title === titleFilter.value)
))

const AVATAR_COLORS = ["#409eff", "#67c23a", "#e6a23c", "#f56c6c", "#722ed1", "#13c2c2"]
function avatarBg(doc) { return AVATAR_COLORS[(doc.doctorId || 0) % AVATAR_COLORS.length] }
function titleLabel(v) { return his_doctor_title.value.find(x => x.value === v)?.label || "" }
function titleType(v) { return ({ "0": "danger", "1": "warning", "2": "primary", "3": "info" })[v] || "info" }
function schedCount(doctorId) { return weekSchedules.value.filter(s => s.doctorId === doctorId).length }

function getList() {
   loading.value = true
   Promise.all([
      listDoctor({ pageNum: 1, pageSize: 500 }),
      listDepartment({ pageNum: 1, pageSize: 200 }),
      listSchedule({ pageNum: 1, pageSize: 1000 })
   ]).then(([doc, dept, sch]) => {
      doctorList.value = doc.data
      deptOptions.value = dept.data
      const { start, end } = thisWeek()
      weekSchedules.value = (sch.data || []).filter(s => {
         const d = (s.workDate || "").slice(0, 10)
         return d >= start && d <= end
      })
      loading.value = false
   })
}

function thisWeek() {
   const now = new Date()
   const dow = (now.getDay() + 6) % 7
   const s = new Date(now); s.setDate(s.getDate() - dow)
   const e = new Date(s); e.setDate(e.getDate() + 6)
   const f = d => `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}-${String(d.getDate()).padStart(2, "0")}`
   return { start: f(s), end: f(e) }
}

function openProfile(doc) {
   cur.value = doc
   curSchedules.value = weekSchedules.value.filter(s => s.doctorId === doc.doctorId)
   profileOpen.value = true
}

function cancel() { open.value = false; reset() }
function reset() {
   form.value = { doctorId: undefined, doctorName: undefined, deptId: undefined, title: "2", gender: "0", phone: undefined, specialty: undefined, regFee: 10, intro: undefined, status: "0", remark: undefined }
   proxy.resetForm("doctorRef")
}
function handleAdd() { reset(); open.value = true; title.value = "添加医生" }
function handleUpdate(row) {
   reset()
   getDoctor(row.doctorId).then(res => { form.value = res.data; open.value = true; title.value = "修改医生" })
}
function submitForm() {
   proxy.$refs["doctorRef"].validate(valid => {
      if (!valid) return
      const fn = form.value.doctorId ? updateDoctor : addDoctor
      fn(form.value).then(() => { proxy.$modal.msgSuccess("保存成功"); open.value = false; getList() })
   })
}
function handleDelete(row) {
   proxy.$modal.confirm('是否确认删除医生"' + row.doctorName + '"？').then(() => delDoctor(row.doctorId)).then(() => { getList(); proxy.$modal.msgSuccess("删除成功") }).catch(() => {})
}

getList()
</script>

<style scoped>
.topbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px; flex-wrap: wrap; gap: 10px; }
.filters { display: flex; gap: 10px; }

.doc-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(330px, 1fr)); gap: 14px; }
.doc-card {
   background: #fff; border: 1px solid #e4e7ed; border-radius: 8px; padding: 16px;
   display: flex; gap: 14px; cursor: pointer; position: relative; transition: box-shadow .15s, transform .15s;
}
.doc-card:hover { box-shadow: 0 4px 16px rgba(0,0,0,.1); transform: translateY(-2px); }
.doc-card.disabled { opacity: .55; }
.avatar {
   width: 52px; height: 52px; border-radius: 50%; color: #fff; font-size: 20px; font-weight: 700;
   display: flex; align-items: center; justify-content: center; flex-shrink: 0;
}
.avatar.big { width: 64px; height: 64px; font-size: 26px; }
.info { flex: 1; min-width: 0; }
.row1 { display: flex; align-items: center; gap: 8px; }
.name { font-size: 16px; font-weight: 700; }
.row2 { font-size: 13px; color: #606266; margin-top: 3px; }
.row3 { font-size: 12px; color: #909399; margin-top: 3px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.row4 { display: flex; justify-content: space-between; align-items: center; margin-top: 8px; }
.fee { color: #f56c6c; font-weight: 700; font-size: 15px; }
.sched { font-size: 12px; color: #67c23a; }
.sched.off { color: #c0c4cc; }
.ops { position: absolute; top: 10px; right: 10px; }
.pf-head { display: flex; gap: 14px; align-items: center; }
</style>

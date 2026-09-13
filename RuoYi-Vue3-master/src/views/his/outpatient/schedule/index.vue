<template>
   <div class="app-container">
      <!-- 顶部工具栏: 科室筛选 + 周导航 -->
      <el-card shadow="never" class="toolbar-card">
         <div class="toolbar">
            <div class="left">
               <el-select v-model="deptId" placeholder="全部科室" clearable filterable style="width: 160px" @change="buildBoard">
                  <el-option v-for="d in deptOptions" :key="d.deptId" :label="d.deptName" :value="d.deptId" />
               </el-select>
               <el-input v-model="kw" placeholder="搜索医生" clearable prefix-icon="Search" style="width: 150px" @input="buildBoard" />
            </div>
            <div class="week-nav">
               <el-button icon="ArrowLeft" circle @click="shiftWeek(-1)" />
               <el-button size="small" @click="goThisWeek">本周</el-button>
               <span class="week-label">{{ weekLabel }}</span>
               <el-button icon="ArrowRight" circle @click="shiftWeek(1)" />
            </div>
            <div class="legend">
               <span class="lg"><i class="dot open"></i>开放挂号</span>
               <span class="lg"><i class="dot full"></i>约满</span>
               <span class="lg"><i class="dot stop"></i>停诊</span>
            </div>
         </div>
      </el-card>

      <!-- 排班板: 行=医生 列=周一至周日, 单元格=时段号源 -->
      <div class="board" v-loading="loading">
         <div class="board-head">
            <div class="col-doctor">医生 / 科室</div>
            <div v-for="d in weekDays" :key="d.date" class="col-day" :class="{ today: d.date === today }">
               <div class="dow">{{ d.dow }}</div>
               <div class="dnum">{{ d.label }}</div>
            </div>
         </div>
         <div v-for="doc in boardDoctors" :key="doc.doctorId" class="board-row">
            <div class="col-doctor doc-cell">
               <div class="avatar">{{ doc.doctorName?.slice(0, 1) }}</div>
               <div>
                  <div class="dname">{{ doc.doctorName }}
                     <el-tag size="small" effect="plain" style="margin-left:4px">{{ titleLabel(doc.title) }}</el-tag>
                  </div>
                  <div class="ddept">{{ doc.deptName }} · ¥{{ doc.regFee }}</div>
               </div>
            </div>
            <div v-for="d in weekDays" :key="d.date" class="col-day cell" :class="{ today: d.date === today }">
               <template v-for="slot in slotsOf(doc.doctorId, d.date)" :key="slot.scheduleId">
                  <div class="slot" :class="slotClass(slot)" @click="editSlot(slot)">
                     <span class="sname">{{ slotLabel(slot.timeSlot) }}</span>
                     <span class="squota">{{ slot.remainQuota }}/{{ slot.quota }}</span>
                  </div>
               </template>
               <div class="add-btn" @click="addSlot(doc, d.date)" v-hasPermi="['his:schedule:add']">
                  <el-icon><Plus /></el-icon>
               </div>
            </div>
         </div>
         <el-empty v-if="!loading && !boardDoctors.length" description="该科室暂无医生" :image-size="80" />
      </div>

      <!-- 新增/编辑排班 -->
      <el-dialog :title="title" v-model="open" width="520px" append-to-body>
         <el-form ref="scheduleRef" :model="form" :rules="rules" label-width="90px">
            <el-form-item label="科室" prop="deptId">
               <el-select v-model="form.deptId" filterable style="width:100%" @change="onDeptChange">
                  <el-option v-for="d in deptOptions" :key="d.deptId" :label="d.deptName" :value="d.deptId" />
               </el-select>
            </el-form-item>
            <el-form-item label="医生" prop="doctorId">
               <el-select v-model="form.doctorId" filterable style="width:100%">
                  <el-option v-for="d in dialogDoctors" :key="d.doctorId" :label="d.doctorName" :value="d.doctorId" />
               </el-select>
            </el-form-item>
            <el-form-item label="出诊日期" prop="workDate">
               <el-date-picker v-model="form.workDate" type="date" value-format="YYYY-MM-DD" style="width:100%" />
            </el-form-item>
            <el-form-item label="时段" prop="timeSlot">
               <el-radio-group v-model="form.timeSlot">
                  <el-radio-button v-for="dict in his_time_slot" :key="dict.value" :value="dict.value">{{ dict.label }}</el-radio-button>
               </el-radio-group>
            </el-form-item>
            <el-row>
               <el-col :span="12">
                  <el-form-item label="总号源" prop="quota">
                     <el-input-number v-model="form.quota" :min="1" :max="200" controls-position="right" style="width:100%" @change="syncRemain" />
                  </el-form-item>
               </el-col>
               <el-col :span="12">
                  <el-form-item label="剩余号源" prop="remainQuota">
                     <el-input-number v-model="form.remainQuota" :min="0" :max="form.quota" controls-position="right" style="width:100%" />
                  </el-form-item>
               </el-col>
            </el-row>
            <el-form-item label="状态" prop="status">
               <el-radio-group v-model="form.status">
                  <el-radio v-for="dict in his_schedule_status" :key="dict.value" :value="dict.value">{{ dict.label }}</el-radio>
               </el-radio-group>
            </el-form-item>
         </el-form>
         <template #footer>
            <el-button v-if="form.scheduleId" type="danger" plain @click="removeSlot" v-hasPermi="['his:schedule:remove']">删除排班</el-button>
            <el-button type="primary" @click="submitForm">确 定</el-button>
            <el-button @click="open = false">取 消</el-button>
         </template>
      </el-dialog>
   </div>
</template>

<script setup name="Schedule">
import { listSchedule, addSchedule, delSchedule, updateSchedule } from "@/api/his/schedule"
import { listDepartment } from "@/api/his/dept"
import { listDoctor } from "@/api/his/doctor"
import { Plus } from "@element-plus/icons-vue"

const { proxy } = getCurrentInstance()
const { his_doctor_title, his_time_slot, his_schedule_status } = useDict("his_doctor_title", "his_time_slot", "his_schedule_status")

const loading = ref(false)
const deptId = ref()
const kw = ref("")
const deptOptions = ref([])
const doctorOptions = ref([])
const allSchedules = ref([])
const weekStart = ref(startOfWeek(new Date()))
const open = ref(false)
const title = ref("")
const dialogDoctors = ref([])

const today = formatDate(new Date())
const form = ref({})
const rules = {
   deptId: [{ required: true, message: "科室不能为空", trigger: "change" }],
   doctorId: [{ required: true, message: "医生不能为空", trigger: "change" }],
   workDate: [{ required: true, message: "出诊日期不能为空", trigger: "change" }],
   timeSlot: [{ required: true, message: "时段不能为空", trigger: "change" }],
   quota: [{ required: true, message: "总号源不能为空", trigger: "blur" }],
}

function startOfWeek(d) {
   const x = new Date(d)
   const dow = (x.getDay() + 6) % 7
   x.setDate(x.getDate() - dow)
   x.setHours(0, 0, 0, 0)
   return x
}
function formatDate(d) {
   return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}-${String(d.getDate()).padStart(2, "0")}`
}

const weekDays = computed(() => {
   const names = ["周一", "周二", "周三", "周四", "周五", "周六", "周日"]
   return names.map((dow, i) => {
      const d = new Date(weekStart.value)
      d.setDate(d.getDate() + i)
      return { date: formatDate(d), dow, label: `${d.getMonth() + 1}/${d.getDate()}` }
   })
})
const weekLabel = computed(() => {
   const end = new Date(weekStart.value); end.setDate(end.getDate() + 6)
   return `${formatDate(weekStart.value)} ~ ${formatDate(end)}`
})

const weekDates = computed(() => new Set(weekDays.value.map(d => d.date)))

/** 本周排班数据(医生×日期→时段列表) */
const boardDoctors = computed(() => {
   return doctorOptions.value.filter(d =>
      (!deptId.value || d.deptId === deptId.value) &&
      (!kw.value || d.doctorName?.includes(kw.value))
   )
})

function slotsOf(doctorId, date) {
   return allSchedules.value.filter(s => s.doctorId === doctorId && s.workDate === date)
}
function slotLabel(v) { return his_time_slot.value.find(x => x.value === v)?.label || v }
function titleLabel(v) { return his_doctor_title.value.find(x => x.value === v)?.label || "" }
function slotClass(s) {
   if (s.status === "1") return "stop"
   if (s.status === "2" || s.remainQuota <= 0) return "full"
   return "open"
}

function shiftWeek(n) {
   const d = new Date(weekStart.value)
   d.setDate(d.getDate() + n * 7)
   weekStart.value = d
   loadSchedules()
}
function goThisWeek() { weekStart.value = startOfWeek(new Date()); loadSchedules() }

/** 拉取全部排班后客户端按周过滤(后端仅支持单日过滤) */
function loadSchedules() {
   loading.value = true
   listSchedule({ pageNum: 1, pageSize: 1000, deptId: deptId.value }).then(res => {
      allSchedules.value = (res.data || []).map(s => ({ ...s, workDate: (s.workDate || "").slice(0, 10) }))
      loading.value = false
   })
}
function buildBoard() { loadSchedules() }

function onDeptChange(v) {
   dialogDoctors.value = doctorOptions.value.filter(d => d.deptId === v)
   form.value.doctorId = undefined
}

function addSlot(doc, date) {
   form.value = {
      scheduleId: undefined, deptId: doc.deptId, doctorId: doc.doctorId,
      workDate: date, timeSlot: "0", quota: 20, remainQuota: 20, status: "0", remark: undefined
   }
   dialogDoctors.value = doctorOptions.value.filter(d => d.deptId === doc.deptId)
   title.value = `${doc.doctorName} · ${date} 排班`
   open.value = true
}

function editSlot(slot) {
   form.value = { ...slot }
   dialogDoctors.value = doctorOptions.value.filter(d => d.deptId === slot.deptId)
   title.value = `编辑排班 · ${slot.doctorName} ${slot.workDate} ${slotLabel(slot.timeSlot)}`
   open.value = true
}

function syncRemain(v) { if (form.value.remainQuota > v) form.value.remainQuota = v }

function submitForm() {
   proxy.$refs["scheduleRef"].validate(valid => {
      if (!valid) return
      const fn = form.value.scheduleId ? updateSchedule : addSchedule
      fn(form.value).then(() => { proxy.$modal.msgSuccess("保存成功"); open.value = false; loadSchedules() })
   })
}

function removeSlot() {
   proxy.$modal.confirm("确认删除该排班？").then(() => delSchedule(form.value.scheduleId))
      .then(() => { proxy.$modal.msgSuccess("删除成功"); open.value = false; loadSchedules() }).catch(() => {})
}

listDepartment({ pageNum: 1, pageSize: 200 }).then(res => deptOptions.value = res.data)
listDoctor({ pageNum: 1, pageSize: 500, status: "0" }).then(res => { doctorOptions.value = res.data; loadSchedules() })
</script>

<style scoped>
.toolbar-card { margin-bottom: 12px; }
.toolbar { display: flex; align-items: center; justify-content: space-between; flex-wrap: wrap; gap: 10px; }
.toolbar .left { display: flex; gap: 10px; }
.week-nav { display: flex; align-items: center; gap: 10px; }
.week-label { font-weight: 600; font-size: 14px; min-width: 190px; text-align: center; }
.legend { display: flex; gap: 14px; font-size: 12px; color: #606266; }
.lg { display: flex; align-items: center; gap: 4px; }
.dot { width: 10px; height: 10px; border-radius: 3px; display: inline-block; }
.dot.open { background: #67c23a; }
.dot.full { background: #e6a23c; }
.dot.stop { background: #f56c6c; }

.board { background: #fff; border-radius: 6px; border: 1px solid #e4e7ed; overflow: hidden; }
.board-head, .board-row { display: grid; grid-template-columns: 200px repeat(7, 1fr); }
.board-head { background: #f5f7fa; border-bottom: 1px solid #e4e7ed; }
.board-head .col-day { padding: 8px 4px; text-align: center; border-left: 1px solid #e4e7ed; }
.board-head .col-day.today { background: #ecf5ff; color: #409eff; }
.dow { font-size: 12px; color: #909399; }
.dnum { font-weight: 600; font-size: 14px; }
.col-doctor { padding: 8px 12px; display: flex; align-items: center; }

.board-row { border-bottom: 1px solid #f0f0f0; min-height: 86px; }
.board-row:last-child { border-bottom: none; }
.board-row .col-day { border-left: 1px solid #f0f0f0; padding: 4px; display: flex; flex-direction: column; gap: 3px; }
.board-row .col-day.today { background: #fbfdff; }

.doc-cell { gap: 10px; }
.avatar {
   width: 38px; height: 38px; border-radius: 50%; background: linear-gradient(135deg, #409eff, #79bbff);
   color: #fff; display: flex; align-items: center; justify-content: center; font-weight: 600; flex-shrink: 0;
}
.dname { font-weight: 600; font-size: 13px; }
.ddept { font-size: 12px; color: #909399; margin-top: 2px; }

.slot {
   border-radius: 4px; padding: 3px 6px; font-size: 12px; cursor: pointer;
   display: flex; justify-content: space-between; align-items: center; transition: transform .1s;
}
.slot:hover { transform: scale(1.03); }
.slot.open { background: #f0f9eb; color: #529b2e; border: 1px solid #c2e7b0; }
.slot.full { background: #fdf6ec; color: #b88230; border: 1px solid #f5dab1; }
.slot.stop { background: #fef0f0; color: #c45656; border: 1px solid #fab6b6; text-decoration: line-through; }
.squota { font-weight: 600; }

.add-btn {
   color: #c0c4cc; border: 1px dashed #dcdfe6; border-radius: 4px; text-align: center;
   padding: 1px 0; cursor: pointer; font-size: 12px; line-height: 18px; opacity: 0; transition: opacity .15s;
}
.cell:hover .add-btn { opacity: 1; }
.add-btn:hover { color: #409eff; border-color: #409eff; }
</style>

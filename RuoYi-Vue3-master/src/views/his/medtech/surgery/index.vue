<template>
   <div class="app-container">
      <div class="topbar">
         <div class="filters">
            <el-input v-model="kw" placeholder="搜索手术名称/患者" clearable prefix-icon="Search" style="width: 200px" />
            <el-date-picker v-model="day" type="date" value-format="YYYY-MM-DD" placeholder="查看某日排台" clearable style="width: 160px" />
         </div>
         <el-button type="primary" icon="Plus" @click="handleAdd" v-hasPermi="['his:surgery:add']">手术申请</el-button>
      </div>

      <!-- 今日手术概览 -->
      <el-row :gutter="12" class="sum-row">
         <el-col :span="6"><div class="sum pending"><b>{{ countOf('0') }}</b><span>已预约</span></div></el-col>
         <el-col :span="6"><div class="sum doing"><b>{{ countOf('1') }}</b><span>手术中</span></div></el-col>
         <el-col :span="6"><div class="sum done"><b>{{ countOf('2') }}</b><span>已完成</span></div></el-col>
         <el-col :span="6"><div class="sum cancel"><b>{{ countOf('3') }}</b><span>已取消</span></div></el-col>
      </el-row>

      <!-- 看板: 按状态分列 -->
      <div class="kanban" v-loading="loading">
         <div v-for="col in columns" :key="col.status" class="kanban-col">
            <div class="col-head" :style="{ borderTopColor: col.color }">
               <span>{{ col.label }}</span>
               <el-tag size="small" :color="col.color" effect="dark" style="border:none">{{ colItems(col.status).length }}</el-tag>
            </div>
            <div class="col-body">
               <div v-for="s in colItems(col.status)" :key="s.surgeryId" class="op-card">
                  <div class="op-name">{{ s.surgeryName }}</div>
                  <div class="op-meta">
                     <span><el-icon><User /></el-icon>{{ s.patientName }}</span>
                     <span><el-icon><OfficeBuilding /></el-icon>{{ s.deptName }}</span>
                  </div>
                  <div class="op-meta">
                     <span><el-icon><UserFilled /></el-icon>{{ s.surgeonName }}</span>
                     <span><el-icon><Monitor /></el-icon>{{ s.roomNo || '未排间' }}</span>
                  </div>
                  <div class="op-tags">
                     <el-tag size="small" effect="plain" type="warning">{{ levelLabel(s.level) }}</el-tag>
                     <el-tag size="small" effect="plain" type="info">{{ anesLabel(s.anesthesia) }}</el-tag>
                  </div>
                  <div class="op-time"><el-icon><Clock /></el-icon>{{ parseTime(s.planTime, '{m}-{d} {h}:{i}') }}</div>
                  <div class="op-ops">
                     <el-button v-if="s.status === '0'" size="small" type="warning" plain @click="flow(s, '1', '开始手术')" v-hasPermi="['his:surgery:edit']">开始手术</el-button>
                     <el-button v-if="s.status === '1'" size="small" type="success" plain @click="flow(s, '2', '完成手术')" v-hasPermi="['his:surgery:edit']">完成</el-button>
                     <el-button v-if="s.status === '0'" size="small" type="danger" link @click="flow(s, '3', '取消手术')" v-hasPermi="['his:surgery:edit']">取消</el-button>
                  </div>
               </div>
               <el-empty v-if="!colItems(col.status).length" description="无" :image-size="50" />
            </div>
         </div>
      </div>

      <!-- 手术申请 -->
      <el-dialog title="手术申请" v-model="open" width="640px" append-to-body>
         <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
            <el-form-item label="手术名称" prop="surgeryName"><el-input v-model="form.surgeryName" placeholder="如 腹腔镜胆囊切除术" /></el-form-item>
            <el-form-item label="患者" prop="patientId">
               <el-select v-model="form.patientId" filterable remote :remote-method="searchPatient" placeholder="输入姓名搜索" style="width:100%">
                  <el-option v-for="p in patientOptions" :key="p.patientId" :label="p.patientName + '(' + p.patientNo + ')'" :value="p.patientId" />
               </el-select>
            </el-form-item>
            <el-row>
               <el-col :span="12">
                  <el-form-item label="科室" prop="deptId">
                     <el-select v-model="form.deptId" filterable style="width:100%">
                        <el-option v-for="d in deptOptions" :key="d.deptId" :label="d.deptName" :value="d.deptId" />
                     </el-select>
                  </el-form-item>
               </el-col>
               <el-col :span="12">
                  <el-form-item label="主刀医生" prop="surgeonId">
                     <el-select v-model="form.surgeonId" filterable style="width:100%">
                        <el-option v-for="d in doctorOptions" :key="d.doctorId" :label="d.doctorName" :value="d.doctorId" />
                     </el-select>
                  </el-form-item>
               </el-col>
               <el-col :span="12">
                  <el-form-item label="手术级别" prop="level">
                     <el-select v-model="form.level" style="width:100%">
                        <el-option v-for="d in his_surgery_level" :key="d.value" :label="d.label" :value="d.value" />
                     </el-select>
                  </el-form-item>
               </el-col>
               <el-col :span="12">
                  <el-form-item label="麻醉方式" prop="anesthesia">
                     <el-select v-model="form.anesthesia" style="width:100%">
                        <el-option v-for="d in his_anesthesia" :key="d.value" :label="d.label" :value="d.value" />
                     </el-select>
                  </el-form-item>
               </el-col>
               <el-col :span="12">
                  <el-form-item label="手术间" prop="roomNo"><el-input v-model="form.roomNo" placeholder="如 3号手术间" /></el-form-item>
               </el-col>
               <el-col :span="12">
                  <el-form-item label="计划时间" prop="planTime">
                     <el-date-picker v-model="form.planTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width:100%" />
                  </el-form-item>
               </el-col>
            </el-row>
         </el-form>
         <template #footer>
            <el-button type="primary" @click="submit">提交申请</el-button>
            <el-button @click="open = false">取 消</el-button>
         </template>
      </el-dialog>
   </div>
</template>

<script setup name="HisSurgery">
import { listSurgery, addSurgery, changeSurgeryStatus } from "@/api/his/surgery"
import { listPatient } from "@/api/his/patient"
import { listDepartment } from "@/api/his/dept"
import { listDoctor } from "@/api/his/doctor"
import { User, OfficeBuilding, UserFilled, Monitor, Clock } from "@element-plus/icons-vue"

const { proxy } = getCurrentInstance()
const { his_surgery_status, his_surgery_level, his_anesthesia } = useDict("his_surgery_status", "his_surgery_level", "his_anesthesia")

const rows = ref([])
const loading = ref(true)
const kw = ref("")
const day = ref()
const open = ref(false)
const form = ref({})
const patientOptions = ref([]), deptOptions = ref([]), doctorOptions = ref([])

const columns = [
   { status: "0", label: "已预约", color: "#e6a23c" },
   { status: "1", label: "手术中", color: "#f56c6c" },
   { status: "2", label: "已完成", color: "#67c23a" },
   { status: "3", label: "已取消", color: "#909399" },
]

const rules = {
   surgeryName: [{ required: true, message: "手术名称不能为空", trigger: "blur" }],
   patientId: [{ required: true, message: "请选择患者", trigger: "change" }],
   planTime: [{ required: true, message: "请选择计划时间", trigger: "change" }]
}

const filtered = computed(() => rows.value.filter(s => {
   if (kw.value && !(s.surgeryName?.includes(kw.value) || s.patientName?.includes(kw.value))) return false
   if (day.value && (s.planTime || "").slice(0, 10) !== day.value) return false
   return true
}))

function colItems(status) { return filtered.value.filter(s => s.status === status) }
function countOf(status) { return filtered.value.filter(s => s.status === status).length }
function levelLabel(v) { return his_surgery_level.value.find(x => x.value === v)?.label || v }
function anesLabel(v) { return his_anesthesia.value.find(x => x.value === v)?.label || v }

function getList() {
   loading.value = true
   listSurgery({ pageNum: 1, pageSize: 200 }).then(res => { rows.value = res.data; loading.value = false })
}

function searchPatient(kw2) { listPatient({ pageNum: 1, pageSize: 50, patientName: kw2 || undefined }).then(res => patientOptions.value = res.data) }

function handleAdd() {
   form.value = { surgeryName: undefined, patientId: undefined, deptId: undefined, surgeonId: undefined, level: '0', anesthesia: '0', roomNo: undefined, planTime: undefined }
   searchPatient("")
   open.value = true
}

function submit() {
   proxy.$refs["formRef"].validate(valid => {
      if (!valid) return
      addSurgery({ ...form.value, status: '0' }).then(() => { proxy.$modal.msgSuccess("手术申请已提交"); open.value = false; getList() })
   })
}

function flow(row, status, action) {
   proxy.$modal.confirm(`确认${action}「${row.surgeryName}」？`).then(() => changeSurgeryStatus(row.surgeryId, status)).then(() => { proxy.$modal.msgSuccess(`${action}成功`); getList() }).catch(() => {})
}

listDepartment({ pageNum: 1, pageSize: 100 }).then(res => deptOptions.value = res.data)
listDoctor({ pageNum: 1, pageSize: 200 }).then(res => doctorOptions.value = res.data)
getList()
</script>

<style scoped>
.topbar { display: flex; justify-content: space-between; margin-bottom: 12px; flex-wrap: wrap; gap: 10px; }
.filters { display: flex; gap: 10px; }
.sum-row { margin-bottom: 12px; }
.sum { border-radius: 8px; padding: 14px; text-align: center; background: #fff; border: 1px solid #e4e7ed; }
.sum b { display: block; font-size: 24px; }
.sum span { font-size: 12px; color: #909399; }
.sum.pending b { color: #e6a23c; }
.sum.doing b { color: #f56c6c; }
.sum.done b { color: #67c23a; }
.sum.cancel b { color: #909399; }

.kanban { display: grid; grid-template-columns: repeat(4, 1fr); gap: 12px; align-items: start; }
.kanban-col { background: #f5f7fa; border-radius: 8px; overflow: hidden; }
.col-head {
   padding: 10px 12px; font-weight: 600; font-size: 14px; display: flex; justify-content: space-between;
   align-items: center; background: #fff; border-top: 3px solid transparent;
}
.col-body { padding: 10px; display: flex; flex-direction: column; gap: 10px; min-height: 200px; }
.op-card { background: #fff; border-radius: 6px; padding: 12px; border: 1px solid #e4e7ed; }
.op-name { font-weight: 700; font-size: 14px; margin-bottom: 8px; }
.op-meta { display: flex; gap: 14px; font-size: 12px; color: #606266; margin-bottom: 4px; }
.op-meta span { display: flex; align-items: center; gap: 3px; }
.op-tags { display: flex; gap: 6px; margin: 6px 0; }
.op-time { font-size: 12px; color: #909399; display: flex; align-items: center; gap: 4px; }
.op-ops { margin-top: 8px; display: flex; gap: 6px; }
</style>

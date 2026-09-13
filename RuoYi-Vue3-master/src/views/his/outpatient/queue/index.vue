<template>
   <div class="app-container">
      <el-form :inline="true">
         <el-form-item label="就诊日期">
            <el-date-picker v-model="queryDate" type="date" value-format="YYYY-MM-DD" style="width: 150px" @change="getQueue" />
         </el-form-item>
         <el-form-item label="科室">
            <el-select v-model="queryDept" clearable placeholder="全部科室" style="width: 150px" @change="getQueue">
               <el-option v-for="d in deptOptions" :key="d.deptId" :label="d.deptName" :value="d.deptId" />
            </el-select>
         </el-form-item>
         <el-form-item label="医生">
            <el-select v-model="queryDoctor" clearable placeholder="全部医生" style="width: 150px" @change="getQueue">
               <el-option v-for="d in doctorOptions" :key="d.doctorId" :label="d.doctorName" :value="d.doctorId" />
            </el-select>
         </el-form-item>
         <el-form-item>
            <el-button type="primary" icon="Refresh" @click="getQueue">刷新队列</el-button>
         </el-form-item>
      </el-form>

      <el-row :gutter="16" v-loading="loading">
         <!-- 候诊中 -->
         <el-col :span="8">
            <el-card shadow="never">
               <template #header><el-tag type="info" effect="dark">候诊中 ({{ waitingList.length }})</el-tag></template>
               <div v-for="r in waitingList" :key="r.regId" class="q-card">
                  <div class="q-no">No.{{ r.queueNo }}</div>
                  <div class="q-info">
                     <div class="q-name">{{ r.patientName }} <span class="q-sub">{{ r.gender === '0' ? '男' : '女' }} · {{ r.age }}岁</span></div>
                     <div class="q-sub">{{ r.deptName }} · {{ r.doctorName }} · {{ slotText(r.timeSlot) }}</div>
                  </div>
                  <div class="q-ops">
                     <el-button size="small" type="primary" @click="call(r)" v-hasPermi="['his:queue:call']">叫号</el-button>
                     <el-button size="small" type="warning" plain @click="pass(r)" v-hasPermi="['his:queue:call']">过号</el-button>
                  </div>
               </div>
               <el-empty v-if="!waitingList.length" description="无候诊患者" :image-size="60" />
            </el-card>
         </el-col>
         <!-- 就诊中 -->
         <el-col :span="8">
            <el-card shadow="never">
               <template #header><el-tag type="primary" effect="dark">就诊中 ({{ visitingList.length }})</el-tag></template>
               <div v-for="r in visitingList" :key="r.regId" class="q-card q-visiting">
                  <div class="q-no">No.{{ r.queueNo }}</div>
                  <div class="q-info">
                     <div class="q-name">{{ r.patientName }} <span class="q-sub">{{ r.gender === '0' ? '男' : '女' }} · {{ r.age }}岁</span></div>
                     <div class="q-sub">{{ r.deptName }} · {{ r.doctorName }}</div>
                  </div>
                  <div class="q-ops">
                     <el-button size="small" type="warning" plain @click="pass(r)" v-hasPermi="['his:queue:call']">过号</el-button>
                  </div>
               </div>
               <el-empty v-if="!visitingList.length" description="暂无就诊" :image-size="60" />
            </el-card>
         </el-col>
         <!-- 过号 -->
         <el-col :span="8">
            <el-card shadow="never">
               <template #header><el-tag type="warning" effect="dark">已过号 ({{ passedList.length }})</el-tag></template>
               <div v-for="r in passedList" :key="r.regId" class="q-card q-passed">
                  <div class="q-no">No.{{ r.queueNo }}</div>
                  <div class="q-info">
                     <div class="q-name">{{ r.patientName }}</div>
                     <div class="q-sub">{{ r.deptName }} · {{ r.doctorName }}</div>
                  </div>
                  <div class="q-ops">
                     <el-button size="small" type="success" plain @click="recover(r)" v-hasPermi="['his:queue:call']">恢复</el-button>
                  </div>
               </div>
               <el-empty v-if="!passedList.length" description="无过号" :image-size="60" />
            </el-card>
         </el-col>
      </el-row>
   </div>
</template>

<script setup name="HisQueue">
import { listQueue, changeRegStatus } from "@/api/his/registration"
import { listDepartment } from "@/api/his/dept"
import { listDoctor } from "@/api/his/doctor"

const { proxy } = getCurrentInstance()
const { his_time_slot } = useDict("his_time_slot")

const queryDate = ref(new Date().toISOString().slice(0, 10))
const queryDept = ref()
const queryDoctor = ref()
const deptOptions = ref([])
const doctorOptions = ref([])
const queueList = ref([])
const loading = ref(false)

const waitingList = computed(() => queueList.value.filter(r => r.visitStatus === '0'))
const visitingList = computed(() => queueList.value.filter(r => r.visitStatus === '1'))
const passedList = computed(() => queueList.value.filter(r => r.visitStatus === '4'))

function slotText(v) { return (his_time_slot.value.find(d => d.value === v) || {}).label || v }

function getQueue() {
  loading.value = true
  listQueue({ regDate: queryDate.value, deptId: queryDept.value, doctorId: queryDoctor.value }).then(res => {
    queueList.value = res.data
    loading.value = false
  })
}

function call(r) {
  changeRegStatus(r.regId, '1').then(() => {
    proxy.$modal.msgSuccess(`请 ${r.queueNo} 号 ${r.patientName} 到 ${r.deptName} 就诊`)
    getQueue()
  })
}
function pass(r) {
  changeRegStatus(r.regId, '4').then(() => { proxy.$modal.msgWarning("已过号"); getQueue() })
}
function recover(r) {
  changeRegStatus(r.regId, '0').then(() => { proxy.$modal.msgSuccess("已恢复待诊"); getQueue() })
}

listDepartment({ pageNum: 1, pageSize: 100 }).then(res => deptOptions.value = res.data)
listDoctor({ pageNum: 1, pageSize: 200 }).then(res => doctorOptions.value = res.data)
getQueue()
</script>

<style scoped>
.q-card { display: flex; align-items: center; padding: 10px; border: 1px solid #ebeef5; border-radius: 8px; margin-bottom: 10px; }
.q-visiting { border-color: #409eff; background: #ecf5ff; }
.q-passed { opacity: .75; }
.q-no { font-size: 18px; font-weight: 700; color: #409eff; width: 64px; }
.q-info { flex: 1; }
.q-name { font-weight: 600; }
.q-sub { font-size: 12px; color: #909399; }
</style>

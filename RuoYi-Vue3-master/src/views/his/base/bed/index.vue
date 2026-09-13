<template>
   <div class="app-container">
      <el-row :gutter="16">
         <!-- 左侧病区列表 -->
         <el-col :span="5">
            <el-card shadow="never" header="病区列表">
               <div
                  v-for="w in wardList" :key="w.wardId"
                  class="ward-item" :class="{ active: currentWard === w.wardId }"
                  @click="selectWard(w)"
               >
                  <div class="ward-name">{{ w.wardName }}</div>
                  <div class="ward-info">{{ w.deptName }} · {{ w.location }}</div>
               </div>
            </el-card>
         </el-col>

         <!-- 右侧床位图 -->
         <el-col :span="19">
            <el-card shadow="never">
               <template #header>
                  <div style="display:flex;justify-content:space-between;align-items:center">
                     <span>{{ wardName }} - 床位分布</span>
                     <div>
                        <el-tag type="success" effect="plain">空闲 {{ stat.free }}</el-tag>
                        <el-tag type="danger" effect="plain" style="margin-left:8px">占用 {{ stat.used }}</el-tag>
                        <el-tag type="warning" effect="plain" style="margin-left:8px">维修 {{ stat.fix }}</el-tag>
                        <el-button style="margin-left:12px" type="primary" plain icon="Plus" @click="handleAddBed" v-hasPermi="['his:bed:add']">添加床位</el-button>
                     </div>
                  </div>
               </template>

               <el-row :gutter="12" v-loading="loading">
                  <el-col :span="4" v-for="bed in bedList" :key="bed.bedId" style="margin-bottom:12px">
                     <div class="bed-card" :class="'bed-' + bed.status" @click="handleBedClick(bed)">
                        <div class="bed-no">{{ bed.bedNo }}</div>
                        <div class="bed-type">{{ bedTypeText(bed.bedType) }}</div>
                        <div class="bed-status">{{ bedStatusText(bed.status) }}</div>
                        <div class="bed-patient" v-if="bed.patientName">{{ bed.patientName }}</div>
                        <div class="bed-price">¥{{ bed.pricePerDay }}/天</div>
                     </div>
                  </el-col>
                  <el-empty v-if="!loading && bedList.length === 0" description="该病区暂无床位" />
               </el-row>
            </el-card>
         </el-col>
      </el-row>

      <!-- 添加/修改床位 -->
      <el-dialog :title="bedTitle" v-model="bedOpen" width="480px" append-to-body>
         <el-form ref="bedRef" :model="bedForm" :rules="bedRules" label-width="100px">
            <el-form-item label="床号" prop="bedNo"><el-input v-model="bedForm.bedNo" placeholder="如 01-09" /></el-form-item>
            <el-form-item label="床位类型" prop="bedType">
               <el-select v-model="bedForm.bedType">
                  <el-option v-for="d in his_bed_type" :key="d.value" :label="d.label" :value="d.value" />
               </el-select>
            </el-form-item>
            <el-form-item label="每日价格" prop="pricePerDay"><el-input-number v-model="bedForm.pricePerDay" :precision="2" :min="0" controls-position="right" /></el-form-item>
            <el-form-item label="状态" prop="status">
               <el-select v-model="bedForm.status">
                  <el-option v-for="d in his_bed_status" :key="d.value" :label="d.label" :value="d.value" />
               </el-select>
            </el-form-item>
         </el-form>
         <template #footer>
            <el-button type="primary" @click="submitBed">确 定</el-button>
            <el-button @click="bedOpen = false">取 消</el-button>
         </template>
      </el-dialog>

      <!-- 床位操作: 空闲->住院登记 / 占用->查看在住信息 / 维修预留->恢复 -->
      <el-dialog :title="`床位 ${opBed.bedNo}`" v-model="opOpen" width="520px" append-to-body>
         <template v-if="opBed.status === '0'">
            <el-alert title="该床位空闲，可直接办理住院登记" type="success" :closable="false" style="margin-bottom:12px" />
            <el-form ref="admRef" :model="admForm" :rules="admRules" label-width="100px">
               <el-form-item label="患者" prop="patientId">
                  <el-select v-model="admForm.patientId" filterable remote :remote-method="searchPatient" placeholder="输入姓名/卡号搜索" style="width:100%">
                     <el-option v-for="p in patientOptions" :key="p.patientId" :label="p.patientName + '(' + p.patientNo + ')'" :value="p.patientId" />
                  </el-select>
               </el-form-item>
               <el-form-item label="主治医生" prop="doctorId">
                  <el-select v-model="admForm.doctorId" filterable style="width:100%">
                     <el-option v-for="d in doctorOptions" :key="d.doctorId" :label="d.doctorName" :value="d.doctorId" />
                  </el-select>
               </el-form-item>
               <el-form-item label="预缴押金" prop="deposit"><el-input-number v-model="admForm.deposit" :precision="2" :min="0" controls-position="right" /></el-form-item>
               <el-form-item label="入院诊断" prop="diagnosisIn"><el-input v-model="admForm.diagnosisIn" type="textarea" :rows="2" /></el-form-item>
            </el-form>
         </template>
         <template v-else-if="opBed.status === '1'">
            <el-descriptions :column="2" border>
               <el-descriptions-item label="床号">{{ opBed.bedNo }}</el-descriptions-item>
               <el-descriptions-item label="在住患者">{{ opBed.patientName }}</el-descriptions-item>
               <el-descriptions-item label="床位类型">{{ bedTypeText(opBed.bedType) }}</el-descriptions-item>
               <el-descriptions-item label="日租">¥{{ opBed.pricePerDay }}/天</el-descriptions-item>
            </el-descriptions>
            <el-alert title="出院请前往 住院管理->出院结算 办理" type="info" :closable="false" style="margin-top:12px" />
         </template>
         <template v-else>
            <el-alert :title="`该床位当前为【${bedStatusText(opBed.status)}】状态`" type="warning" :closable="false" style="margin-bottom:12px" />
         </template>
         <template #footer>
            <el-button v-if="opBed.status === '0'" type="primary" @click="submitAdmit" v-hasPermi="['his:bed:edit']">办理住院</el-button>
            <el-button v-else-if="opBed.status !== '1'" type="success" @click="recoverBed" v-hasPermi="['his:bed:edit']">恢复空闲</el-button>
            <el-button @click="opOpen = false">{{ opBed.status === '0' ? '取 消' : '关 闭' }}</el-button>
         </template>
      </el-dialog>
   </div>
</template>

<script setup name="HisBed">
import { listWard } from "@/api/his/ward"
import { listBed, addBed, updateBed, changeBedStatus, getBed } from "@/api/his/bed"
import { listPatient } from "@/api/his/patient"
import { listDoctor } from "@/api/his/doctor"
import { admit } from "@/api/his/admission"

const { proxy } = getCurrentInstance()
const { his_bed_type, his_bed_status } = useDict("his_bed_type", "his_bed_status")

const wardList = ref([])
const bedList = ref([])
const currentWard = ref()
const currentWardDept = ref()
const wardName = ref("")
const loading = ref(false)
const stat = ref({ free: 0, used: 0, fix: 0 })

const bedOpen = ref(false), bedTitle = ref(""), opOpen = ref(false)
const bedForm = ref({})
const opBed = ref({})
const admForm = ref({})
const patientOptions = ref([])
const doctorOptions = ref([])

const bedRules = { bedNo: [{ required: true, message: "床号不能为空", trigger: "blur" }] }
const admRules = {
  patientId: [{ required: true, message: "请选择患者", trigger: "change" }],
  doctorId: [{ required: true, message: "请选择主治医生", trigger: "change" }],
  deposit: [{ required: true, message: "押金不能为空", trigger: "blur" }]
}

function bedTypeText(v) { return (his_bed_type.value.find(d => d.value === v) || {}).label || v }
function bedStatusText(v) { return (his_bed_status.value.find(d => d.value === v) || {}).label || v }

function getWards() {
  listWard({ pageNum: 1, pageSize: 100 }).then(res => {
    wardList.value = res.data
    if (res.data.length && !currentWard.value) selectWard(res.data[0])
  })
}

function selectWard(w) {
  currentWard.value = w.wardId
  currentWardDept.value = w.deptId
  wardName.value = w.wardName
  getBeds()
}

function getBeds() {
  loading.value = true
  listBed({ pageNum: 1, pageSize: 200, wardId: currentWard.value }).then(res => {
    bedList.value = res.data
    stat.value = {
      free: res.data.filter(b => b.status === '0').length,
      used: res.data.filter(b => b.status === '1').length,
      fix: res.data.filter(b => b.status === '2' || b.status === '3').length
    }
    loading.value = false
  })
}

function handleBedClick(bed) {
  opBed.value = bed
  opOpen.value = true
  if (bed.status === '0') {
    admForm.value = { patientId: undefined, doctorId: undefined, deposit: 3000, diagnosisIn: undefined }
    searchPatient("")
    listDoctor({ pageNum: 1, pageSize: 200 }).then(res => doctorOptions.value = res.data)
  }
}

function searchPatient(kw) {
  listPatient({ pageNum: 1, pageSize: 50, patientName: kw || undefined, patientNo: kw || undefined }).then(res => patientOptions.value = res.data)
}

/** 空闲床位 -> 直接办理住院(联动 admission) */
function submitAdmit() {
  proxy.$refs["admRef"].validate(valid => {
    if (!valid) return
    admit({
      patientId: admForm.value.patientId,
      deptId: currentWardDept.value,
      wardId: currentWard.value,
      bedId: opBed.value.bedId,
      doctorId: admForm.value.doctorId,
      deposit: admForm.value.deposit,
      diagnosisIn: admForm.value.diagnosisIn
    }).then(() => {
      proxy.$modal.msgSuccess("住院登记成功，床位已占用")
      opOpen.value = false
      getBeds()
    })
  })
}

function recoverBed() {
  changeBedStatus(opBed.value.bedId, '0').then(() => { proxy.$modal.msgSuccess("已恢复空闲"); opOpen.value = false; getBeds() })
}

function handleAddBed() {
  bedForm.value = { bedId: undefined, wardId: currentWard.value, bedNo: undefined, bedType: '0', pricePerDay: 60, status: '0' }
  bedOpen.value = true
  bedTitle.value = "添加床位"
}

function submitBed() {
  proxy.$refs["bedRef"].validate(valid => {
    if (!valid) return
    const fn = bedForm.value.bedId ? updateBed : addBed
    fn(bedForm.value).then(() => { proxy.$modal.msgSuccess("保存成功"); bedOpen.value = false; getBeds() })
  })
}

getWards()
</script>

<style scoped>
.ward-item { padding: 10px 12px; border-radius: 6px; cursor: pointer; margin-bottom: 6px; border: 1px solid #ebeef5; }
.ward-item:hover { border-color: #409eff; }
.ward-item.active { background: #ecf5ff; border-color: #409eff; }
.ward-name { font-weight: 600; font-size: 14px; }
.ward-info { font-size: 12px; color: #909399; margin-top: 4px; }
.bed-card { border-radius: 8px; padding: 10px; text-align: center; cursor: pointer; border: 2px solid; transition: transform .15s; min-height: 110px; }
.bed-card:hover { transform: translateY(-3px); }
.bed-0 { border-color: #67c23a; background: #f0f9eb; }
.bed-1 { border-color: #f56c6c; background: #fef0f0; }
.bed-2 { border-color: #e6a23c; background: #fdf6ec; }
.bed-3 { border-color: #909399; background: #f4f4f5; }
.bed-no { font-size: 16px; font-weight: 700; }
.bed-type, .bed-status { font-size: 12px; color: #606266; margin-top: 4px; }
.bed-patient { font-size: 13px; font-weight: 600; color: #f56c6c; margin-top: 4px; }
.bed-price { font-size: 12px; color: #909399; margin-top: 4px; }
</style>

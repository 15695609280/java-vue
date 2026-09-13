<template>
   <div class="app-container workstation">
      <el-row :gutter="12" style="height: calc(100vh - 130px)">
         <!-- 左：医生选择 + 今日候诊队列 -->
         <el-col :span="6" style="height:100%">
            <el-card shadow="never" style="height:100%" :body-style="{padding:'10px', height:'calc(100% - 110px)', overflow:'auto'}">
               <el-form label-position="top">
                  <el-form-item label="当前坐诊医生">
                     <el-select v-model="doctorId" filterable style="width:100%" @change="loadQueue">
                        <el-option v-for="d in doctorOptions" :key="d.doctorId" :label="d.doctorName + ' (' + d.deptName + ')'" :value="d.doctorId" />
                     </el-select>
                  </el-form-item>
                  <el-form-item label="就诊日期">
                     <el-date-picker v-model="workDate" type="date" value-format="YYYY-MM-DD" style="width:100%" @change="loadQueue" />
                  </el-form-item>
               </el-form>
               <div class="q-title">候诊队列({{ queueList.length }})</div>
               <div v-for="r in queueList" :key="r.regId" class="ws-q-item" :class="{active: curReg && curReg.regId === r.regId, done: r.visitStatus === '2'}" @click="pickReg(r)">
                  <div class="ws-q-no">{{ r.queueNo }}</div>
                  <div style="flex:1">
                     <div>{{ r.patientName }} <span class="ws-sub">{{ r.gender === '0' ? '男' : '女' }}·{{ r.age }}岁</span></div>
                     <div class="ws-sub">{{ slotText(r.timeSlot) }}</div>
                  </div>
                  <dict-tag :options="his_reg_status" :value="r.visitStatus" />
               </div>
               <el-empty v-if="!queueList.length" description="今日无候诊" :image-size="60" />
            </el-card>
         </el-col>

         <!-- 右：接诊操作台 -->
         <el-col :span="18" style="height:100%">
            <el-card shadow="never" style="height:100%" :body-style="{height:'calc(100% - 57px)', overflow:'auto'}">
               <template #header>
                  <div style="display:flex;justify-content:space-between;align-items:center">
                     <span v-if="curReg">接诊台 - {{ curReg.patientName }}({{ curReg.patientNo }})</span>
                     <span v-else>请从左侧选择候诊患者</span>
                     <div v-if="curReg">
                        <el-button v-if="curReg.visitStatus === '0' || curReg.visitStatus === '4'" type="primary" size="small" @click="startVisit" v-hasPermi="['his:workstation:visit']">接诊</el-button>
                        <el-button v-if="curVisit && curVisit.visitId" type="success" size="small" @click="finish" v-hasPermi="['his:workstation:visit']">完诊</el-button>
                     </div>
                  </div>
               </template>

               <el-empty v-if="!curReg" description="选择左侧候诊患者开始接诊" />
               <template v-else>
                  <el-descriptions :column="4" border size="small" style="margin-bottom:12px">
                     <el-descriptions-item label="患者">{{ curReg.patientName }}</el-descriptions-item>
                     <el-descriptions-item label="性别">{{ curReg.gender === '0' ? '男' : '女' }}</el-descriptions-item>
                     <el-descriptions-item label="年龄">{{ curReg.age }}岁</el-descriptions-item>
                     <el-descriptions-item label="排队号">No.{{ curReg.queueNo }}</el-descriptions-item>
                  </el-descriptions>

                  <el-tabs v-model="tab">
                     <!-- 病历 -->
                     <el-tab-pane label="门诊病历" name="emr">
                        <el-form :model="visitForm" label-width="90px" :disabled="!canEdit">
                           <el-form-item label="主诉"><el-input v-model="visitForm.chiefComplaint" type="textarea" :rows="2" placeholder="患者主要症状及持续时间" /></el-form-item>
                           <el-form-item label="现病史"><el-input v-model="visitForm.presentIllness" type="textarea" :rows="3" /></el-form-item>
                           <el-form-item label="既往史"><el-input v-model="visitForm.pastIllness" type="textarea" :rows="2" /></el-form-item>
                           <el-form-item label="诊断"><el-input v-model="visitForm.diagnosis" placeholder="初步诊断结论" /></el-form-item>
                           <el-form-item label="处理意见"><el-input v-model="visitForm.treatment" type="textarea" :rows="2" /></el-form-item>
                           <el-form-item v-if="canEdit">
                              <el-button type="primary" @click="saveVisit" v-hasPermi="['his:workstation:visit']">保存病历</el-button>
                           </el-form-item>
                        </el-form>
                     </el-tab-pane>

                     <!-- 处方 -->
                     <el-tab-pane label="开具处方" name="rx">
                        <el-alert v-if="!curVisit" title="请先保存病历再开处方" type="warning" :closable="false" />
                        <template v-else>
                           <el-table :data="rxItems" border size="small" style="margin-bottom:10px">
                              <el-table-column label="药品" min-width="220">
                                 <template #default="s">
                                    <el-select v-model="s.row.drugId" filterable placeholder="选择药品" @change="onDrugPick(s.row)">
                                       <el-option v-for="d in drugOptions" :key="d.drugId" :label="`${d.drugName} ${d.specification} ¥${d.price} (存${d.stock})`" :value="d.drugId" />
                                    </el-select>
                                 </template>
                              </el-table-column>
                              <el-table-column label="单价" width="90" prop="price" />
                              <el-table-column label="数量" width="120">
                                 <template #default="s"><el-input-number v-model="s.row.quantity" :min="1" size="small" controls-position="right" @change="calcItem(s.row)" /></template>
                              </el-table-column>
                              <el-table-column label="用法用量" width="130"><template #default="s"><el-input v-model="s.row.usageDose" size="small" placeholder="如 每次1片" /></template></el-table-column>
                              <el-table-column label="频次" width="120">
                                 <template #default="s">
                                    <el-select v-model="s.row.frequency" size="small">
                                       <el-option label="每日一次" value="每日一次" /><el-option label="每日两次" value="每日两次" /><el-option label="每日三次" value="每日三次" /><el-option label="必要时" value="必要时" />
                                    </el-select>
                                 </template>
                              </el-table-column>
                              <el-table-column label="天数" width="90"><template #default="s"><el-input-number v-model="s.row.days" :min="1" size="small" controls-position="right" /></template></el-table-column>
                              <el-table-column label="金额" width="90"><template #default="s">¥{{ s.row.amount || 0 }}</template></el-table-column>
                              <el-table-column width="60"><template #default="s"><el-button link type="danger" @click="rxItems.splice(s.$index, 1)">删</el-button></template></el-table-column>
                           </el-table>
                           <div style="display:flex;justify-content:space-between;align-items:center">
                              <el-button icon="Plus" @click="rxItems.push({ quantity: 1, days: 7, frequency: '每日三次' })">添加药品</el-button>
                              <div>
                                 <span style="margin-right:16px;font-size:16px">合计 <b style="color:#f56c6c">¥{{ rxTotal }}</b></span>
                                 <el-button type="primary" @click="submitRx" v-hasPermi="['his:workstation:rx']">提交处方(生成待缴费)</el-button>
                              </div>
                           </div>
                           <el-divider>该患者历史处方</el-divider>
                           <el-table :data="hisRxList" size="small" border>
                              <el-table-column prop="rxNo" label="处方号" width="120" />
                              <el-table-column prop="totalAmount" label="金额" width="90" />
                              <el-table-column label="状态" width="100"><template #default="s"><dict-tag :options="his_rx_status" :value="s.row.status" /></template></el-table-column>
                              <el-table-column prop="createTime" label="开立时间"><template #default="s"><span>{{ parseTime(s.row.createTime) }}</span></template></el-table-column>
                           </el-table>
                        </template>
                     </el-tab-pane>

                     <!-- 检验申请 -->
                     <el-tab-pane label="检验申请" name="lab">
                        <el-form :inline="true" :disabled="!canEdit">
                           <el-form-item label="检验项目">
                              <el-select v-model="labItem" filterable placeholder="选择检验项目" style="width: 200px">
                                 <el-option v-for="f in labFeeItems" :key="f.itemId" :label="`${f.itemName} ¥${f.price}`" :value="f.itemName" />
                              </el-select>
                           </el-form-item>
                           <el-form-item label="标本">
                              <el-select v-model="labSample" style="width: 120px">
                                 <el-option v-for="d in his_sample_type" :key="d.value" :label="d.label" :value="d.value" />
                              </el-select>
                           </el-form-item>
                           <el-form-item><el-button type="primary" @click="applyLab" v-hasPermi="['his:workstation:visit']">申请检验</el-button></el-form-item>
                        </el-form>
                        <el-table :data="labList" size="small" border>
                           <el-table-column prop="testNo" label="单号" width="110" />
                           <el-table-column prop="testItem" label="项目" />
                           <el-table-column label="标本" width="80"><template #default="s"><dict-tag :options="his_sample_type" :value="s.row.sampleType" /></template></el-table-column>
                           <el-table-column label="状态" width="90"><template #default="s"><dict-tag :options="his_lab_status" :value="s.row.status" /></template></el-table-column>
                           <el-table-column prop="resultSummary" label="结论" :show-overflow-tooltip="true" />
                        </el-table>
                     </el-tab-pane>

                     <!-- 检查申请 -->
                     <el-tab-pane label="检查申请" name="exam">
                        <el-form :inline="true" :disabled="!canEdit">
                           <el-form-item label="检查类型">
                              <el-select v-model="examType" style="width: 130px">
                                 <el-option v-for="d in his_exam_type" :key="d.value" :label="d.label" :value="d.value" />
                              </el-select>
                           </el-form-item>
                           <el-form-item label="部位"><el-input v-model="examPart" placeholder="如 胸部/头颅" style="width: 140px" /></el-form-item>
                           <el-form-item label="检查项目">
                              <el-select v-model="examFeeItem" filterable placeholder="选择项目(定费)" style="width: 190px">
                                 <el-option v-for="f in examFeeItems" :key="f.itemId" :label="`${f.itemName} ¥${f.price}`" :value="f.itemId" />
                              </el-select>
                           </el-form-item>
                           <el-form-item label="目的"><el-input v-model="examPurpose" placeholder="检查目的" style="width: 160px" /></el-form-item>
                           <el-form-item><el-button type="primary" @click="applyExam" v-hasPermi="['his:workstation:visit']">申请检查</el-button></el-form-item>
                        </el-form>
                        <el-table :data="examList" size="small" border>
                           <el-table-column prop="examNo" label="单号" width="110" />
                           <el-table-column label="类型" width="90"><template #default="s"><dict-tag :options="his_exam_type" :value="s.row.examType" /></template></el-table-column>
                           <el-table-column prop="bodyPart" label="部位" width="90" />
                           <el-table-column label="状态" width="90"><template #default="s"><dict-tag :options="his_exam_status" :value="s.row.status" /></template></el-table-column>
                           <el-table-column prop="conclusion" label="结论" :show-overflow-tooltip="true" />
                        </el-table>
                     </el-tab-pane>
                  </el-tabs>
               </template>
            </el-card>
         </el-col>
      </el-row>
   </div>
</template>

<script setup name="HisWorkstation">
import { listQueue, changeRegStatus } from "@/api/his/registration"
import { listDoctor } from "@/api/his/doctor"
import { listDrug } from "@/api/his/drug"
import { listFeeItem } from "@/api/his/feeitem"
import { addVisit, updateVisit, finishVisit, listVisit } from "@/api/his/visit"
import { createPrescription, listPrescription } from "@/api/his/prescription"
import { addLabTest, listLabTest } from "@/api/his/lab"
import { addExam, listExam } from "@/api/his/exam"

const { proxy } = getCurrentInstance()
const { his_reg_status, his_time_slot, his_rx_status, his_sample_type, his_lab_status, his_exam_type, his_exam_status } =
   useDict("his_reg_status", "his_time_slot", "his_rx_status", "his_sample_type", "his_lab_status", "his_exam_type", "his_exam_status")

const doctorId = ref()
const workDate = ref(new Date().toISOString().slice(0, 10))
const doctorOptions = ref([])
const drugOptions = ref([])
const labFeeItems = ref([])
const queueList = ref([])
const curReg = ref()
const curVisit = ref()
const visitForm = ref({})
const tab = ref("emr")
const rxItems = ref([])
const hisRxList = ref([])
const labItem = ref(), labSample = ref('0'), labList = ref([])
const examType = ref('0'), examPart = ref(''), examPurpose = ref(''), examList = ref([]), examFeeItem = ref(), examFeeItems = ref([])

const canEdit = computed(() => curReg.value && (curReg.value.visitStatus === '1' || curReg.value.visitStatus === '0'))
const rxTotal = computed(() => rxItems.value.reduce((s, i) => s + (i.amount || 0), 0).toFixed(2))

function slotText(v) { return (his_time_slot.value.find(d => d.value === v) || {}).label || v }

function loadQueue() {
  if (!doctorId.value) return
  listQueue({ doctorId: doctorId.value, regDate: workDate.value }).then(res => {
    queueList.value = res.data.filter(r => ['0','1','2','4'].includes(r.visitStatus))
  })
}

/** 选中候诊患者：加载其本次就诊记录/处方/检验/检查 */
function pickReg(r) {
  curReg.value = r
  curVisit.value = null
  visitForm.value = {}
  rxItems.value = []
  tab.value = "emr"
  loadPatientData(r)
}

function loadPatientData(r) {
  listVisit({ patientId: r.patientId, regId: r.regId, pageSize: 10 }).then(res => {
    const v = res.data.find(x => x.regId === r.regId)
    if (v) { curVisit.value = v; visitForm.value = { ...v } }
  })
  listPrescription({ patientId: r.patientId, pageSize: 20 }).then(res => hisRxList.value = res.data)
  listLabTest({ patientId: r.patientId, pageSize: 20 }).then(res => labList.value = res.data)
  listExam({ patientId: r.patientId, pageSize: 20 }).then(res => examList.value = res.data)
}

/** 接诊：挂号->就诊中 */
function startVisit() {
  changeRegStatus(curReg.value.regId, '1').then(() => {
    curReg.value.visitStatus = '1'
    proxy.$modal.msgSuccess("已接诊，请书写病历")
  })
}

function saveVisit() {
  const payload = {
    ...visitForm.value,
    regId: curReg.value.regId,
    patientId: curReg.value.patientId,
    doctorId: doctorId.value,
    deptId: curReg.value.deptId,
    visitTime: new Date().toISOString().slice(0, 19).replace('T', ' '),
    status: '0'
  }
  if (curVisit.value && curVisit.value.visitId) {
    payload.visitId = curVisit.value.visitId
    updateVisit(payload).then(() => proxy.$modal.msgSuccess("病历已保存"))
  } else {
    payload.visitNo = "JZ" + Date.now()
    addVisit(payload).then(() => {
      listVisit({ patientId: curReg.value.patientId, regId: curReg.value.regId, pageSize: 1 }).then(res => {
        curVisit.value = res.data.find(x => x.regId === curReg.value.regId)
        proxy.$modal.msgSuccess("病历已保存")
      })
    })
  }
}

function finish() {
  finishVisit(curVisit.value.visitId).then(() => {
    proxy.$modal.msgSuccess("已完诊")
    curReg.value.visitStatus = '2'
    curVisit.value = null
    visitForm.value = {}
    loadQueue()
  })
}

/** 处方编辑器 */
function onDrugPick(row) {
  const d = drugOptions.value.find(x => x.drugId === row.drugId)
  if (d) { row.drugName = d.drugName; row.specification = d.specification; row.unit = d.unit; row.price = d.price }
  calcItem(row)
}
function calcItem(row) { row.amount = ((row.price || 0) * (row.quantity || 0)).toFixed(2) }

function submitRx() {
  if (!curVisit.value) { proxy.$modal.msgWarning("请先保存病历"); return }
  if (!rxItems.value.length || rxItems.value.some(i => !i.drugId)) { proxy.$modal.msgWarning("请添加并选择药品"); return }
  createPrescription({
    visitId: curVisit.value.visitId, patientId: curReg.value.patientId, doctorId: doctorId.value,
    rxType: '0', itemList: rxItems.value
  }).then(() => {
    proxy.$modal.msgSuccess("处方已开立，待收费")
    rxItems.value = []
    listPrescription({ patientId: curReg.value.patientId, pageSize: 20 }).then(res => hisRxList.value = res.data)
  })
}

/** 检验申请：选择项目 -> 生成检验单 + 待缴检验费 */
function applyLab() {
  if (!curVisit.value) { proxy.$modal.msgWarning("请先保存病历"); return }
  if (!labItem.value) { proxy.$modal.msgWarning("请选择检验项目"); return }
  const fee = labFeeItems.value.find(f => f.itemName === labItem.value)
  addLabTest({
    testNo: "JY" + Date.now(), patientId: curReg.value.patientId, visitId: curVisit.value.visitId,
    doctorId: doctorId.value, testItem: labItem.value, sampleType: labSample.value, status: '0',
    fee: fee ? fee.price : undefined,
    applyTime: new Date().toISOString().slice(0, 19).replace('T', ' ')
  }).then(() => {
    proxy.$modal.msgSuccess("检验申请已提交(已生成待缴检验费)")
    listLabTest({ patientId: curReg.value.patientId, pageSize: 20 }).then(res => labList.value = res.data)
    labItem.value = undefined
  })
}

function applyExam() {
  if (!curVisit.value) { proxy.$modal.msgWarning("请先保存病历"); return }
  if (!examPart.value) { proxy.$modal.msgWarning("请填写检查部位"); return }
  const fee = examFeeItems.value.find(f => f.itemId === examFeeItem.value)
  addExam({
    examNo: "JC" + Date.now(), patientId: curReg.value.patientId, doctorId: doctorId.value,
    examType: examType.value, bodyPart: examPart.value, purpose: examPurpose.value, status: '0',
    fee: fee ? fee.price : undefined,
    applyTime: new Date().toISOString().slice(0, 19).replace('T', ' ')
  }).then(() => {
    proxy.$modal.msgSuccess("检查申请已提交(已生成待缴检查费)")
    listExam({ patientId: curReg.value.patientId, pageSize: 20 }).then(res => examList.value = res.data)
    examPart.value = ''; examPurpose.value = ''
  })
}

listDoctor({ pageNum: 1, pageSize: 200 }).then(res => {
  doctorOptions.value = res.data
  if (res.data.length) { doctorId.value = res.data[0].doctorId; loadQueue() }
})
listDrug({ pageNum: 1, pageSize: 500, status: '0' }).then(res => drugOptions.value = res.data)
listFeeItem({ pageNum: 1, pageSize: 100 }).then(res => {
  labFeeItems.value = res.data.filter(f => f.category === '0')
  examFeeItems.value = res.data.filter(f => f.category === '1')
})
</script>

<style scoped>
.q-title { font-weight: 600; margin: 4px 0 8px; }
.ws-q-item { display: flex; align-items: center; gap: 8px; padding: 8px; border: 1px solid #ebeef5; border-radius: 6px; margin-bottom: 6px; cursor: pointer; }
.ws-q-item:hover { border-color: #409eff; }
.ws-q-item.active { background: #ecf5ff; border-color: #409eff; }
.ws-q-item.done { opacity: .55; }
.ws-q-no { font-size: 18px; font-weight: 700; color: #409eff; width: 30px; text-align: center; }
.ws-sub { font-size: 12px; color: #909399; }
</style>

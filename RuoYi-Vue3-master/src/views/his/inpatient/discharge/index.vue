<template>
   <div class="app-container">
      <el-card shadow="never" header="在院患者(点击办理出院结算)">
         <el-table v-loading="loading" :data="admList" border>
            <el-table-column label="住院号" align="center" prop="admNo" width="130" />
            <el-table-column label="患者" align="center" prop="patientName" width="90" />
            <el-table-column label="科室" align="center" prop="deptName" width="90" />
            <el-table-column label="病区/床号" align="center" width="130">
               <template #default="s">{{ s.row.wardName }}-{{ s.row.bedNo }}</template>
            </el-table-column>
            <el-table-column label="主治医生" align="center" prop="doctorName" width="100" />
            <el-table-column label="入院时间" align="center" prop="inDate" width="150">
               <template #default="s"><span>{{ parseTime(s.row.inDate) }}</span></template>
            </el-table-column>
            <el-table-column label="住院天数" align="center" width="90">
               <template #default="s">{{ daysOf(s.row.inDate) }}天</template>
            </el-table-column>
            <el-table-column label="押金" align="center" prop="deposit" width="100" />
            <el-table-column label="操作" width="140" align="center" fixed="right">
               <template #default="scope">
                  <el-button type="warning" size="small" @click="openSettle(scope.row)" v-hasPermi="['his:discharge:settle']">出院结算</el-button>
               </template>
            </el-table-column>
         </el-table>
      </el-card>

      <!-- 出院结算抽屉 -->
      <el-drawer v-model="open" :title="`出院结算 - ${cur.patientName || ''}`" size="48%">
         <template v-if="cur.admId">
            <el-descriptions :column="2" border style="margin-bottom:14px">
               <el-descriptions-item label="住院号">{{ cur.admNo }}</el-descriptions-item>
               <el-descriptions-item label="床号">{{ cur.wardName }}-{{ cur.bedNo }}</el-descriptions-item>
               <el-descriptions-item label="入院时间">{{ parseTime(cur.inDate) }}</el-descriptions-item>
               <el-descriptions-item label="住院天数">{{ daysOf(cur.inDate) }}天</el-descriptions-item>
               <el-descriptions-item label="预缴押金"><b style="color:#67c23a">¥{{ cur.deposit }}</b></el-descriptions-item>
               <el-descriptions-item label="入院诊断">{{ cur.diagnosisIn }}</el-descriptions-item>
            </el-descriptions>

            <el-divider>待缴费用明细</el-divider>
            <el-table :data="pending" size="small" border>
               <el-table-column label="类型" width="80"><template #default="s"><dict-tag :options="his_charge_type" :value="s.row.sourceType" /></template></el-table-column>
               <el-table-column label="项目" prop="itemName" :show-overflow-tooltip="true" />
               <el-table-column label="金额" prop="amount" width="90" />
            </el-table>

            <div class="sum-box">
               <div>待缴合计：<b class="red">¥{{ pendingTotal }}</b></div>
               <div>押金抵扣后{{ diff >= 0 ? '应退' : '应补' }}：<b :class="diff >= 0 ? 'green' : 'red'">¥{{ Math.abs(diff).toFixed(2) }}</b></div>
               <el-form inline style="margin-top:10px">
                  <el-form-item label="支付方式">
                     <el-radio-group v-model="payType">
                        <el-radio v-for="d in his_pay_type" :key="d.value" :value="d.value">{{ d.label }}</el-radio>
                     </el-radio-group>
                  </el-form-item>
               </el-form>
               <el-button type="primary" size="large" style="width:100%" @click="doDischarge" v-hasPermi="['his:discharge:settle']">确认出院结算</el-button>
            </div>
         </template>
      </el-drawer>
   </div>
</template>

<script setup name="HisDischarge">
import { listAdmission, discharge } from "@/api/his/admission"
import { listCharge, settleCharge } from "@/api/his/charge"

const { proxy } = getCurrentInstance()
const { his_charge_type, his_pay_type } = useDict("his_charge_type", "his_pay_type")

const admList = ref([])
const loading = ref(false)
const open = ref(false)
const cur = ref({})
const pending = ref([])
const payType = ref('1')

const pendingTotal = computed(() => pending.value.reduce((s, c) => s + Number(c.amount), 0).toFixed(2))
const diff = computed(() => Number(cur.value.deposit || 0) - Number(pendingTotal.value))

function daysOf(inDate) {
  if (!inDate) return 0
  return Math.max(1, Math.ceil((Date.now() - new Date(inDate).getTime()) / 86400000))
}

function getList() {
  loading.value = true
  listAdmission({ pageNum: 1, pageSize: 100, admStatus: '0' }).then(res => { admList.value = res.data; loading.value = false })
}

/** 打开结算：先出院(生成床位费)再拉取全部未缴费用 */
function openSettle(row) {
  cur.value = row
  listCharge({ pageNum: 1, pageSize: 200, patientId: row.patientId, chargeStatus: '0' }).then(res => {
    pending.value = res.data
    open.value = true
  })
}

function doDischarge() {
  proxy.$modal.confirm(`确认出院结算？床位 ${cur.value.bedNo} 将释放并生成床位费。`).then(() => {
    return discharge(cur.value.admId)
  }).then(() => {
    // 出院后重新拉取未缴(含新床位费)并结算
    return listCharge({ pageNum: 1, pageSize: 200, patientId: cur.value.patientId, chargeStatus: '0' })
  }).then(res => {
    const ids = res.data.map(r => r.chargeId)
    if (!ids.length) return Promise.resolve()
    return settleCharge(ids, payType.value)
  }).then(() => {
    proxy.$modal.msgSuccess("出院结算完成")
    open.value = false
    getList()
  }).catch(() => {})
}

getList()
</script>

<style scoped>
.sum-box { margin-top: 16px; padding: 16px; background: #f5f7fa; border-radius: 8px; }
.sum-box > div { font-size: 15px; margin-bottom: 8px; }
.red { color: #f56c6c; font-size: 20px; }
.green { color: #67c23a; font-size: 20px; }
</style>

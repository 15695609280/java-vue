<template>
   <div class="app-container">
      <el-row :gutter="16">
         <!-- 左: 待缴费结算台 -->
         <el-col :span="14">
            <el-card shadow="never">
               <template #header>
                  <div style="display:flex;justify-content:space-between;align-items:center">
                     <span>门诊收费结算台</span>
                     <el-select v-model="patientId" filterable remote :remote-method="searchPatient" placeholder="搜索患者(姓名)" style="width: 260px" @change="loadPending">
                        <el-option v-for="p in patientOptions" :key="p.patientId" :label="p.patientName + ' (' + p.patientNo + ')'" :value="p.patientId" />
                     </el-select>
                  </div>
               </template>

               <el-table ref="pendTable" :data="pendingList" border @selection-change="onSelChange" size="small">
                  <el-table-column type="selection" width="45" />
                  <el-table-column label="单据号" prop="chargeNo" width="120" />
                  <el-table-column label="类型" width="85">
                     <template #default="s"><dict-tag :options="his_charge_type" :value="s.row.sourceType" /></template>
                  </el-table-column>
                  <el-table-column label="项目" prop="itemName" :show-overflow-tooltip="true" />
                  <el-table-column label="单价" prop="price" width="80" />
                  <el-table-column label="数量" prop="quantity" width="60" />
                  <el-table-column label="金额" prop="amount" width="90" />
               </el-table>

               <div class="settle-bar">
                  <div>
                     <span>合计：</span><b class="amount">¥{{ selTotal }}</b>
                     <span style="margin-left:24px">支付方式：</span>
                     <el-radio-group v-model="payType">
                        <el-radio v-for="d in his_pay_type" :key="d.value" :value="d.value">{{ d.label }}</el-radio>
                     </el-radio-group>
                  </div>
                  <el-button type="primary" size="large" :disabled="!selIds.length" @click="doSettle" v-hasPermi="['his:charge:settle']">收费结算</el-button>
               </div>
            </el-card>
         </el-col>

         <!-- 右: 收费流水 -->
         <el-col :span="10">
            <el-card shadow="never">
               <template #header>
                  <div style="display:flex;justify-content:space-between;align-items:center">
                     <span>收费流水</span>
                     <el-radio-group v-model="statusFilter" size="small" @change="loadHistory">
                        <el-radio-button value="">全部</el-radio-button>
                        <el-radio-button value="0">未收</el-radio-button>
                        <el-radio-button value="1">已收</el-radio-button>
                        <el-radio-button value="2">已退</el-radio-button>
                     </el-radio-group>
                  </div>
               </template>
               <el-table :data="historyList" size="small" border height="520">
                  <el-table-column label="项目" prop="itemName" :show-overflow-tooltip="true" />
                  <el-table-column label="患者" prop="patientName" width="80" />
                  <el-table-column label="金额" prop="amount" width="80" />
                  <el-table-column label="状态" width="80">
                     <template #default="s"><dict-tag :options="his_charge_status" :value="s.row.chargeStatus" /></template>
                  </el-table-column>
                  <el-table-column label="操作" width="70">
                     <template #default="s">
                        <el-button v-if="s.row.chargeStatus === '1'" link type="danger" @click="doRefund(s.row)" v-hasPermi="['his:charge:refund']">退费</el-button>
                     </template>
                  </el-table-column>
               </el-table>
            </el-card>
         </el-col>
      </el-row>
   </div>
</template>

<script setup name="HisCharge">
import { listCharge, settleCharge, refundCharge } from "@/api/his/charge"
import { listPatient } from "@/api/his/patient"

const { proxy } = getCurrentInstance()
const { his_charge_type, his_charge_status, his_pay_type } = useDict("his_charge_type", "his_charge_status", "his_pay_type")

const patientId = ref()
const patientOptions = ref([])
const pendingList = ref([])
const historyList = ref([])
const selIds = ref([])
const payType = ref('1')
const statusFilter = ref('')

const selTotal = computed(() => pendingList.value.filter(c => selIds.value.includes(c.chargeId)).reduce((s, c) => s + Number(c.amount), 0).toFixed(2))

function searchPatient(kw) {
  listPatient({ pageNum: 1, pageSize: 50, patientName: kw || undefined }).then(res => patientOptions.value = res.data)
}

/** 加载该患者所有未缴费单 */
function loadPending() {
  if (!patientId.value) return
  listCharge({ pageNum: 1, pageSize: 200, patientId: patientId.value, chargeStatus: '0' }).then(res => {
    pendingList.value = res.data
    nextTick(() => res.data.forEach(r => proxy.$refs.pendTable && proxy.$refs.pendTable.toggleRowSelection(r, true)))
  })
}

function onSelChange(sel) { selIds.value = sel.map(i => i.chargeId) }

function doSettle() {
  proxy.$modal.confirm(`确认收取 ¥${selTotal.value}？`).then(() => {
    return settleCharge(selIds.value, payType.value)
  }).then(() => {
    proxy.$modal.msgSuccess("结算成功")
    loadPending()
    loadHistory()
  }).catch(() => {})
}

function doRefund(row) {
  proxy.$modal.confirm(`确认退费 ¥${row.amount}？来源单据状态将回滚。`).then(() => refundCharge(row.chargeId)).then(() => { proxy.$modal.msgSuccess("已退费"); loadPending(); loadHistory() }).catch(() => {})
}

function loadHistory() {
  listCharge({ pageNum: 1, pageSize: 100, chargeStatus: statusFilter.value || undefined, patientId: patientId.value }).then(res => historyList.value = res.data)
}

searchPatient("")
loadHistory()
</script>

<style scoped>
.settle-bar { display: flex; justify-content: space-between; align-items: center; margin-top: 14px; padding: 12px; background: #f5f7fa; border-radius: 6px; }
.amount { font-size: 22px; color: #f56c6c; }
</style>

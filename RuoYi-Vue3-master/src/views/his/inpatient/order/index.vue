<template>
   <div class="app-container order-page">
      <!-- 左栏: 在院患者 -->
      <div class="pat-panel">
         <div class="pat-head">
            <b>在院患者</b>
            <el-tag size="small" type="success">{{ admOptions.length }}</el-tag>
         </div>
         <el-input v-model="patKw" placeholder="搜索患者" prefix-icon="Search" clearable size="small" style="margin-bottom:8px" />
         <div class="pat-list" v-loading="admLoading">
            <div v-for="a in filteredAdms" :key="a.admId" class="pat-item" :class="{ active: a.admId === admId }" @click="selectAdm(a)">
               <div class="p-name">{{ a.patientName }}</div>
               <div class="p-sub">{{ a.wardName }}-{{ a.bedNo }}床</div>
               <div class="p-sub">{{ a.doctorName }}</div>
            </div>
            <el-empty v-if="!admLoading && !filteredAdms.length" description="无在院患者" :image-size="60" />
         </div>
      </div>

      <!-- 右栏: 医嘱时间线 -->
      <div class="order-panel">
         <template v-if="curAdm">
            <el-alert :closable="false" type="info" style="margin-bottom:12px">
               <template #title>
                  <b>{{ curAdm.patientName }}</b> · 住院号 {{ curAdm.admNo }} · {{ curAdm.wardName }}-{{ curAdm.bedNo }}床 · 主治 {{ curAdm.doctorName }}
               </template>
            </el-alert>
            <div class="o-bar">
               <el-radio-group v-model="queryStatus" size="small" @change="getList">
                  <el-radio-button value="">全部</el-radio-button>
                  <el-radio-button v-for="d in his_order_status" :key="d.value" :value="d.value">{{ d.label }}</el-radio-button>
               </el-radio-group>
               <div>
                  <el-button icon="Refresh" circle size="small" @click="getList" />
                  <el-button type="primary" size="small" icon="Plus" @click="handleAdd" v-hasPermi="['his:order:add']">开立医嘱</el-button>
               </div>
            </div>

            <el-timeline v-loading="loading" style="margin-top:14px;padding-left:6px">
               <el-timeline-item v-for="o in orderList" :key="o.orderId"
                  :type="dotType(o)" :timestamp="parseTime(o.createTime)" placement="top">
                  <div class="o-card" :class="{ stopped: o.orderStatus === '2' }">
                     <div class="o-head">
                        <el-tag size="small" :type="o.orderType === '0' ? 'warning' : 'primary'" effect="dark">
                           {{ orderTypeLabel(o.orderType) }}
                        </el-tag>
                        <dict-tag :options="his_order_status" :value="o.orderStatus" />
                        <span class="o-time" v-if="o.execTime">执行于 {{ parseTime(o.execTime, '{m}-{d} {h}:{i}') }} {{ o.execBy }}</span>
                     </div>
                     <div class="o-content">{{ o.content }}</div>
                     <div class="o-drug" v-if="o.drugName">
                        <el-icon><FirstAidKit /></el-icon>{{ o.drugName }} · {{ o.dose }} · {{ o.frequency }}
                     </div>
                     <div class="o-ops">
                        <el-button v-if="o.orderStatus === '0'" size="small" type="success" plain @click="doExec(o)" v-hasPermi="['his:order:exec']">执行(自动计费)</el-button>
                        <el-button v-if="o.orderStatus !== '2'" size="small" type="danger" link @click="doStop(o)" v-hasPermi="['his:order:stop']">停止</el-button>
                     </div>
                  </div>
               </el-timeline-item>
            </el-timeline>
            <el-empty v-if="!loading && !orderList.length" description="暂无医嘱" :image-size="80" />
            <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
         </template>
         <el-empty v-else description="请在左侧选择在院患者" :image-size="120" style="margin-top:80px" />
      </div>

      <!-- 开立医嘱 -->
      <el-dialog title="开立医嘱" v-model="open" width="640px" append-to-body>
         <el-alert v-if="curAdm" :title="`患者: ${curAdm.patientName} | 住院号: ${curAdm.admNo} | 床位: ${curAdm.wardName}-${curAdm.bedNo}`" type="info" :closable="false" style="margin-bottom:12px" />
         <el-form ref="orderRef" :model="form" :rules="rules" label-width="100px">
            <el-form-item label="医嘱类型" prop="orderType">
               <el-radio-group v-model="form.orderType">
                  <el-radio v-for="d in his_order_type" :key="d.value" :value="d.value">{{ d.label }}</el-radio>
               </el-radio-group>
            </el-form-item>
            <el-form-item label="医嘱内容" prop="content">
               <el-input v-model="form.content" type="textarea" :rows="3" placeholder="如: 头孢曲松钠2g+0.9%氯化钠250ml 静滴" />
            </el-form-item>
            <el-form-item label="关联药品" prop="drugId">
               <el-select v-model="form.drugId" filterable clearable placeholder="仅药品医嘱选择(执行时自动计费)" style="width:100%">
                  <el-option v-for="d in drugOptions" :key="d.drugId" :label="`${d.drugName} ${d.specification} ¥${d.price}`" :value="d.drugId" />
               </el-select>
            </el-form-item>
            <el-row>
               <el-col :span="12"><el-form-item label="单次剂量" prop="dose"><el-input v-model="form.dose" placeholder="如 2g" /></el-form-item></el-col>
               <el-col :span="12"><el-form-item label="频次" prop="frequency">
                  <el-select v-model="form.frequency" style="width:100%">
                     <el-option label="每日一次" value="每日一次" /><el-option label="每日两次" value="每日两次" /><el-option label="每日三次" value="每日三次" /><el-option label="每8小时" value="每8小时" /><el-option label="必要时" value="必要时" /><el-option label="立即" value="立即" />
                  </el-select>
               </el-form-item></el-col>
            </el-row>
         </el-form>
         <template #footer>
            <el-button type="primary" @click="submit">开立</el-button>
            <el-button @click="open = false">取 消</el-button>
         </template>
      </el-dialog>
   </div>
</template>

<script setup name="HisOrder">
import { listMedicalOrder, addMedicalOrder, executeOrder, stopOrder } from "@/api/his/order"
import { listAdmission } from "@/api/his/admission"
import { listDrug } from "@/api/his/drug"
import { FirstAidKit } from "@element-plus/icons-vue"

const { proxy } = getCurrentInstance()
const { his_order_type, his_order_status } = useDict("his_order_type", "his_order_status")

const admId = ref()
const queryStatus = ref("")
const patKw = ref("")
const admOptions = ref([])
const admLoading = ref(true)
const orderList = ref([])
const loading = ref(false)
const total = ref(0)
const open = ref(false)
const form = ref({})
const drugOptions = ref([])

const queryParams = reactive({ pageNum: 1, pageSize: 20 })
const curAdm = computed(() => admOptions.value.find(a => a.admId === admId.value))
const filteredAdms = computed(() => admOptions.value.filter(a => !patKw.value || a.patientName?.includes(patKw.value)))
const rules = { content: [{ required: true, message: "医嘱内容不能为空", trigger: "blur" }], orderType: [{ required: true, message: "请选择类型", trigger: "change" }] }

function orderTypeLabel(v) { return his_order_type.value.find(x => x.value === v)?.label || v }
function dotType(o) {
   if (o.orderStatus === '2') return 'info'
   if (o.orderStatus === '1') return 'success'
   return o.orderType === '1' ? 'danger' : 'warning'
}

function selectAdm(a) {
   admId.value = a.admId
   queryParams.pageNum = 1
   getList()
}

function getList() {
   if (!admId.value) return
   loading.value = true
   listMedicalOrder({ pageNum: queryParams.pageNum, pageSize: queryParams.pageSize, admId: admId.value, orderStatus: queryStatus.value || undefined }).then(res => {
      orderList.value = res.data; total.value = res.total; loading.value = false
   })
}

function handleAdd() {
   form.value = { orderType: '1', content: undefined, drugId: undefined, dose: undefined, frequency: '每日一次' }
   open.value = true
}

function submit() {
   proxy.$refs["orderRef"].validate(valid => {
      if (!valid) return
      addMedicalOrder({
         ...form.value,
         admId: admId.value,
         patientId: curAdm.value.patientId,
         doctorId: curAdm.value.doctorId,
         startTime: new Date().toISOString().slice(0, 19).replace('T', ' '),
         orderStatus: '0'
      }).then(() => { proxy.$modal.msgSuccess("医嘱已开立"); open.value = false; getList() })
   })
}

function doExec(row) {
   proxy.$modal.confirm(`确认执行医嘱"${row.content}"？关联药品将自动计费并扣减库存。`).then(() => executeOrder(row.orderId)).then(() => { proxy.$modal.msgSuccess("已执行"); getList() }).catch(() => {})
}
function doStop(row) {
   proxy.$modal.confirm('确认停止该医嘱？').then(() => stopOrder(row.orderId)).then(() => { proxy.$modal.msgSuccess("已停止"); getList() }).catch(() => {})
}

listAdmission({ pageNum: 1, pageSize: 200, admStatus: '0' }).then(res => {
   admOptions.value = res.data
   admLoading.value = false
   if (res.data.length) selectAdm(res.data[0])
})
listDrug({ pageNum: 1, pageSize: 500, status: '0' }).then(res => drugOptions.value = res.data)
</script>

<style scoped>
.order-page { display: flex; gap: 14px; align-items: flex-start; }
.pat-panel {
   width: 240px; flex-shrink: 0; background: #fff; border: 1px solid #e4e7ed; border-radius: 8px;
   padding: 12px; position: sticky; top: 10px; max-height: calc(100vh - 130px); overflow: auto;
}
.pat-head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
.pat-list { display: flex; flex-direction: column; gap: 6px; }
.pat-item { padding: 10px; border-radius: 6px; border: 1px solid #ebeef5; cursor: pointer; transition: all .12s; }
.pat-item:hover { border-color: #409eff; }
.pat-item.active { border-color: #409eff; background: #ecf5ff; }
.p-name { font-weight: 600; font-size: 14px; }
.p-sub { font-size: 12px; color: #909399; margin-top: 2px; }
.order-panel { flex: 1; min-width: 0; }
.o-bar { display: flex; justify-content: space-between; align-items: center; }
.o-card { background: #fff; border: 1px solid #e4e7ed; border-radius: 8px; padding: 12px 14px; }
.o-card.stopped { opacity: .55; }
.o-head { display: flex; align-items: center; gap: 8px; margin-bottom: 6px; }
.o-time { font-size: 12px; color: #909399; margin-left: auto; }
.o-content { font-size: 14px; font-weight: 600; }
.o-drug { font-size: 12px; color: #e6a23c; margin-top: 6px; display: flex; align-items: center; gap: 4px; }
.o-ops { margin-top: 8px; }
</style>

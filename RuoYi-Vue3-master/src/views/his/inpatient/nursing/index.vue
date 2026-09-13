<template>
   <div class="app-container">
      <el-row :gutter="16">
         <el-col :span="7">
            <el-card shadow="never" header="在院患者">
               <div v-for="a in admList" :key="a.admId" class="pat-item" :class="{active: admId === a.admId}" @click="pick(a)">
                  <div>
                     <b>{{ a.patientName }}</b>
                     <div class="sub">{{ a.wardName }} · {{ a.bedNo }}床 · {{ a.doctorName }}</div>
                  </div>
                  <el-tag size="small" type="primary">在院</el-tag>
               </div>
               <el-empty v-if="!admList.length" description="无在院患者" :image-size="60" />
            </el-card>
         </el-col>

         <el-col :span="17">
            <el-card shadow="never">
               <template #header>
                  <div style="display:flex;justify-content:space-between;align-items:center">
                     <span>体温单 / 护理记录</span>
                     <el-button v-if="admId" type="primary" plain icon="Plus" @click="open = true" v-hasPermi="['his:nursing:add']">录入体征</el-button>
                  </div>
               </template>
               <el-empty v-if="!admId" description="请选择左侧在院患者" />
               <template v-else>
                  <div ref="chartEl" style="height:280px" />
                  <el-table :data="records" size="small" border style="margin-top:12px">
                     <el-table-column label="记录时间" prop="recordTime" width="150">
                        <template #default="s"><span>{{ parseTime(s.row.recordTime) }}</span></template>
                     </el-table-column>
                     <el-table-column label="体温℃" prop="temperature" width="80" />
                     <el-table-column label="脉搏" prop="pulse" width="70" />
                     <el-table-column label="呼吸" prop="breath" width="70" />
                     <el-table-column label="血压" width="100"><template #default="s">{{ s.row.bpHigh }}/{{ s.row.bpLow }}</template></el-table-column>
                     <el-table-column label="血氧" prop="spo2" width="70" />
                     <el-table-column label="护士" prop="nurseName" width="80" />
                     <el-table-column label="护理内容" prop="content" :show-overflow-tooltip="true" />
                  </el-table>
               </template>
            </el-card>
         </el-col>
      </el-row>

      <el-dialog title="录入生命体征" v-model="open" width="560px" append-to-body>
         <el-form ref="ref" :model="form" label-width="100px">
            <el-row :gutter="8">
               <el-col :span="12"><el-form-item label="体温(℃)"><el-input-number v-model="form.temperature" :precision="1" :min="34" :max="43" :step="0.1" controls-position="right" style="width:100%" /></el-form-item></el-col>
               <el-col :span="12"><el-form-item label="脉搏"><el-input-number v-model="form.pulse" :min="30" :max="200" controls-position="right" style="width:100%" /></el-form-item></el-col>
               <el-col :span="12"><el-form-item label="呼吸"><el-input-number v-model="form.breath" :min="5" :max="60" controls-position="right" style="width:100%" /></el-form-item></el-col>
               <el-col :span="12"><el-form-item label="血氧(%)"><el-input-number v-model="form.spo2" :min="50" :max="100" controls-position="right" style="width:100%" /></el-form-item></el-col>
               <el-col :span="12"><el-form-item label="收缩压"><el-input-number v-model="form.bpHigh" :min="50" :max="250" controls-position="right" style="width:100%" /></el-form-item></el-col>
               <el-col :span="12"><el-form-item label="舒张压"><el-input-number v-model="form.bpLow" :min="20" :max="150" controls-position="right" style="width:100%" /></el-form-item></el-col>
               <el-col :span="24"><el-form-item label="护士"><el-input v-model="form.nurseName" placeholder="护士姓名" /></el-form-item></el-col>
               <el-col :span="24"><el-form-item label="护理内容"><el-input v-model="form.content" type="textarea" :rows="2" placeholder="如: 晨间护理，协助翻身" /></el-form-item></el-col>
            </el-row>
         </el-form>
         <template #footer>
            <el-button type="primary" @click="submit">保存</el-button>
            <el-button @click="open = false">取消</el-button>
         </template>
      </el-dialog>
   </div>
</template>

<script setup name="HisNursing">
import * as echarts from 'echarts'
import { listAdmission } from "@/api/his/admission"
import { listNursingByAdm, addNursing } from "@/api/his/nursing"

const { proxy } = getCurrentInstance()
const admList = ref([])
const admId = ref()
const curAdm = ref()
const records = ref([])
const open = ref(false)
const form = ref({})
const chartEl = ref()
let chart = null

function getAdms() {
  listAdmission({ pageNum: 1, pageSize: 100, admStatus: '0' }).then(res => admList.value = res.data)
}

function pick(a) {
  admId.value = a.admId
  curAdm.value = a
  loadRecords()
}

function loadRecords() {
  listNursingByAdm(admId.value).then(res => {
    records.value = res.data || []
    renderChart()
  })
}

function renderChart() {
  nextTick(() => {
    if (!chartEl.value) return
    if (!chart) chart = echarts.init(chartEl.value)
    chart.setOption({
      tooltip: { trigger: 'axis' },
      legend: { data: ['体温℃', '脉搏', '收缩压', '舒张压'], top: 0 },
      grid: { left: 40, right: 20, top: 34, bottom: 30 },
      xAxis: { type: 'category', data: records.value.map(r => parseTime(r.recordTime, '{m}-{d} {h}:{i}')) },
      yAxis: [{ type: 'value', name: '体温/脉搏' }, { type: 'value', name: '血压' }],
      series: [
        { name: '体温℃', type: 'line', smooth: true, data: records.value.map(r => r.temperature), itemStyle: { color: '#f56c6c' } },
        { name: '脉搏', type: 'line', smooth: true, data: records.value.map(r => r.pulse), itemStyle: { color: '#409eff' } },
        { name: '收缩压', type: 'line', yAxisIndex: 1, data: records.value.map(r => r.bpHigh), itemStyle: { color: '#e6a23c' } },
        { name: '舒张压', type: 'line', yAxisIndex: 1, data: records.value.map(r => r.bpLow), itemStyle: { color: '#67c23a' } }
      ]
    })
  })
}

function submit() {
  addNursing({
    ...form.value,
    admId: admId.value,
    patientId: curAdm.value.patientId,
    recordTime: new Date().toISOString().slice(0, 19).replace('T', ' ')
  }).then(() => {
    proxy.$modal.msgSuccess("已记录")
    open.value = false
    form.value = {}
    loadRecords()
  })
}

onBeforeUnmount(() => { if (chart) chart.dispose() })
getAdms()
</script>

<style scoped>
.pat-item { display: flex; justify-content: space-between; align-items: center; padding: 10px; border: 1px solid #ebeef5; border-radius: 6px; margin-bottom: 8px; cursor: pointer; }
.pat-item.active { background: #ecf5ff; border-color: #409eff; }
.sub { font-size: 12px; color: #909399; margin-top: 4px; }
</style>

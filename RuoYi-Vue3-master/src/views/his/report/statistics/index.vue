<template>
   <div class="app-container">
      <!-- 本月经营 KPI -->
      <el-row :gutter="14" class="kpi-row">
         <el-col :span="4" v-for="k in kpis" :key="k.label">
            <el-card shadow="hover" class="kpi-card">
               <div class="kpi-label">{{ k.label }}</div>
               <div class="kpi-value" :style="{ color: k.color }">{{ k.value }}</div>
               <div class="kpi-sub">{{ k.sub }}</div>
            </el-card>
         </el-col>
      </el-row>

      <el-row :gutter="14">
         <el-col :span="16">
            <el-card shadow="never" header="近30天收入趋势(元)">
               <div ref="revenueEl" style="height:280px" />
            </el-card>
         </el-col>
         <el-col :span="8">
            <el-card shadow="never" header="费用类型占比(已收)">
               <div ref="chargeEl" style="height:280px" />
            </el-card>
         </el-col>
      </el-row>

      <el-row :gutter="14" style="margin-top:14px">
         <el-col :span="12">
            <el-card shadow="never" header="近14天出入院趋势">
               <div ref="admEl" style="height:280px" />
            </el-card>
         </el-col>
         <el-col :span="12">
            <el-card shadow="never" header="近14天门诊挂号趋势">
               <div ref="regEl" style="height:280px" />
            </el-card>
         </el-col>
      </el-row>

      <el-row :gutter="14" style="margin-top:14px">
         <el-col :span="8">
            <el-card shadow="never" header="科室挂号分布">
               <div ref="deptEl" style="height:300px" />
            </el-card>
         </el-col>
         <el-col :span="8">
            <el-card shadow="never" header="科室收入分布(元)">
               <div ref="deptRevEl" style="height:300px" />
            </el-card>
         </el-col>
         <el-col :span="8">
            <el-card shadow="never" header="挂号时段分布">
               <div ref="slotEl" style="height:300px" />
            </el-card>
         </el-col>
      </el-row>

      <el-row :gutter="14" style="margin-top:14px">
         <el-col :span="8">
            <el-card shadow="never" header="医生工作量TOP10(接诊数)">
               <div ref="docEl" style="height:320px" />
            </el-card>
         </el-col>
         <el-col :span="8">
            <el-card shadow="never" header="药品消耗TOP10">
               <div ref="drugEl" style="height:320px" />
            </el-card>
         </el-col>
         <el-col :span="8">
            <el-card shadow="never" header="病区床位使用率">
               <el-table :data="bedData" size="small" border max-height="320">
                  <el-table-column label="病区" prop="name" />
                  <el-table-column label="总床位" align="center" prop="total" width="80" />
                  <el-table-column label="占用" align="center" prop="used" width="80" />
                  <el-table-column label="使用率" min-width="140">
                     <template #default="s">
                        <el-progress :percentage="s.row.total ? Math.round(s.row.used / s.row.total * 100) : 0" :status="s.row.used / s.row.total > 0.9 ? 'exception' : 'success'" :stroke-width="14" />
                     </template>
                  </el-table-column>
               </el-table>
            </el-card>
         </el-col>
      </el-row>
   </div>
</template>

<script setup name="HisStatistics">
import * as echarts from 'echarts'
import { deptReg, chargeType, revenueTrend, topDrugs, bedUsage, regTrend, doctorWorkload, admTrend, slotDist, deptRevenue, monthSummary } from "@/api/his/statistics"

const revenueEl = ref(), chargeEl = ref(), admEl = ref(), regEl = ref()
const deptEl = ref(), deptRevEl = ref(), slotEl = ref(), docEl = ref(), drugEl = ref()
const bedData = ref([])
const kpis = ref([])
const charts = []

const PALETTE = ['#409eff', '#67c23a', '#e6a23c', '#f56c6c', '#909399', '#a0cfff', '#b3e19d']

function mount(el, option) {
   const c = echarts.init(el)
   c.setOption(option)
   charts.push(c)
}

onMounted(() => {
   monthSummary().then(res => {
      const d = res.data || {}
      kpis.value = [
         { label: '本月挂号量', value: d.monthReg ?? 0, sub: '人次', color: '#409eff' },
         { label: '本月收入', value: '¥' + Number(d.monthRevenue ?? 0).toLocaleString(), sub: '已结算', color: '#67c23a' },
         { label: '本月入院', value: d.monthAdmit ?? 0, sub: '人次', color: '#e6a23c' },
         { label: '本月出院', value: d.monthDischarge ?? 0, sub: '人次', color: '#13c2c2' },
         { label: '本月处方', value: d.monthRx ?? 0, sub: '张', color: '#722ed1' },
         { label: '本月手术', value: d.monthSurgery ?? 0, sub: '台', color: '#f56c6c' },
      ]
   })

   revenueTrend().then(res => {
      const d = res.data || []
      mount(revenueEl.value, {
         tooltip: { trigger: 'axis' },
         grid: { left: 70, right: 20, top: 30, bottom: 40 },
         xAxis: { type: 'category', data: d.map(x => x.day), boundaryGap: false },
         yAxis: { type: 'value' },
         series: [{ type: 'line', smooth: true, areaStyle: { opacity: 0.15 }, data: d.map(x => x.amount), itemStyle: { color: '#67c23a' } }]
      })
   })

   chargeType().then(res => {
      const d = res.data || []
      mount(chargeEl.value, {
         tooltip: { trigger: 'item', formatter: '{b}: ¥{c} ({d}%)' },
         legend: { bottom: 0 },
         color: PALETTE,
         series: [{ type: 'pie', radius: ['40%', '65%'], center: ['50%', '45%'], data: d.map(x => ({ name: x.name, value: x.value })), label: { formatter: '{b}' } }]
      })
   })

   admTrend().then(res => {
      const d = res.data || []
      mount(admEl.value, {
         tooltip: { trigger: 'axis' },
         legend: { top: 0 },
         grid: { left: 40, right: 20, top: 40, bottom: 30 },
         xAxis: { type: 'category', data: d.map(x => (x.day || '').slice(5)), boundaryGap: false },
         yAxis: { type: 'value' },
         series: [
            { name: '入院', type: 'line', smooth: true, data: d.map(x => Number(x.admitCnt)), itemStyle: { color: '#e6a23c' } },
            { name: '出院', type: 'line', smooth: true, data: d.map(x => Number(x.dischargeCnt)), itemStyle: { color: '#13c2c2' } }
         ]
      })
   })

   regTrend().then(res => {
      const d = res.data || []
      mount(regEl.value, {
         tooltip: { trigger: 'axis' },
         grid: { left: 40, right: 20, top: 30, bottom: 30 },
         xAxis: { type: 'category', data: d.map(x => x.day), boundaryGap: false },
         yAxis: { type: 'value' },
         series: [{ type: 'line', smooth: true, areaStyle: { opacity: 0.2 }, data: d.map(x => x.cnt), itemStyle: { color: '#409eff' } }]
      })
   })

   deptReg().then(res => {
      const d = res.data || []
      mount(deptEl.value, {
         tooltip: { trigger: 'axis' },
         grid: { left: 60, right: 20, top: 30, bottom: 60 },
         xAxis: { type: 'category', data: d.map(x => x.name), axisLabel: { rotate: 30 } },
         yAxis: { type: 'value' },
         series: [{ type: 'bar', data: d.map(x => x.value), itemStyle: { color: '#409eff', borderRadius: [4, 4, 0, 0] }, barWidth: 22 }]
      })
   })

   deptRevenue().then(res => {
      const d = res.data || []
      mount(deptRevEl.value, {
         tooltip: { trigger: 'axis', formatter: '{b}: ¥{c}' },
         grid: { left: 70, right: 20, top: 30, bottom: 60 },
         xAxis: { type: 'category', data: d.map(x => x.name), axisLabel: { rotate: 30 } },
         yAxis: { type: 'value' },
         series: [{ type: 'bar', data: d.map(x => x.value), itemStyle: { color: '#67c23a', borderRadius: [4, 4, 0, 0] }, barWidth: 22 }]
      })
   })

   slotDist().then(res => {
      const d = res.data || []
      mount(slotEl.value, {
         tooltip: { trigger: 'item', formatter: '{b}: {c}人次 ({d}%)' },
         legend: { bottom: 0 },
         series: [{
            type: 'pie', radius: ['35%', '60%'], center: ['50%', '45%'],
            roseType: 'radius',
            data: d.map(x => ({ name: x.name, value: x.value })),
            label: { formatter: '{b}\n{c}人次' }
         }]
      })
   })

   doctorWorkload().then(res => {
      const d = (res.data || []).reverse()
      mount(docEl.value, {
         tooltip: { trigger: 'axis' },
         grid: { left: 90, right: 30, top: 20, bottom: 30 },
         xAxis: { type: 'value' },
         yAxis: { type: 'category', data: d.map(x => x.name) },
         series: [{ type: 'bar', data: d.map(x => x.value), itemStyle: { color: '#409eff', borderRadius: [0, 4, 4, 0] }, barWidth: 14 }]
      })
   })

   topDrugs().then(res => {
      const d = (res.data || []).reverse()
      mount(drugEl.value, {
         tooltip: { trigger: 'axis' },
         grid: { left: 130, right: 30, top: 20, bottom: 30 },
         xAxis: { type: 'value' },
         yAxis: { type: 'category', data: d.map(x => x.name) },
         series: [{ type: 'bar', data: d.map(x => x.value), itemStyle: { color: '#e6a23c', borderRadius: [0, 4, 4, 0] }, barWidth: 14 }]
      })
   })

   bedUsage().then(res => bedData.value = res.data || [])
})

onBeforeUnmount(() => charts.forEach(c => c.dispose()))
</script>

<style scoped>
.kpi-row { margin-bottom: 0; }
.kpi-card { margin-bottom: 14px; }
.kpi-label { font-size: 13px; color: #909399; }
.kpi-value { font-size: 26px; font-weight: 700; margin: 6px 0 2px; }
.kpi-sub { font-size: 12px; color: #c0c4cc; }
</style>

<template>
   <div class="app-container his-dashboard">
      <!-- 顶部统计卡片 -->
      <el-row :gutter="16">
         <el-col :span="6" v-for="card in cards" :key="card.label">
            <el-card shadow="hover" class="stat-card">
               <div class="stat-card-inner">
                  <div class="stat-icon" :style="{ background: card.color }">
                     <el-icon :size="26"><component :is="card.icon" /></el-icon>
                  </div>
                  <div>
                     <div class="stat-num">{{ card.value }}</div>
                     <div class="stat-label">{{ card.label }}</div>
                  </div>
               </div>
            </el-card>
         </el-col>
      </el-row>

      <el-row :gutter="16" style="margin-top: 16px">
         <el-col :span="12">
            <el-card shadow="never" header="近14天门诊挂号趋势">
               <div ref="regChart" style="height: 300px"></div>
            </el-card>
         </el-col>
         <el-col :span="12">
            <el-card shadow="never" header="近30天收费收入趋势(元)">
               <div ref="revChart" style="height: 300px"></div>
            </el-card>
         </el-col>
      </el-row>

      <el-row :gutter="16" style="margin-top: 16px">
         <el-col :span="8">
            <el-card shadow="never" header="科室挂号量分布">
               <div ref="deptChart" style="height: 300px"></div>
            </el-card>
         </el-col>
         <el-col :span="8">
            <el-card shadow="never" header="病区床位使用率">
               <div ref="bedChart" style="height: 300px"></div>
            </el-card>
         </el-col>
         <el-col :span="8">
            <el-card shadow="never" header="药品消耗TOP10">
               <div ref="drugChart" style="height: 300px"></div>
            </el-card>
         </el-col>
      </el-row>
   </div>
</template>

<script setup name="HisDashboard">
import * as echarts from 'echarts'
import { getOverview, regTrend, revenueTrend, deptReg, topDrugs, bedUsage } from "@/api/his/statistics"

const cards = ref([
  { label: '今日挂号', value: 0, icon: 'Tickets', color: '#409eff' },
  { label: '今日收入(元)', value: 0, icon: 'Money', color: '#67c23a' },
  { label: '在院患者', value: 0, icon: 'OfficeBuilding', color: '#e6a23c' },
  { label: '空闲床位', value: 0, icon: 'Grid', color: '#909399' },
  { label: '建档患者总数', value: 0, icon: 'User', color: '#5c6bc0' },
  { label: '库存预警药品', value: 0, icon: 'Warning', color: '#f56c6c' },
  { label: '待手术', value: 0, icon: 'AlarmClock', color: '#ab47bc' },
  { label: '检验进行中', value: 0, icon: 'Odometer', color: '#26a69a' },
])

const regChart = ref(), revChart = ref(), deptChart = ref(), bedChart = ref(), drugChart = ref()
const chartInstances = []

function makeChart(el, option) {
  const c = echarts.init(el)
  c.setOption(option)
  chartInstances.push(c)
}

function loadOverview() {
  getOverview().then(res => {
    const d = res.data || {}
    cards.value[0].value = d.todayReg
    cards.value[1].value = d.todayRevenue
    cards.value[2].value = d.inHospital
    cards.value[3].value = d.freeBeds
    cards.value[4].value = d.totalPatients
    cards.value[5].value = d.warnDrugs
    cards.value[6].value = d.pendingSurgery
    cards.value[7].value = d.pendingLab
  })
}

function loadCharts() {
  regTrend().then(res => {
    const rows = res.data || []
    makeChart(regChart.value, {
      tooltip: { trigger: 'axis' },
      grid: { left: 40, right: 20, top: 30, bottom: 30 },
      xAxis: { type: 'category', data: rows.map(r => r.day) },
      yAxis: { type: 'value' },
      series: [{ name: '挂号量', type: 'line', smooth: true, areaStyle: {}, data: rows.map(r => r.cnt), itemStyle: { color: '#409eff' } }]
    })
  })
  revenueTrend().then(res => {
    const rows = res.data || []
    makeChart(revChart.value, {
      tooltip: { trigger: 'axis' },
      grid: { left: 60, right: 20, top: 30, bottom: 30 },
      xAxis: { type: 'category', data: rows.map(r => r.day) },
      yAxis: { type: 'value' },
      series: [{ name: '收入', type: 'bar', barMaxWidth: 20, data: rows.map(r => r.amount), itemStyle: { color: '#67c23a' } }]
    })
  })
  deptReg().then(res => {
    const rows = res.data || []
    makeChart(deptChart.value, {
      tooltip: { trigger: 'item' },
      legend: { bottom: 0 },
      series: [{ name: '挂号量', type: 'pie', radius: ['40%', '65%'], data: rows.map(r => ({ name: r.name, value: r.value })) }]
    })
  })
  bedUsage().then(res => {
    const rows = res.data || []
    makeChart(bedChart.value, {
      tooltip: { trigger: 'axis' },
      legend: { bottom: 0 },
      grid: { left: 90, right: 30, top: 20, bottom: 40 },
      xAxis: { type: 'value' },
      yAxis: { type: 'category', data: rows.map(r => r.name) },
      series: [
        { name: '占用', type: 'bar', stack: 'b', data: rows.map(r => r.used), itemStyle: { color: '#f56c6c' } },
        { name: '空闲', type: 'bar', stack: 'b', data: rows.map(r => r.total - r.used), itemStyle: { color: '#67c23a' } }
      ]
    })
  })
  topDrugs().then(res => {
    const rows = res.data || []
    makeChart(drugChart.value, {
      tooltip: { trigger: 'axis' },
      grid: { left: 110, right: 30, top: 20, bottom: 30 },
      xAxis: { type: 'value' },
      yAxis: { type: 'category', data: rows.map(r => r.name).reverse() },
      series: [{ name: '消耗量', type: 'bar', data: rows.map(r => r.value).reverse(), itemStyle: { color: '#e6a23c' } }]
    })
  })
}

function onResize() { chartInstances.forEach(c => c.resize()) }

onMounted(() => {
  loadOverview()
  loadCharts()
  window.addEventListener('resize', onResize)
})
onBeforeUnmount(() => {
  window.removeEventListener('resize', onResize)
  chartInstances.forEach(c => c.dispose())
})
</script>

<style scoped>
.stat-card-inner { display: flex; align-items: center; gap: 14px; }
.stat-icon { width: 52px; height: 52px; border-radius: 10px; display: flex; align-items: center; justify-content: center; color: #fff; }
.stat-num { font-size: 24px; font-weight: 700; color: #303133; }
.stat-label { font-size: 13px; color: #909399; margin-top: 2px; }
</style>

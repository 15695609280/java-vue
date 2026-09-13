import request from '@/utils/request'

// 工作台总览卡片
export function getOverview() {
  return request({ url: '/his/statistics/overview', method: 'get' })
}

// 近14天挂号趋势
export function regTrend() {
  return request({ url: '/his/statistics/regTrend', method: 'get' })
}

// 近30天收入趋势
export function revenueTrend() {
  return request({ url: '/his/statistics/revenueTrend', method: 'get' })
}

// 科室挂号分布
export function deptReg() {
  return request({ url: '/his/statistics/deptReg', method: 'get' })
}

// 药品消耗TOP10
export function topDrugs() {
  return request({ url: '/his/statistics/topDrugs', method: 'get' })
}

// 病区床位使用率
export function bedUsage() {
  return request({ url: '/his/statistics/bedUsage', method: 'get' })
}

// 费用类型占比
export function chargeType() {
  return request({ url: '/his/statistics/chargeType', method: 'get' })
}

// 医生工作量TOP10
export function doctorWorkload() {
  return request({ url: '/his/statistics/doctorWorkload', method: 'get' })
}

// 近14天出入院趋势
export function admTrend() {
  return request({ url: '/his/statistics/admTrend', method: 'get' })
}

// 挂号时段分布
export function slotDist() {
  return request({ url: '/his/statistics/slotDist', method: 'get' })
}

// 科室收入分布
export function deptRevenue() {
  return request({ url: '/his/statistics/deptRevenue', method: 'get' })
}

// 本月经营汇总
export function monthSummary() {
  return request({ url: '/his/statistics/monthSummary', method: 'get' })
}

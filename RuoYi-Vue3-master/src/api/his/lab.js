import request from '@/utils/request'

// 查询检验单列表
export function listLabTest(query) {
  return request({
    url: '/his/lab/list',
    method: 'get',
    params: query
  })
}

// 查询检验单详细
export function getLabTest(testId) {
  return request({
    url: '/his/lab/' + testId,
    method: 'get'
  })
}

// 新增检验单
export function addLabTest(data) {
  return request({
    url: '/his/lab',
    method: 'post',
    data: data
  })
}

// 修改检验单
export function updateLabTest(data) {
  return request({
    url: '/his/lab',
    method: 'put',
    data: data
  })
}

// 删除检验单
export function delLabTest(testId) {
  return request({
    url: '/his/lab/' + testId,
    method: 'delete'
  })
}

// 检验状态流转 1采样 2开始检验 4作废
export function labFlow(testId, status) {
  return request({ url: '/his/lab/flow/' + testId + '/' + status, method: 'put' })
}

// 出报告(含结果明细)
export function reportLab(data) {
  return request({ url: '/his/lab/report', method: 'put', data: data })
}

// 检验结果明细
export function listLabResult(testId) {
  return request({ url: '/his/lab/results/' + testId, method: 'get' })
}

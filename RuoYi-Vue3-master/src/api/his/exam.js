import request from '@/utils/request'

// 查询检查单列表
export function listExam(query) {
  return request({
    url: '/his/exam/list',
    method: 'get',
    params: query
  })
}

// 查询检查单详细
export function getExam(examId) {
  return request({
    url: '/his/exam/' + examId,
    method: 'get'
  })
}

// 新增检查单
export function addExam(data) {
  return request({
    url: '/his/exam',
    method: 'post',
    data: data
  })
}

// 修改检查单
export function updateExam(data) {
  return request({
    url: '/his/exam',
    method: 'put',
    data: data
  })
}

// 删除检查单
export function delExam(examId) {
  return request({
    url: '/his/exam/' + examId,
    method: 'delete'
  })
}

// 检查状态流转 1已检查 3取消
export function examFlow(examId, status) {
  return request({ url: '/his/exam/flow/' + examId + '/' + status, method: 'put' })
}

// 出报告
export function reportExam(data) {
  return request({ url: '/his/exam/report', method: 'put', data: data })
}

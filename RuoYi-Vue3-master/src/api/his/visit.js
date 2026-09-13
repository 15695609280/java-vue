import request from '@/utils/request'

// 查询就诊记录列表
export function listVisit(query) {
  return request({
    url: '/his/visit/list',
    method: 'get',
    params: query
  })
}

// 查询就诊记录详细
export function getVisit(visitId) {
  return request({
    url: '/his/visit/' + visitId,
    method: 'get'
  })
}

// 新增就诊记录
export function addVisit(data) {
  return request({
    url: '/his/visit',
    method: 'post',
    data: data
  })
}

// 修改就诊记录
export function updateVisit(data) {
  return request({
    url: '/his/visit',
    method: 'put',
    data: data
  })
}

// 删除就诊记录
export function delVisit(visitId) {
  return request({
    url: '/his/visit/' + visitId,
    method: 'delete'
  })
}

// 完成就诊
export function finishVisit(visitId) {
  return request({ url: '/his/visit/finish/' + visitId, method: 'put' })
}

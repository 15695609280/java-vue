import request from '@/utils/request'

// 查询手术安排列表
export function listSurgery(query) {
  return request({
    url: '/his/surgery/list',
    method: 'get',
    params: query
  })
}

// 查询手术安排详细
export function getSurgery(surgeryId) {
  return request({
    url: '/his/surgery/' + surgeryId,
    method: 'get'
  })
}

// 新增手术安排
export function addSurgery(data) {
  return request({
    url: '/his/surgery',
    method: 'post',
    data: data
  })
}

// 修改手术安排
export function updateSurgery(data) {
  return request({
    url: '/his/surgery',
    method: 'put',
    data: data
  })
}

// 删除手术安排
export function delSurgery(surgeryId) {
  return request({
    url: '/his/surgery/' + surgeryId,
    method: 'delete'
  })
}

// 手术状态流转(1开始 2完成 3取消)
export function changeSurgeryStatus(id, status) {
  return request({ url: '/his/surgery/status/' + id + '/' + status, method: 'put' })
}

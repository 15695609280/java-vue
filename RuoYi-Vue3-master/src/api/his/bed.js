import request from '@/utils/request'

// 查询床位列表
export function listBed(query) {
  return request({
    url: '/his/bed/list',
    method: 'get',
    params: query
  })
}

// 查询床位详细
export function getBed(bedId) {
  return request({
    url: '/his/bed/' + bedId,
    method: 'get'
  })
}

// 新增床位
export function addBed(data) {
  return request({
    url: '/his/bed',
    method: 'post',
    data: data
  })
}

// 修改床位
export function updateBed(data) {
  return request({
    url: '/his/bed',
    method: 'put',
    data: data
  })
}

// 删除床位
export function delBed(bedId) {
  return request({
    url: '/his/bed/' + bedId,
    method: 'delete'
  })
}

// 查询病区空闲床位
export function listFreeBed(wardId) {
  return request({ url: '/his/bed/free/' + wardId, method: 'get' })
}

// 修改床位状态
export function changeBedStatus(bedId, status) {
  return request({ url: '/his/bed/status/' + bedId + '/' + status, method: 'put' })
}

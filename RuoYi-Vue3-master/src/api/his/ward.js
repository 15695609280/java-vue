import request from '@/utils/request'

// 查询病区列表
export function listWard(query) {
  return request({
    url: '/his/ward/list',
    method: 'get',
    params: query
  })
}

// 查询病区详细
export function getWard(wardId) {
  return request({
    url: '/his/ward/' + wardId,
    method: 'get'
  })
}

// 新增病区
export function addWard(data) {
  return request({
    url: '/his/ward',
    method: 'post',
    data: data
  })
}

// 修改病区
export function updateWard(data) {
  return request({
    url: '/his/ward',
    method: 'put',
    data: data
  })
}

// 删除病区
export function delWard(wardId) {
  return request({
    url: '/his/ward/' + wardId,
    method: 'delete'
  })
}

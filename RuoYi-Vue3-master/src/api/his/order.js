import request from '@/utils/request'

// 查询医嘱列表
export function listMedicalOrder(query) {
  return request({
    url: '/his/order/list',
    method: 'get',
    params: query
  })
}

// 查询医嘱详细
export function getMedicalOrder(orderId) {
  return request({
    url: '/his/order/' + orderId,
    method: 'get'
  })
}

// 新增医嘱
export function addMedicalOrder(data) {
  return request({
    url: '/his/order',
    method: 'post',
    data: data
  })
}

// 修改医嘱
export function updateMedicalOrder(data) {
  return request({
    url: '/his/order',
    method: 'put',
    data: data
  })
}

// 删除医嘱
export function delMedicalOrder(orderId) {
  return request({
    url: '/his/order/' + orderId,
    method: 'delete'
  })
}

// 执行医嘱
export function executeOrder(orderId) {
  return request({ url: '/his/order/execute/' + orderId, method: 'put' })
}

// 停止医嘱
export function stopOrder(orderId) {
  return request({ url: '/his/order/stop/' + orderId, method: 'put' })
}

import request from '@/utils/request'

// 查询处方列表
export function listPrescription(query) {
  return request({
    url: '/his/prescription/list',
    method: 'get',
    params: query
  })
}

// 查询处方详细
export function getPrescription(rxId) {
  return request({
    url: '/his/prescription/' + rxId,
    method: 'get'
  })
}

// 新增处方
export function addPrescription(data) {
  return request({
    url: '/his/prescription',
    method: 'post',
    data: data
  })
}

// 修改处方
export function updatePrescription(data) {
  return request({
    url: '/his/prescription',
    method: 'put',
    data: data
  })
}

// 删除处方
export function delPrescription(rxId) {
  return request({
    url: '/his/prescription/' + rxId,
    method: 'delete'
  })
}

// 开具处方(含明细)
export function createPrescription(data) {
  return request({ url: '/his/prescription/create', method: 'post', data: data })
}

// 发药
export function dispenseRx(rxId) {
  return request({ url: '/his/prescription/dispense/' + rxId, method: 'put' })
}

// 作废处方
export function cancelRx(rxId) {
  return request({ url: '/his/prescription/cancel/' + rxId, method: 'put' })
}

// 待发药处方(已收费)
export function listPaidRx(query) {
  return request({ url: '/his/prescription/paid', method: 'get', params: query })
}

// 处方明细
export function listRxItems(rxId) {
  return request({ url: '/his/prescription/items/' + rxId, method: 'get' })
}

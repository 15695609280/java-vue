import request from '@/utils/request'

// 查询费用单列表
export function listCharge(query) {
  return request({
    url: '/his/charge/list',
    method: 'get',
    params: query
  })
}

// 查询费用单详细
export function getCharge(chargeId) {
  return request({
    url: '/his/charge/' + chargeId,
    method: 'get'
  })
}

// 新增费用单
export function addCharge(data) {
  return request({
    url: '/his/charge',
    method: 'post',
    data: data
  })
}

// 修改费用单
export function updateCharge(data) {
  return request({
    url: '/his/charge',
    method: 'put',
    data: data
  })
}

// 删除费用单
export function delCharge(chargeId) {
  return request({
    url: '/his/charge/' + chargeId,
    method: 'delete'
  })
}

// 批量结算
export function settleCharge(chargeIds, payType) {
  return request({ url: '/his/charge/settle', method: 'put', data: { chargeIds: chargeIds, payType: payType } })
}

// 退费
export function refundCharge(chargeId) {
  return request({ url: '/his/charge/refund/' + chargeId, method: 'put' })
}

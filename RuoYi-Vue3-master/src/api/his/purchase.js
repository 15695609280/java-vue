import request from '@/utils/request'

// 查询采购单列表
export function listPurchase(query) {
  return request({
    url: '/his/purchase/list',
    method: 'get',
    params: query
  })
}

// 查询采购单详细
export function getPurchase(purchaseId) {
  return request({
    url: '/his/purchase/' + purchaseId,
    method: 'get'
  })
}

// 新增采购单
export function addPurchase(data) {
  return request({
    url: '/his/purchase',
    method: 'post',
    data: data
  })
}

// 修改采购单
export function updatePurchase(data) {
  return request({
    url: '/his/purchase',
    method: 'put',
    data: data
  })
}

// 删除采购单
export function delPurchase(purchaseId) {
  return request({
    url: '/his/purchase/' + purchaseId,
    method: 'delete'
  })
}

// 新建采购单(含明细)
export function createPurchase(data) {
  return request({ url: '/his/purchase/create', method: 'post', data: data })
}

// 入库审核
export function inboundPurchase(purchaseId) {
  return request({ url: '/his/purchase/inbound/' + purchaseId, method: 'put' })
}

// 采购明细
export function listPurchaseItem(purchaseId) {
  return request({ url: '/his/purchase/items/' + purchaseId, method: 'get' })
}

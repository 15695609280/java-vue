import request from '@/utils/request'

// 查询药品列表
export function listDrug(query) {
  return request({
    url: '/his/drug/list',
    method: 'get',
    params: query
  })
}

// 查询药品详细
export function getDrug(drugId) {
  return request({
    url: '/his/drug/' + drugId,
    method: 'get'
  })
}

// 新增药品
export function addDrug(data) {
  return request({
    url: '/his/drug',
    method: 'post',
    data: data
  })
}

// 修改药品
export function updateDrug(data) {
  return request({
    url: '/his/drug',
    method: 'put',
    data: data
  })
}

// 删除药品
export function delDrug(drugId) {
  return request({
    url: '/his/drug/' + drugId,
    method: 'delete'
  })
}

// 库存预警
export function listWarnDrug() {
  return request({ url: '/his/drug/warnList', method: 'get' })
}

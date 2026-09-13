import request from '@/utils/request'

// 查询出入库记录列表
export function listStockRecord(query) {
  return request({
    url: '/his/stockrecord/list',
    method: 'get',
    params: query
  })
}

// 查询出入库记录详细
export function getStockRecord(recordId) {
  return request({
    url: '/his/stockrecord/' + recordId,
    method: 'get'
  })
}

// 新增出入库记录
export function addStockRecord(data) {
  return request({
    url: '/his/stockrecord',
    method: 'post',
    data: data
  })
}

// 修改出入库记录
export function updateStockRecord(data) {
  return request({
    url: '/his/stockrecord',
    method: 'put',
    data: data
  })
}

// 删除出入库记录
export function delStockRecord(recordId) {
  return request({
    url: '/his/stockrecord/' + recordId,
    method: 'delete'
  })
}

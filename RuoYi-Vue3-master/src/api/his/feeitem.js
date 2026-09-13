import request from '@/utils/request'

// 查询收费项目列表
export function listFeeItem(query) {
  return request({
    url: '/his/feeitem/list',
    method: 'get',
    params: query
  })
}

// 查询收费项目详细
export function getFeeItem(itemId) {
  return request({
    url: '/his/feeitem/' + itemId,
    method: 'get'
  })
}

// 新增收费项目
export function addFeeItem(data) {
  return request({
    url: '/his/feeitem',
    method: 'post',
    data: data
  })
}

// 修改收费项目
export function updateFeeItem(data) {
  return request({
    url: '/his/feeitem',
    method: 'put',
    data: data
  })
}

// 删除收费项目
export function delFeeItem(itemId) {
  return request({
    url: '/his/feeitem/' + itemId,
    method: 'delete'
  })
}

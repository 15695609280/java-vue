import request from '@/utils/request'

// 查询护理记录列表
export function listNursing(query) {
  return request({
    url: '/his/nursing/list',
    method: 'get',
    params: query
  })
}

// 查询护理记录详细
export function getNursing(recordId) {
  return request({
    url: '/his/nursing/' + recordId,
    method: 'get'
  })
}

// 新增护理记录
export function addNursing(data) {
  return request({
    url: '/his/nursing',
    method: 'post',
    data: data
  })
}

// 修改护理记录
export function updateNursing(data) {
  return request({
    url: '/his/nursing',
    method: 'put',
    data: data
  })
}

// 删除护理记录
export function delNursing(recordId) {
  return request({
    url: '/his/nursing/' + recordId,
    method: 'delete'
  })
}

// 查询住院的生命体征曲线数据
export function listNursingByAdm(admId) {
  return request({ url: '/his/nursing/byAdm/' + admId, method: 'get' })
}

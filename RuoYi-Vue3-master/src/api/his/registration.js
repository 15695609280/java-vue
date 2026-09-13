import request from '@/utils/request'

// 查询挂号列表
export function listRegistration(query) {
  return request({
    url: '/his/registration/list',
    method: 'get',
    params: query
  })
}

// 查询挂号详细
export function getRegistration(regId) {
  return request({
    url: '/his/registration/' + regId,
    method: 'get'
  })
}

// 新增挂号
export function addRegistration(data) {
  return request({
    url: '/his/registration',
    method: 'post',
    data: data
  })
}

// 修改挂号
export function updateRegistration(data) {
  return request({
    url: '/his/registration',
    method: 'put',
    data: data
  })
}

// 删除挂号
export function delRegistration(regId) {
  return request({
    url: '/his/registration/' + regId,
    method: 'delete'
  })
}

// 挂号(选择排班，自动扣号源/生成排队号/生成挂号费账单)
export function register(data) {
  return request({ url: '/his/registration/register', method: 'post', data: data })
}

// 状态流转 1叫号 3退号 4过号
export function changeRegStatus(regId, status) {
  return request({ url: '/his/registration/status/' + regId + '/' + status, method: 'put' })
}

// 候诊队列
export function listQueue(query) {
  return request({ url: '/his/registration/queue', method: 'get', params: query })
}

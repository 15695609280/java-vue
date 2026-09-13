import request from '@/utils/request'

// 查询排班列表
export function listSchedule(query) {
  return request({
    url: '/his/schedule/list',
    method: 'get',
    params: query
  })
}

// 查询排班详细
export function getSchedule(scheduleId) {
  return request({
    url: '/his/schedule/' + scheduleId,
    method: 'get'
  })
}

// 新增排班
export function addSchedule(data) {
  return request({
    url: '/his/schedule',
    method: 'post',
    data: data
  })
}

// 修改排班
export function updateSchedule(data) {
  return request({
    url: '/his/schedule',
    method: 'put',
    data: data
  })
}

// 删除排班
export function delSchedule(scheduleId) {
  return request({
    url: '/his/schedule/' + scheduleId,
    method: 'delete'
  })
}

// 查询可挂号排班
export function listAvailableSchedule(query) {
  return request({ url: '/his/schedule/available', method: 'get', params: query })
}

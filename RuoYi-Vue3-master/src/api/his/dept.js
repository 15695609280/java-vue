import request from '@/utils/request'

// 查询科室列表
export function listDepartment(query) {
  return request({
    url: '/his/dept/list',
    method: 'get',
    params: query
  })
}

// 查询科室详细
export function getDepartment(deptId) {
  return request({
    url: '/his/dept/' + deptId,
    method: 'get'
  })
}

// 新增科室
export function addDepartment(data) {
  return request({
    url: '/his/dept',
    method: 'post',
    data: data
  })
}

// 修改科室
export function updateDepartment(data) {
  return request({
    url: '/his/dept',
    method: 'put',
    data: data
  })
}

// 删除科室
export function delDepartment(deptId) {
  return request({
    url: '/his/dept/' + deptId,
    method: 'delete'
  })
}

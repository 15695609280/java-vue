import request from '@/utils/request'

// 查询住院登记列表
export function listAdmission(query) {
  return request({
    url: '/his/admission/list',
    method: 'get',
    params: query
  })
}

// 查询住院登记详细
export function getAdmission(admId) {
  return request({
    url: '/his/admission/' + admId,
    method: 'get'
  })
}

// 新增住院登记
export function addAdmission(data) {
  return request({
    url: '/his/admission',
    method: 'post',
    data: data
  })
}

// 修改住院登记
export function updateAdmission(data) {
  return request({
    url: '/his/admission',
    method: 'put',
    data: data
  })
}

// 删除住院登记
export function delAdmission(admId) {
  return request({
    url: '/his/admission/' + admId,
    method: 'delete'
  })
}

// 住院登记(占用床位)
export function admit(data) {
  return request({ url: '/his/admission/admit', method: 'post', data: data })
}

// 出院(释放床位+生成床位费)
export function discharge(admId) {
  return request({ url: '/his/admission/discharge/' + admId, method: 'put' })
}

// 查询患者在院记录
export function getInHospital(patientId) {
  return request({ url: '/his/admission/inHospital/' + patientId, method: 'get' })
}

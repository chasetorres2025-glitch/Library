import request from '../utils/request'

export function recordBehavior(data) {
  return request({
    url: '/api/behavior',
    method: 'post',
    data
  })
}

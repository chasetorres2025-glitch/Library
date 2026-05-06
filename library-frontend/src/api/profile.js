import request from '../utils/request'

export function getUserProfile() {
  return request({
    url: '/api/profile',
    method: 'get'
  })
}

export function updateUserProfile(data) {
  return request({
    url: '/api/profile',
    method: 'put',
    data
  })
}

export function getProfileStatistics() {
  return request({
    url: '/api/profile/statistics',
    method: 'get'
  })
}

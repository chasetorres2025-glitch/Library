import request from '../utils/request'

export function getPersonalRecommend(limit = 10) {
  return request({
    url: '/api/recommend/personal',
    method: 'get',
    params: { limit }
  })
}

export function getSimilarBooks(bookId, limit = 5) {
  return request({
    url: `/api/recommend/similar/${bookId}`,
    method: 'get',
    params: { limit }
  })
}

export function getPopularBooks(limit = 10) {
  return request({
    url: '/api/recommend/popular',
    method: 'get',
    params: { limit }
  })
}

export function getRecentBooks(limit = 10) {
  return request({
    url: '/api/recommend/recent',
    method: 'get',
    params: { limit }
  })
}

import request from './request'

export function recordBehavior(data) {
  return request({
    url: '/api/behavior',
    method: 'post',
    data
  })
}

export function recordView(bookId, duration = 0) {
  return recordBehavior({
    bookId,
    behaviorType: 'view',
    duration
  })
}

export function recordSearch(keyword) {
  return recordBehavior({
    behaviorType: 'search',
    metadata: JSON.stringify({ keyword })
  })
}

export function recordFavorite(bookId) {
  return recordBehavior({
    bookId,
    behaviorType: 'favorite'
  })
}

export function recordRate(bookId, rating) {
  return recordBehavior({
    bookId,
    behaviorType: 'rate',
    rating
  })
}

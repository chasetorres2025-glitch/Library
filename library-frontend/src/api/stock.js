import request from '../utils/request'

export function getStockByBookId(bookId) {
  return request({
    url: `/api/stock/${bookId}`,
    method: 'get'
  })
}

export function addStock(data) {
  return request({
    url: '/api/stock',
    method: 'post',
    data
  })
}

export function updateStock(data) {
  return request({
    url: '/api/stock',
    method: 'put',
    data
  })
}

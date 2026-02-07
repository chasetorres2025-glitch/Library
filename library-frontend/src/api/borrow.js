import request from '../utils/request'

export function getBorrowList(params) {
  return request({
    url: '/api/borrow/list',
    method: 'get',
    params
  })
}

export function getMyBorrows(params) {
  return request({
    url: '/api/borrow/my-borrows',
    method: 'get',
    params
  })
}

export function borrowBook(data) {
  return request({
    url: '/api/borrow',
    method: 'post',
    data
  })
}

export function returnBook(id, operatorId) {
  return request({
    url: `/api/borrow/return/${id}`,
    method: 'put',
    params: { operatorId }
  })
}

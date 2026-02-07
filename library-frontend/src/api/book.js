import request from '../utils/request'

export function getBookList(params) {
  return request({
    url: '/api/book/list',
    method: 'get',
    params
  })
}

export function getBookById(id) {
  return request({
    url: `/api/book/${id}`,
    method: 'get'
  })
}

export function addBook(data) {
  return request({
    url: '/api/book',
    method: 'post',
    data
  })
}

export function updateBook(data) {
  return request({
    url: '/api/book',
    method: 'put',
    data
  })
}

export function deleteBook(id) {
  return request({
    url: `/api/book/${id}`,
    method: 'delete'
  })
}

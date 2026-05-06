import request from '../utils/request'

export function getBookTagList(tagType) {
  return request({
    url: '/api/book-tags',
    method: 'get',
    params: tagType ? { tagType } : {}
  })
}

export function getTagsByBookId(bookId) {
  return request({
    url: `/api/book-tags/book/${bookId}`,
    method: 'get'
  })
}

export function addBookTag(data) {
  return request({
    url: '/api/book-tags',
    method: 'post',
    data
  })
}

export function updateBookTag(id, data) {
  return request({
    url: `/api/book-tags/${id}`,
    method: 'put',
    data
  })
}

export function deleteBookTag(id) {
  return request({
    url: `/api/book-tags/${id}`,
    method: 'delete'
  })
}

export function assignTagToBook(data) {
  return request({
    url: '/api/book-tags/assign',
    method: 'post',
    data
  })
}

export function removeTagFromBook(data) {
  return request({
    url: '/api/book-tags/remove',
    method: 'post',
    data
  })
}

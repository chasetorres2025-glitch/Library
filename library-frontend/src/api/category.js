import request from '../utils/request'

export function getCategoryList(params) {
  return request({
    url: '/api/category/list',
    method: 'get',
    params
  })
}

export function getAllCategories() {
  return request({
    url: '/api/category/all',
    method: 'get'
  })
}

export function getCategoryById(id) {
  return request({
    url: `/api/category/${id}`,
    method: 'get'
  })
}

export function addCategory(data) {
  return request({
    url: '/api/category',
    method: 'post',
    data
  })
}

export function updateCategory(data) {
  return request({
    url: '/api/category',
    method: 'put',
    data
  })
}

export function deleteCategory(id) {
  return request({
    url: `/api/category/${id}`,
    method: 'delete'
  })
}

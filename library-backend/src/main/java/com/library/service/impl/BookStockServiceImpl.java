package com.library.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.entity.BookStock;
import com.library.mapper.BookStockMapper;
import com.library.service.BookStockService;
import org.springframework.stereotype.Service;

@Service
public class BookStockServiceImpl extends ServiceImpl<BookStockMapper, BookStock> implements BookStockService {

}

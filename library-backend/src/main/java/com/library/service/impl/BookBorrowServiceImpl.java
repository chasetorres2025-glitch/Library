package com.library.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.entity.BookBorrow;
import com.library.mapper.BookBorrowMapper;
import com.library.service.BookBorrowService;
import org.springframework.stereotype.Service;

@Service
public class BookBorrowServiceImpl extends ServiceImpl<BookBorrowMapper, BookBorrow> implements BookBorrowService {

}

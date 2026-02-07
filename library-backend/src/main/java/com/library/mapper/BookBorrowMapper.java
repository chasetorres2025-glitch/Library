package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.BookBorrow;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BookBorrowMapper extends BaseMapper<BookBorrow> {

}

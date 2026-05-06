package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.BookTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BookTagMapper extends BaseMapper<BookTag> {

    @Select("SELECT t.* FROM book_tag t " +
            "INNER JOIN book_tag_relation r ON t.id = r.tag_id " +
            "WHERE r.book_id = #{bookId} ORDER BY r.weight DESC")
    List<BookTag> selectTagsByBookId(@Param("bookId") Long bookId);

}

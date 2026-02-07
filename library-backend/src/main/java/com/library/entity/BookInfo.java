package com.library.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("book_info")
public class BookInfo {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String bookIsbn;

    private String bookName;

    private String author;

    private String publisher;

    private LocalDate publishTime;

    private Long categoryId;

    private BigDecimal price;

    private String coverUrl;

    private String description;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}

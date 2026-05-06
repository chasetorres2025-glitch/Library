package com.library.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("book_tag_relation")
public class BookTagRelation {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long bookId;

    private Long tagId;

    private BigDecimal weight;

    private String source;

    private LocalDateTime createdAt;

}

package com.library.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("book_tag")
public class BookTag {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String tagName;

    private String tagType;

    private Long parentId;

    private Integer sort;

    private Integer usageCount;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}

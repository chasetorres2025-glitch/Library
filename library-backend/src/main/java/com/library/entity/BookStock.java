package com.library.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("book_stock")
public class BookStock {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long bookId;

    private Integer totalNum;

    private Integer borrowNum;

    private Integer availableNum;

    private LocalDateTime updateTime;

}

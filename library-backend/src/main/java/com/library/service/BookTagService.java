package com.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.entity.BookTag;

import java.util.List;

public interface BookTagService extends IService<BookTag> {

    List<BookTag> getTagsByBookId(Long bookId);

    void assignTagToBook(Long bookId, Long tagId, Double weight);

    void removeTagFromBook(Long bookId, Long tagId);

}

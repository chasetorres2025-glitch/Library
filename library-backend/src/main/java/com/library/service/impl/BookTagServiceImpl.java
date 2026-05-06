package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.entity.BookTag;
import com.library.entity.BookTagRelation;
import com.library.mapper.BookTagMapper;
import com.library.mapper.BookTagRelationMapper;
import com.library.service.BookTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookTagServiceImpl extends ServiceImpl<BookTagMapper, BookTag> implements BookTagService {

    @Autowired
    private BookTagRelationMapper bookTagRelationMapper;

    @Override
    public List<BookTag> getTagsByBookId(Long bookId) {
        return baseMapper.selectTagsByBookId(bookId);
    }

    @Override
    @Transactional
    public void assignTagToBook(Long bookId, Long tagId, Double weight) {
        LambdaQueryWrapper<BookTagRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookTagRelation::getBookId, bookId).eq(BookTagRelation::getTagId, tagId);
        BookTagRelation existing = bookTagRelationMapper.selectOne(wrapper);
        if (existing != null) {
            existing.setWeight(BigDecimal.valueOf(weight));
            bookTagRelationMapper.updateById(existing);
        } else {
            BookTagRelation relation = new BookTagRelation();
            relation.setBookId(bookId);
            relation.setTagId(tagId);
            relation.setWeight(BigDecimal.valueOf(weight));
            relation.setSource("manual");
            relation.setCreatedAt(LocalDateTime.now());
            bookTagRelationMapper.insert(relation);
        }
    }

    @Override
    @Transactional
    public void removeTagFromBook(Long bookId, Long tagId) {
        LambdaQueryWrapper<BookTagRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookTagRelation::getBookId, bookId).eq(BookTagRelation::getTagId, tagId);
        bookTagRelationMapper.delete(wrapper);
    }

}

package com.library.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.BookInfo;
import com.library.entity.BookStock;
import com.library.service.BookInfoService;
import com.library.service.BookStockService;
import com.library.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/book")
public class BookInfoController {

    @Autowired
    private BookInfoService bookInfoService;

    @Autowired
    private BookStockService bookStockService;

    @GetMapping("/list")
    public Result<Page<BookInfo>> list(@RequestParam(defaultValue = "1") Integer current,
                                        @RequestParam(defaultValue = "10") Integer size,
                                        @RequestParam(required = false) String bookName,
                                        @RequestParam(required = false) String author,
                                        @RequestParam(required = false) String bookIsbn,
                                        @RequestParam(required = false) Long categoryId) {
        Page<BookInfo> page = new Page<>(current, size);
        LambdaQueryWrapper<BookInfo> wrapper = new LambdaQueryWrapper<>();
        if (bookName != null && !bookName.isEmpty()) {
            wrapper.like(BookInfo::getBookName, bookName);
        }
        if (author != null && !author.isEmpty()) {
            wrapper.like(BookInfo::getAuthor, author);
        }
        if (bookIsbn != null && !bookIsbn.isEmpty()) {
            wrapper.eq(BookInfo::getBookIsbn, bookIsbn);
        }
        if (categoryId != null) {
            wrapper.eq(BookInfo::getCategoryId, categoryId);
        }
        page = bookInfoService.page(page, wrapper);
        return Result.success(page);
    }

    @GetMapping("/{id}")
    public Result<BookInfo> getById(@PathVariable Long id) {
        BookInfo book = bookInfoService.getById(id);
        return Result.success(book);
    }

    @PostMapping
    public Result<String> add(@RequestBody BookInfo book) {
        LambdaQueryWrapper<BookInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookInfo::getBookIsbn, book.getBookIsbn());
        if (bookInfoService.count(wrapper) > 0) {
            return Result.error("ISBN已存在");
        }
        
        book.setCreateTime(LocalDateTime.now());
        book.setUpdateTime(LocalDateTime.now());
        
        bookInfoService.save(book);
        
        BookStock stock = new BookStock();
        stock.setBookId(book.getId());
        stock.setTotalNum(0);
        stock.setBorrowNum(0);
        stock.setAvailableNum(0);
        bookStockService.save(stock);
        
        return Result.success("添加成功");
    }

    @PutMapping
    public Result<String> update(@RequestBody BookInfo book) {
        // 设置更新时间
        book.setUpdateTime(LocalDateTime.now());
        bookInfoService.updateById(book);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        bookInfoService.removeById(id);
        return Result.success("删除成功");
    }

}

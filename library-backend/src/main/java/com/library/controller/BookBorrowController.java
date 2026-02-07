package com.library.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.BookBorrow;
import com.library.entity.BookStock;
import com.library.service.BookBorrowService;
import com.library.service.BookStockService;
import com.library.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/borrow")
public class BookBorrowController {

    @Autowired
    private BookBorrowService bookBorrowService;

    @Autowired
    private BookStockService bookStockService;

    @GetMapping("/list")
    public Result<Page<BookBorrow>> list(@RequestParam(defaultValue = "1") Integer current,
                                          @RequestParam(defaultValue = "10") Integer size,
                                          @RequestParam(required = false) Long userId,
                                          @RequestParam(required = false) Long bookId,
                                          @RequestParam(required = false) Integer status) {
        Page<BookBorrow> page = new Page<>(current, size);
        LambdaQueryWrapper<BookBorrow> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(BookBorrow::getUserId, userId);
        }
        if (bookId != null) {
            wrapper.eq(BookBorrow::getBookId, bookId);
        }
        if (status != null) {
            wrapper.eq(BookBorrow::getStatus, status);
        }
        wrapper.orderByDesc(BookBorrow::getBorrowTime);
        page = bookBorrowService.page(page, wrapper);
        return Result.success(page);
    }

    @GetMapping("/my-borrows")
    public Result<Page<BookBorrow>> myBorrows(@RequestParam(defaultValue = "1") Integer current,
                                               @RequestParam(defaultValue = "10") Integer size,
                                               @RequestParam Long userId,
                                               @RequestParam(required = false) Integer status) {
        Page<BookBorrow> page = new Page<>(current, size);
        LambdaQueryWrapper<BookBorrow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookBorrow::getUserId, userId);
        if (status != null) {
            wrapper.eq(BookBorrow::getStatus, status);
        }
        wrapper.orderByDesc(BookBorrow::getBorrowTime);
        page = bookBorrowService.page(page, wrapper);
        return Result.success(page);
    }

    @PostMapping
    @Transactional
    public Result<String> borrow(@RequestBody BookBorrow borrow) {
        LambdaQueryWrapper<BookStock> stockWrapper = new LambdaQueryWrapper<>();
        stockWrapper.eq(BookStock::getBookId, borrow.getBookId());
        BookStock stock = bookStockService.getOne(stockWrapper);

        if (stock == null) {
            return Result.error("图书库存不存在");
        }

        if (stock.getAvailableNum() <= 0) {
            return Result.error("图书库存不足");
        }

        borrow.setBorrowTime(LocalDateTime.now());
        borrow.setDueTime(LocalDateTime.now().plusDays(30));
        borrow.setStatus(0);
        bookBorrowService.save(borrow);

        stock.setBorrowNum(stock.getBorrowNum() + 1);
        stock.setAvailableNum(stock.getTotalNum() - stock.getBorrowNum());
        bookStockService.updateById(stock);

        return Result.success("借阅成功");
    }

    @PutMapping("/return/{id}")
    @Transactional
    public Result<String> returnBook(@PathVariable Long id, @RequestParam Long operatorId) {
        BookBorrow borrow = bookBorrowService.getById(id);
        if (borrow == null) {
            return Result.error("借阅记录不存在");
        }

        if (borrow.getStatus() == 1) {
            return Result.error("图书已归还");
        }

        borrow.setReturnTime(LocalDateTime.now());
        borrow.setStatus(1);
        bookBorrowService.updateById(borrow);

        LambdaQueryWrapper<BookStock> stockWrapper = new LambdaQueryWrapper<>();
        stockWrapper.eq(BookStock::getBookId, borrow.getBookId());
        BookStock stock = bookStockService.getOne(stockWrapper);

        if (stock != null) {
            stock.setBorrowNum(stock.getBorrowNum() - 1);
            stock.setAvailableNum(stock.getTotalNum() - stock.getBorrowNum());
            bookStockService.updateById(stock);
        }

        return Result.success("归还成功");
    }

}

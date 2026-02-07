package com.library.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.library.entity.BookStock;
import com.library.service.BookStockService;
import com.library.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stock")
public class BookStockController {

    @Autowired
    private BookStockService bookStockService;

    @GetMapping("/{bookId}")
    public Result<BookStock> getByBookId(@PathVariable Long bookId) {
        LambdaQueryWrapper<BookStock> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookStock::getBookId, bookId);
        BookStock stock = bookStockService.getOne(wrapper);
        return Result.success(stock);
    }

    @PostMapping
    public Result<String> add(@RequestBody BookStock stock) {
        LambdaQueryWrapper<BookStock> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookStock::getBookId, stock.getBookId());
        if (bookStockService.count(wrapper) > 0) {
            return Result.error("该图书库存已存在");
        }
        stock.setAvailableNum(stock.getTotalNum());
        bookStockService.save(stock);
        return Result.success("添加成功");
    }

    @PutMapping
    public Result<String> update(@RequestBody BookStock stock) {
        stock.setAvailableNum(stock.getTotalNum() - stock.getBorrowNum());
        bookStockService.updateById(stock);
        return Result.success("更新成功");
    }

}

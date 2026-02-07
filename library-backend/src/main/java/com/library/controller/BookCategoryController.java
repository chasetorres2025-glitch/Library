package com.library.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.BookCategory;
import com.library.service.BookCategoryService;
import com.library.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/category")
public class BookCategoryController {

    @Autowired
    private BookCategoryService bookCategoryService;

    @GetMapping("/list")
    public Result<Page<BookCategory>> list(@RequestParam(defaultValue = "1") Integer current,
                                           @RequestParam(defaultValue = "10") Integer size) {
        Page<BookCategory> page = new Page<>(current, size);
        LambdaQueryWrapper<BookCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(BookCategory::getSort);
        page = bookCategoryService.page(page, wrapper);
        return Result.success(page);
    }

    @GetMapping("/all")
    public Result<java.util.List<BookCategory>> all() {
        LambdaQueryWrapper<BookCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(BookCategory::getSort);
        return Result.success(bookCategoryService.list(wrapper));
    }

    @GetMapping("/{id}")
    public Result<BookCategory> getById(@PathVariable Long id) {
        BookCategory category = bookCategoryService.getById(id);
        return Result.success(category);
    }

    @PostMapping
    public Result<String> add(@RequestBody BookCategory category) {
        bookCategoryService.save(category);
        return Result.success("添加成功");
    }

    @PutMapping
    public Result<String> update(@RequestBody BookCategory category) {
        bookCategoryService.updateById(category);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        bookCategoryService.removeById(id);
        return Result.success("删除成功");
    }

}

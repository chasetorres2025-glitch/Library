package com.library.controller;

import com.library.dto.AssignTagRequest;
import com.library.entity.BookTag;
import com.library.service.BookTagService;
import com.library.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/book-tags")
public class BookTagController {

    @Autowired
    private BookTagService bookTagService;

    @GetMapping
    public Result<List<BookTag>> list(@RequestParam(required = false) String tagType) {
        List<BookTag> list;
        if (tagType != null && !tagType.isEmpty()) {
            list = bookTagService.lambdaQuery().eq(BookTag::getTagType, tagType).list();
        } else {
            list = bookTagService.list();
        }
        return Result.success(list);
    }

    @GetMapping("/book/{bookId}")
    public Result<List<BookTag>> getTagsByBookId(@PathVariable Long bookId) {
        List<BookTag> tags = bookTagService.getTagsByBookId(bookId);
        return Result.success(tags);
    }

    @PostMapping
    public Result<String> add(@RequestBody BookTag tag) {
        bookTagService.save(tag);
        return Result.success("添加成功");
    }

    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Long id, @RequestBody BookTag tag) {
        tag.setId(id);
        bookTagService.updateById(tag);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        bookTagService.removeById(id);
        return Result.success("删除成功");
    }

    @PostMapping("/assign")
    public Result<String> assignTag(@RequestBody AssignTagRequest request) {
        Double weight = request.getWeight() != null ? request.getWeight() : 1.0;
        bookTagService.assignTagToBook(request.getBookId(), request.getTagId(), weight);
        return Result.success("打标签成功");
    }

    @PostMapping("/remove")
    public Result<String> removeTag(@RequestBody AssignTagRequest request) {
        bookTagService.removeTagFromBook(request.getBookId(), request.getTagId());
        return Result.success("移除标签成功");
    }

}

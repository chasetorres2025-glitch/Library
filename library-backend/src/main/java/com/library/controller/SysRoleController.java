package com.library.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.SysRole;
import com.library.service.SysRoleService;
import com.library.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/role")
public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;

    @GetMapping("/list")
    public Result<Page<SysRole>> list(@RequestParam(defaultValue = "1") Integer current,
                                       @RequestParam(defaultValue = "10") Integer size) {
        Page<SysRole> page = new Page<>(current, size);
        page = sysRoleService.page(page);
        return Result.success(page);
    }

    @GetMapping("/{id}")
    public Result<SysRole> getById(@PathVariable Long id) {
        SysRole role = sysRoleService.getById(id);
        return Result.success(role);
    }

    @PostMapping
    public Result<String> add(@RequestBody SysRole role) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getRoleCode, role.getRoleCode());
        if (sysRoleService.count(wrapper) > 0) {
            return Result.error("角色编码已存在");
        }
        sysRoleService.save(role);
        return Result.success("添加成功");
    }

    @PutMapping
    public Result<String> update(@RequestBody SysRole role) {
        sysRoleService.updateById(role);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        sysRoleService.removeById(id);
        return Result.success("删除成功");
    }

}

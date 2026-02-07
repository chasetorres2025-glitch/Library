package com.library.controller;

import com.library.service.BookBorrowService;
import com.library.service.BookInfoService;
import com.library.service.BookStockService;
import com.library.service.SysUserService;
import com.library.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private BookInfoService bookInfoService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private BookBorrowService bookBorrowService;

    @Autowired
    private BookStockService bookStockService;

    @GetMapping("/overview")
    public Result<Map<String, Object>> getOverviewStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        // 图书总数
        statistics.put("totalBooks", bookInfoService.count());

        // 用户总数
        statistics.put("totalUsers", sysUserService.count());

        // 借阅总数
        statistics.put("totalBorrows", bookBorrowService.count());

        // 逾期未还数量 (状态为0且到期时间小于当前时间)
        LocalDateTime now = LocalDateTime.now();
        statistics.put("overdueBorrows", 
            bookBorrowService.lambdaQuery()
                .eq(com.library.entity.BookBorrow::getStatus, 0)
                .lt(com.library.entity.BookBorrow::getDueTime, now)
                .count());

        // 最近7天的借阅趋势
        LocalDateTime sevenDaysAgo = now.minusDays(7);
        Map<String, Long> borrowTrend = new HashMap<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(23, 59, 59);
            
            long count = bookBorrowService.lambdaQuery()
                .between(com.library.entity.BookBorrow::getBorrowTime, startOfDay, endOfDay)
                .count();
            
            borrowTrend.put(date.toString(), count);
        }
        statistics.put("borrowTrend", borrowTrend);

        // 最新图书 (最近添加的5本)
        statistics.put("recentBooks", bookInfoService.lambdaQuery()
                .orderByDesc(com.library.entity.BookInfo::getCreateTime)
                .last("LIMIT 5")
                .list());

        // 系统公告 (这里可以添加公告服务)
        statistics.put("notices", getDefaultNotices());

        return Result.success(statistics);
    }

    private Map<String, Object> getDefaultNotices() {
        Map<String, Object> notices = new HashMap<>();
        notices.put("title", "欢迎使用图书管理系统");
        notices.put("description", "系统已正常运行");
        notices.put("time", LocalDate.now().toString());
        return notices;
    }
}

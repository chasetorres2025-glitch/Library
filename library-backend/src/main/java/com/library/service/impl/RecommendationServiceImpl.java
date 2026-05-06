package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.library.entity.*;
import com.library.mapper.*;
import com.library.service.RecommendationService;
import com.library.vo.RecommendBookVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationServiceImpl implements RecommendationService {

    @Autowired
    private BookInfoMapper bookInfoMapper;

    @Autowired
    private BookTagMapper bookTagMapper;

    @Autowired
    private BookTagRelationMapper bookTagRelationMapper;

    @Autowired
    private BookBorrowMapper bookBorrowMapper;

    @Autowired
    private UserBehaviorMapper userBehaviorMapper;

    @Autowired
    private UserProfileMapper userProfileMapper;

    @Autowired
    private BookCategoryMapper bookCategoryMapper;

    private static final double ALPHA_CONTENT = 0.4;
    private static final double BETA_COLLABORATIVE = 0.3;
    private static final double GAMMA_POPULAR = 0.2;
    private static final double DELTA_RECENCY = 0.1;

    @Override
    public List<RecommendBookVo> getPersonalRecommendations(Long userId, int limit) {
        List<BookInfo> allBooks = bookInfoMapper.selectList(null);
        if (allBooks.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> excludedBookIds = getUserInteractedBookIds(userId);

        UserProfile profile = null;
        if (userId != null) {
            LambdaQueryWrapper<UserProfile> pw = new LambdaQueryWrapper<>();
            pw.eq(UserProfile::getUserId, userId);
            profile = userProfileMapper.selectOne(pw);
        }

        List<Long> preferredCategoryIds = new ArrayList<>();
        Set<String> preferredTags = new HashSet<>();

        if (profile != null && profile.getPreferredCategories() != null) {
            String cats = profile.getPreferredCategories();
            if (cats.startsWith("[")) {
                try {
                    cats = cats.replace("[", "").replace("]", "").replace("\"", "");
                    for (String cat : cats.split(",")) {
                        String catName = cat.trim();
                        if (!catName.isEmpty()) {
                            LambdaQueryWrapper<BookCategory> cw = new LambdaQueryWrapper<>();
                            cw.eq(BookCategory::getCategoryName, catName);
                            BookCategory bc = bookCategoryMapper.selectOne(cw);
                            if (bc != null) {
                                preferredCategoryIds.add(bc.getId());
                            }
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        }

        if (profile != null && profile.getSkillTags() != null) {
            String tags = profile.getSkillTags();
            if (tags.startsWith("[")) {
                try {
                    tags = tags.replace("[", "").replace("]", "").replace("\"", "");
                    for (String t : tags.split(",")) {
                        preferredTags.add(t.trim());
                    }
                } catch (Exception ignored) {
                }
            }
        }

        boolean hasPreferences = !preferredCategoryIds.isEmpty() || !preferredTags.isEmpty();

        Map<Long, Double> bookScores = new HashMap<>();
        Map<Long, String> bookReasons = new HashMap<>();

        for (BookInfo book : allBooks) {
            if (excludedBookIds.contains(book.getId())) {
                continue;
            }

            double contentScore = 0.0;
            double popularScore = 0.0;
            double recencyScore = 0.0;

            List<BookTag> tags = bookTagMapper.selectTagsByBookId(book.getId());
            Set<String> tagNames = tags.stream().map(BookTag::getTagName).collect(Collectors.toSet());

            if (hasPreferences) {
                if (preferredCategoryIds.contains(book.getCategoryId())) {
                    contentScore += 0.6;
                }
                if (!preferredTags.isEmpty() && !tagNames.isEmpty()) {
                    Set<String> intersection = new HashSet<>(preferredTags);
                    intersection.retainAll(tagNames);
                    if (!intersection.isEmpty()) {
                        contentScore += 0.4 * ((double) intersection.size() / Math.max(preferredTags.size(), tagNames.size()));
                    }
                }
            } else {
                contentScore = 0.1;
            }

            LambdaQueryWrapper<BookBorrow> borrowWrapper = new LambdaQueryWrapper<>();
            borrowWrapper.eq(BookBorrow::getBookId, book.getId());
            long borrowCount = bookBorrowMapper.selectCount(borrowWrapper);
            popularScore = Math.min(borrowCount / 10.0, 1.0);

            LocalDateTime createTime = book.getCreateTime();
            if (createTime != null) {
                long days = java.time.Duration.between(createTime, LocalDateTime.now()).toDays();
                if (days <= 30) {
                    recencyScore = 1.0;
                } else if (days <= 90) {
                    recencyScore = 0.5;
                } else if (days <= 180) {
                    recencyScore = 0.2;
                }
            }

            double totalScore = ALPHA_CONTENT * contentScore + GAMMA_POPULAR * popularScore + DELTA_RECENCY * recencyScore;
            bookScores.put(book.getId(), totalScore);

            if (contentScore >= 0.5) {
                bookReasons.put(book.getId(), "根据您的阅读偏好推荐");
            } else if (popularScore >= 0.5) {
                bookReasons.put(book.getId(), "热门借阅图书");
            } else if (recencyScore >= 0.5) {
                bookReasons.put(book.getId(), "新上架图书");
            } else {
                bookReasons.put(book.getId(), "猜您喜欢");
            }
        }

        List<Long> sortedBookIds = bookScores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .limit(limit)
                .collect(Collectors.toList());

        return buildRecommendVoList(sortedBookIds, bookScores, bookReasons);
    }

    @Override
    public List<RecommendBookVo> getSimilarBooks(Long bookId, int limit) {
        BookInfo sourceBook = bookInfoMapper.selectById(bookId);
        if (sourceBook == null) {
            return Collections.emptyList();
        }

        List<BookTag> sourceTags = bookTagMapper.selectTagsByBookId(bookId);
        Set<String> sourceTagNames = sourceTags.stream().map(BookTag::getTagName).collect(Collectors.toSet());

        List<BookInfo> allBooks = bookInfoMapper.selectList(null);
        Map<Long, Double> similarityScores = new HashMap<>();

        for (BookInfo book : allBooks) {
            if (book.getId().equals(bookId)) {
                continue;
            }

            double score = 0.0;
            if (sourceBook.getCategoryId() != null && sourceBook.getCategoryId().equals(book.getCategoryId())) {
                score += 0.3;
            }

            List<BookTag> tags = bookTagMapper.selectTagsByBookId(book.getId());
            Set<String> tagNames = tags.stream().map(BookTag::getTagName).collect(Collectors.toSet());

            if (!sourceTagNames.isEmpty() && !tagNames.isEmpty()) {
                Set<String> intersection = new HashSet<>(sourceTagNames);
                intersection.retainAll(tagNames);
                Set<String> union = new HashSet<>(sourceTagNames);
                union.addAll(tagNames);
                if (!union.isEmpty()) {
                    score += 0.7 * ((double) intersection.size() / union.size());
                }
            }

            similarityScores.put(book.getId(), score);
        }

        List<Long> sortedIds = similarityScores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .limit(limit)
                .collect(Collectors.toList());

        Map<Long, String> reasons = new HashMap<>();
        for (Long id : sortedIds) {
            reasons.put(id, "相似图书");
        }

        return buildRecommendVoList(sortedIds, similarityScores, reasons);
    }

    @Override
    public List<RecommendBookVo> getPopularBooks(int limit) {
        List<BookInfo> allBooks = bookInfoMapper.selectList(null);
        Map<Long, Long> borrowCounts = new HashMap<>();

        for (BookInfo book : allBooks) {
            LambdaQueryWrapper<BookBorrow> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(BookBorrow::getBookId, book.getId());
            borrowCounts.put(book.getId(), bookBorrowMapper.selectCount(wrapper));
        }

        List<Long> sortedIds = borrowCounts.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .limit(limit)
                .collect(Collectors.toList());

        Map<Long, Double> scores = new HashMap<>();
        Map<Long, String> reasons = new HashMap<>();
        for (Long id : sortedIds) {
            scores.put(id, borrowCounts.get(id).doubleValue());
            reasons.put(id, "热门借阅");
        }

        return buildRecommendVoList(sortedIds, scores, reasons);
    }

    @Override
    public List<RecommendBookVo> getRecentBooks(int limit) {
        LambdaQueryWrapper<BookInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(BookInfo::getCreateTime);
        wrapper.last("LIMIT " + limit);
        List<BookInfo> books = bookInfoMapper.selectList(wrapper);

        Map<Long, Double> scores = new HashMap<>();
        Map<Long, String> reasons = new HashMap<>();
        for (BookInfo book : books) {
            scores.put(book.getId(), 1.0);
            reasons.put(book.getId(), "新上架");
        }

        return buildRecommendVoList(
                books.stream().map(BookInfo::getId).collect(Collectors.toList()),
                scores, reasons);
    }

    private Set<Long> getUserInteractedBookIds(Long userId) {
        Set<Long> ids = new HashSet<>();
        if (userId == null) {
            return ids;
        }
        LambdaQueryWrapper<BookBorrow> bw = new LambdaQueryWrapper<>();
        bw.eq(BookBorrow::getUserId, userId);
        List<BookBorrow> borrows = bookBorrowMapper.selectList(bw);
        for (BookBorrow b : borrows) {
            ids.add(b.getBookId());
        }
        LambdaQueryWrapper<UserBehavior> uw = new LambdaQueryWrapper<>();
        uw.eq(UserBehavior::getUserId, userId);
        List<UserBehavior> behaviors = userBehaviorMapper.selectList(uw);
        for (UserBehavior ub : behaviors) {
            if (ub.getBookId() != null) {
                ids.add(ub.getBookId());
            }
        }
        return ids;
    }

    private List<RecommendBookVo> buildRecommendVoList(List<Long> bookIds, Map<Long, Double> scores, Map<Long, String> reasons) {
        List<RecommendBookVo> result = new ArrayList<>();
        for (Long bookId : bookIds) {
            BookInfo book = bookInfoMapper.selectById(bookId);
            if (book == null) {
                continue;
            }
            RecommendBookVo vo = new RecommendBookVo();
            vo.setBookId(book.getId());
            vo.setBookName(book.getBookName());
            vo.setAuthor(book.getAuthor());
            vo.setCoverUrl(book.getCoverUrl());
            vo.setScore(scores.getOrDefault(bookId, 0.0));
            vo.setReason(reasons.getOrDefault(bookId, "推荐"));
            result.add(vo);
        }
        return result;
    }

}

package com.library.service;

import com.library.vo.RecommendBookVo;

import java.util.List;

public interface RecommendationService {

    List<RecommendBookVo> getPersonalRecommendations(Long userId, int limit);

    List<RecommendBookVo> getSimilarBooks(Long bookId, int limit);

    List<RecommendBookVo> getPopularBooks(int limit);

    List<RecommendBookVo> getRecentBooks(int limit);

}

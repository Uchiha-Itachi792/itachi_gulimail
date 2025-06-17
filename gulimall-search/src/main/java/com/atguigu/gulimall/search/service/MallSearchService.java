package com.atguigu.gulimall.search.service;

import com.atguigu.common.vo.search.SearchParam;
import com.atguigu.common.vo.search.SearchResult;

public interface MallSearchService {

    /**
     * 检索商品
     */
    SearchResult search(SearchParam param);
}

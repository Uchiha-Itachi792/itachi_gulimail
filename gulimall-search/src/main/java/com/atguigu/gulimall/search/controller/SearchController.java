package com.atguigu.gulimall.search.controller;

import com.atguigu.common.vo.search.SearchParam;
import com.atguigu.common.vo.search.SearchResult;
import com.atguigu.gulimall.search.service.impl.MallSearchServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;


import javax.servlet.http.HttpServletRequest;

@Controller
public class SearchController {

    @Autowired
    MallSearchServiceImpl mallSearchService;

    @GetMapping("/list.html")
    public String listPage(SearchParam param, Model model, HttpServletRequest request) {

        param.set_queryString(request.getQueryString());
        SearchResult result = mallSearchService.search(param);
        model.addAttribute("result", result);
        return "list";
    }
    @GetMapping("/searchDemo")
    @ResponseBody
    public String searchDemo(SearchParam param, HttpServletRequest request) {
        param.set_queryString(request.getQueryString());
        SearchResult result = mallSearchService.search(param);
        return result.toString();
    }
}

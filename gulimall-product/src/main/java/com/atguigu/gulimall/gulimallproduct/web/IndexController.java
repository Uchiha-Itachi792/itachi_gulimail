package com.atguigu.gulimall.gulimallproduct.web;



import com.atguigu.common.to.product.Catalog2VO;
import com.atguigu.gulimall.gulimallproduct.entity.CategoryEntity;
import com.atguigu.gulimall.gulimallproduct.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author: wanzenghui
 * @Date: 2021/10/26 22:01
 * 首页页面跳转
 */
@Controller
public class IndexController {

    @Autowired
    CategoryService categoryService;

    @GetMapping({"/", "index.html"})
    public String indexPage(Model model) {

        // 查询所有1级分类
        List<CategoryEntity> categoryEntitys = categoryService.getLevel1Categorys();

        //
        model.addAttribute("categorys", categoryEntitys);


        // 解析器自动拼装classpath:/templates/  + index +  .html =》 classpath:/templates/index.html
        // classpath表示类路径，编译前是resources文件夹，编译后resources文件夹内的文件会统一存放至classes文件夹内
        //  public static final String DEFAULT_PREFIX = "classpath:/templates/";
        //  public static final String DEFAULT_SUFFIX = ".html";
        return "index";
    }


    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String, List<Catalog2VO>> getCatalogJson() {
        Map<String, List<Catalog2VO>> map = categoryService.getCatalogJsonWithSpringCache();
        return map;
    }


}
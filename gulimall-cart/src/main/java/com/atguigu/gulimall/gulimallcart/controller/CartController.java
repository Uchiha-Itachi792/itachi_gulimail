package com.atguigu.gulimall.gulimallcart.controller;

import com.atguigu.common.to.UserInfoTo;
import com.atguigu.common.vo.cart.CartItemVO;
import com.atguigu.common.vo.cart.CartVO;
import com.atguigu.gulimall.gulimallcart.interceptor.CartInterceptor;
import com.atguigu.gulimall.gulimallcart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Controller
public class CartController {

    @Autowired
    CartService cartService;

    @GetMapping("/cart.html")
    public String cartListPage(Model model) throws ExecutionException, InterruptedException {
        CartVO cartVO = cartService.getCart();
        model.addAttribute("cart", cartVO);
        return "cartList";
    }

    /**
     * 更改购物车商品选中状态
     */
    @GetMapping(value = "/checkItem")
    public String checkItem(@RequestParam(value = "skuId") Long skuId,
                            @RequestParam(value = "checked") Integer check) {
        cartService.checkItem(skuId, check);
        return "redirect:http://cart.gulimall.com/cart.html";
    }

    @GetMapping(value = "/addCartItem")
    public String addCartItem(@RequestParam("skuId") Long skuId,
                              @RequestParam("num") Integer num,
                              RedirectAttributes attributes) throws ExecutionException, InterruptedException {
        // 添加sku商品到购物车
        cartService.addToCart(skuId, num);
        attributes.addAttribute("skuId", skuId);// 会在url后面拼接参数
        // 请求重定向给addToCartSuccessPage.html，防刷
        return "redirect:http://cart.gulimall.com/addToCartSuccessPage.html";
    }



    /**
     * 商品添加购物车成功页（防刷）
     */
    @GetMapping(value = "/addToCartSuccessPage.html")
    public String addToCartSuccessPage(@RequestParam("skuId") Long skuId, Model model) {
        //重定向到成功页面。再次查询购物车数据即可
        CartItemVO cartItemVo = cartService.getCartItem(skuId);
        model.addAttribute("cartItem",cartItemVo);
        return "success";
    }

    /**
     * 改变商品数量
     */
    @GetMapping(value = "/countItem")
    public String countItem(@RequestParam(value = "skuId") Long skuId,
                            @RequestParam(value = "num") Integer num) {
        cartService.changeItemCount(skuId,num);
        return "redirect:http://cart.gulimall.com/cart.html";
    }

    /**
     * 删除商品信息
     */
    @GetMapping(value = "/deleteItem")
    public String deleteItem(@RequestParam("skuId") Integer skuId) {
        cartService.deleteIdCartInfo(skuId);
        return "redirect:http://cart.gulimall.com/cart.html";
    }

    /**
     * 获取当前用户的购物车所有商品项
     * 订单服务调用：【购物车列表页面点击确认订单时】
     * 从redis中获取所有选中的商品项
     * 并且要获取最新的商品价格信息，替换redis中的价格信息
     */
    @GetMapping(value = "/currentUserCartItems")
    @ResponseBody
    public List<CartItemVO> getCurrentCartItems() {
        List<CartItemVO> cartItemVoList = cartService.getUserCartItems();
        return cartItemVoList;
    }


}

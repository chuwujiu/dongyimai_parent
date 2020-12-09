package com.offcn.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.offcn.cart.service.CartService;
import com.offcn.common.utils.CookieUtil;
import com.offcn.entity.Result;
import com.offcn.group.Cart;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference(timeout = 6000)
    private CartService cartService;

    @RequestMapping("/findCartList")
    public List<Cart> findCartList(HttpServletRequest request ,HttpServletResponse response){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String cartList = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
        if (cartList==null||cartList.equals("")){
            cartList="[]";
        }
        List<Cart> cartList_cookie = JSON.parseArray(cartList,Cart.class);
        if (username.equals("anonymousUser")){
            return cartList_cookie;
        }else{
            List<Cart> cartList_redis = cartService.findCartListFromRedis(username);
            if (cartList_cookie.size()>0){
                cartList_redis = cartService.margeCartList(cartList_cookie, cartList_redis);
                CookieUtil.deleteCookie(request,response,"cartList");
                cartService.saveCartListToRedis(username,cartList_redis);
            }
            return cartList_redis;
        }

    }

    @RequestMapping("/addGoodsToCartList")
   // @CrossOrigin(origins = "http://localhost:9105")
    public Result addGoodsToCartList(HttpServletRequest request , HttpServletResponse response,Long itemId,Integer num){
        response.setHeader("Access-Control-Allow-Origin","http://localhost:9105");
        response.setHeader("Access-Control-Allow-Credentials","true");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            List<Cart> cartList = findCartList(request,response);
            cartList=cartService.addGoodsCartList(cartList,itemId,num);
            if (username.equals("anonymousUser")){
                CookieUtil.setCookie(request,response,"cartList",JSON.toJSONString(cartList),3600*24,"UTF-8");
            }else {
                cartService.saveCartListToRedis(username,cartList);
            }
            return new Result(true,"添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }
}

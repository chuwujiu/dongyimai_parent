package com.offcn.cart.service;

import com.offcn.group.Cart;

import java.util.List;

public interface CartService {
    public List<Cart> addGoodsCartList(List<Cart> cartList, Long itemId, Integer num);
    public List<Cart> findCartListFromRedis(String username);
    public void saveCartListToRedis(String username,List<Cart> cartList);
    public List<Cart> margeCartList(List<Cart> cartList_cookie,List<Cart> cartList_redis);
}

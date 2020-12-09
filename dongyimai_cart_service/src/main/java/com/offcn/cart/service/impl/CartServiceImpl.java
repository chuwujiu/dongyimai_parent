package com.offcn.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.offcn.cart.service.CartService;
import com.offcn.group.Cart;
import com.offcn.mapper.TbItemMapper;
import com.offcn.pojo.TbItem;
import com.offcn.pojo.TbOrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<Cart> margeCartList(List<Cart> cartList_cookie, List<Cart> cartList_redis) {
        if (cartList_cookie!=null){
            for (Cart cart : cartList_cookie) {
                for (TbOrderItem orderItem : cart.getOrderItemList()) {
                   cartList_redis = addGoodsCartList(cartList_redis, orderItem.getItemId(), orderItem.getNum());
                }
            }
        }
        return cartList_redis;
    }

    @Override
    public List<Cart> addGoodsCartList(List<Cart> cartList, Long itemId, Integer num) {
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        if (item==null){
            throw new RuntimeException("商品不存在");
        }
        if (!item.getStatus().equals("1")){
            throw new RuntimeException("商品状态无效");
        }
        String sellerId = item.getSellerId();
        if (cartList==null){
            cartList=new ArrayList<>();
        }
        Cart cart = searchCartBySellerId(cartList, sellerId);
        if (cart==null){
            cart = new Cart();
            cart.setSellerId(sellerId);
            TbOrderItem orderItem = createOrderItem(item, num);
            List orderItemList = new ArrayList();
            orderItemList.add(orderItem);
            cart.setOrderItemList(orderItemList);

            cartList.add(cart);

        }else {
            TbOrderItem tbOrderItem = searchOrderItemByItemId(cart.getOrderItemList(), itemId);
            if (tbOrderItem==null){
                tbOrderItem=createOrderItem(item,num);
                cart.getOrderItemList().add(tbOrderItem);
            }else {
                tbOrderItem.setNum(tbOrderItem.getNum()+num);
                tbOrderItem.setTotalFee(new BigDecimal(tbOrderItem.getNum()*tbOrderItem.getPrice().doubleValue()));
                if (tbOrderItem.getNum()<=0){
                    cart.getOrderItemList().remove(tbOrderItem);
                }
                if (cart.getOrderItemList().size()==0){
                    cartList.remove(cart);
                }
            }
        }
        return cartList;
    }

    @Override
    public List<Cart> findCartListFromRedis(String username) {
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
        if (cartList==null){
            cartList=new ArrayList();
        }
        return cartList;
    }



    @Override
    public void saveCartListToRedis(String username, List<Cart> cartList) {
        redisTemplate.boundHashOps("cartList").put(username,cartList);
    }

    private Cart searchCartBySellerId(List<Cart> cartList,String sellerId){
        if (cartList==null){
            return null;
        }
        for (Cart cart : cartList) {
            if (cart.getSellerId().equals(sellerId)){
                return cart;
            }
        }
        return null;
    }

    private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList,Long itemId){
        for (TbOrderItem tbOrderItem : orderItemList) {
            if (tbOrderItem.getItemId().longValue()==itemId.longValue()){
                return tbOrderItem;
            }
        }
        return null;
    }

    private TbOrderItem createOrderItem(TbItem item,Integer num){
        if (num<=0){
            throw new RuntimeException("数量非法");
        }
        TbOrderItem tbOrderItem = new TbOrderItem();
        tbOrderItem.setGoodsId(item.getGoodsId());
        tbOrderItem.setItemId(item.getId());
        tbOrderItem.setNum(num);
        tbOrderItem.setPrice(item.getPrice());
        tbOrderItem.setSellerId(item.getSellerId());
        tbOrderItem.setTitle(item.getTitle());
        tbOrderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*num));
        return tbOrderItem;
    }


}

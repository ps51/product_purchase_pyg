package com.pinyougou.cart.service;

import com.pinyougou.pojogroup.Cart;

import java.util.List;

/**
 * @program: pinyougou-parent
 * @description: 购物车服务接口
 * @author: Mr.Cherry
 * @create: 2019-11-14 20:56
 **/
public interface CartService {

    /**
     * 添加商品到购物车列表
     * @param list 商品集合
     * @param itemId SKU id
     * @param num 数量
     * @return 重新添加封装后的商品集合
     */
    public List<Cart> addGoodsToCartList(List<Cart> list,Long itemId,Integer num);

    /**
     * 从redis中取购物车
     * @param username 用户id
     * @return
     */
    public List<Cart> findCartListFromRedis(String username);

    /**
     * 将购物车列表存入redis
     * @param cartList
     * @param username
     * @return
     */
    public List<Cart> saveCartListToRedis(List<Cart> cartList,String username);


    /**
     * 合并购物车
     * @param CartList1
     * @param CartList2
     * @return
     */
    public List<Cart> mergeCartList(List<Cart> CartList1,List<Cart> CartList2);

}

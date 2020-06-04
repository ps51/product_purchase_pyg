package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojogroup.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: pinyougou-parent
 * @description: ${description}
 * @author: Mr.Cherry
 * @create: 2019-11-14 21:00
 **/
@Service
public class CartServiceImpl implements CartService {


    @Autowired
    private TbItemMapper itemMapper;

    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {

        //1.根据SKU id查询商品明细的SKU对象
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        if(item == null){
            //抛出一个运行时异常
            throw new RuntimeException("商品不存在 - ");
        }
        if(!item.getStatus().equals("1")){
            throw new RuntimeException("商品可能已经被下架 - ");
        }
        //2.根据SKU对象得到商家id
        String sellerId = item.getSellerId();
        //3.根据商家id在购物车列表中查询购物车对象 如果存在进行内容追加，反之重新新建一个购物车对象
        Cart cart = searchCartBySellerId(cartList, sellerId);
        if(cart == null){
            //4.如果购物车列表中不存在该商家的购物车
            //4.1创建一个新的购物车对象
            cart = new Cart();
            //商家id
            cart.setSellerId(sellerId);
            //商家名称
            cart.setSellerName(item.getSeller());
            //创建购物车明细列表
            List<TbOrderItem> orderItemList = new ArrayList<>();
            TbOrderItem orderItem = createOrderItem(item, num);
            orderItemList.add(orderItem);
            cart.setOrderItemList(orderItemList);
            //4.2将新的购物车对象添加到购物车列表中
            cartList.add(cart);
        }else{
            //5.如果购物车列表中存在该商家的购物车
            // 判断该商品是否在该购物车的明细列表中存在
            TbOrderItem orderItem = searchOrderItemByItemId(cart.getOrderItemList(), itemId);
            if(orderItem == null){
                //5.1如果不存在，创建新的购物车明细对象，并添加到该购物车的明细列表中
                orderItem = createOrderItem(item,num);
                cart.getOrderItemList().add(orderItem);
            }else{
                //5.2如果存在，则在原有的数量上添加数量，并且更新金额
                orderItem.setNum(orderItem.getNum()+num);
                orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue() * orderItem.getNum()));
                //当明细的数量小于等于0 移除此明细
                if(orderItem.getNum() <= 0){
                    cart.getOrderItemList().remove(orderItem);
                }
                //当购物车的明细数量为0 在购物车列表中移除此购物车
                if(cart.getOrderItemList().size() == 0){
                    cartList.remove(cart);
                }
            }
        }
        return cartList;
    }

    @Autowired
    private RedisTemplate redisTemplate;

    private final String redisKey = "CARTLIST";
    @Override
    public List<Cart> findCartListFromRedis(String username) {
        System.out.println("向redis取购物车 - ");
        List<Cart> cartList = JSON.parseArray((String) redisTemplate.boundHashOps(redisKey).get(username),Cart.class);
        if(cartList == null){
            cartList = new ArrayList<>();
        }
        return cartList;
    }

    @Override
    public List<Cart> saveCartListToRedis(List<Cart> cartList, String username) {
        System.out.println("向redis村存购物车 - ");
        redisTemplate.boundHashOps(redisKey).put(username, JSON.toJSONString(cartList));
        return null;
    }

    @Override
    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {
        for (Cart cart : cartList1) {
            for (TbOrderItem orderItem : cart.getOrderItemList()) {
                cartList2 = addGoodsToCartList(cartList2,orderItem.getItemId(),orderItem.getNum());
            }
        }
        return cartList2;
    }


    /**
     * 根据商家id在购物车列表中查询购物车对象
     * @param cartList 购物车列表
     * @param sellerId 商家id
     * @return
     */
    private Cart searchCartBySellerId(List<Cart> cartList,String sellerId){
        for (Cart cart : cartList) {
            if(cart.getSellerId().equals(sellerId)){
                return cart;
            }
        }
        return null;
    }


    /**
     * 根据商品id判断该商品是否在购物车明细列表中存在
     * @param orderItemList
     * @param itemId
     * @return
     */
    private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList,Long itemId){
        for (TbOrderItem orderItem : orderItemList) {
            if(orderItem.getItemId().equals(itemId)){
                return orderItem;
            }
        }
        return null;
    }

    /**
     * 创建购物车明细对象
     * @param item
     * @param num
     * @return
     */
    private TbOrderItem createOrderItem(TbItem item,Integer num){
        //创建新的购物车明细对象
        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        orderItem.setNum(num);
        orderItem.setPicPath(item.getImage());
        orderItem.setPrice(item.getPrice());
        orderItem.setSellerId(item.getSellerId());
        orderItem.setTitle(item.getTitle());
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*num));

        return orderItem;
    }




}

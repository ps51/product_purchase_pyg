package com.pinyougou.pojogroup;

import com.pinyougou.pojo.TbOrderItem;

import java.io.Serializable;
import java.util.List;

/**
 * @program: pinyougou-parent
 * @description: 购物车对象
 * @author: Mr.Cherry
 * @create: 2019-11-14 20:43
 **/
public class Cart implements Serializable {
    /**
     * 商家id
     */
    private String sellerId;
    /**
     * 商家名称
     */
    private String sellerName;
    /**
     * 购物车明细集合
     */
    private List<TbOrderItem> orderItemList;

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public List<TbOrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<TbOrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }
}

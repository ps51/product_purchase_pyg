package com.pinyougou.page.service;

/**
 * @program: pinyougou-parent
 * @description: 静态页面生成接口
 * @author: Mr.Cherry
 * @create: 2019-11-12 18:36
 **/
public interface ItemPageService {

    /**
     * 生成商品详细页
     * @param goodsId SPU id
     * @return
     */
    public boolean genItemHtml(Long goodsId);


    /**
     * 删除商品详情页
     * @param goodsIds
     * @return
     */
    public boolean deleteItemHtml(Long [] goodsIds);
}

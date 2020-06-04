package com.pinyougou.pay.service;

import java.util.Map;

/**
 * @program: pinyougou-parent
 * @description: 微信支付服务
 * @author: Mr.Cherry
 * @create: 2019-11-16 19:44
 **/
public interface WeixinPayService {

    /**
     * 生成二维码
     * @param out_trade_no 商品订单号
     * @param total_fee 金额 (分)
     * @return
     */
    public Map createNative(String out_trade_no,String total_fee);
}

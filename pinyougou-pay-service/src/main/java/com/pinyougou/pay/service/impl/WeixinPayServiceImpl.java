package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.pay.service.WeixinPayService;
import org.springframework.beans.factory.annotation.Value;
import util.HttpClient;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: pinyougou-parent
 * @description: ${description}
 * @author: Mr.Cherry
 * @create: 2019-11-16 19:48
 **/
@Service
public class WeixinPayServiceImpl implements WeixinPayService {

/*

    @Value("${appid}")
    private String appid;

    @Value("${partner}")
    private String partner;

    @Value("${partnerkey}")
    private String partnerkey;

    @Value("${notifyurl}")
    private String notifyurl;

    @Override
    public Map createNative(String out_trade_no, String total_fee) {
        //1.参数封装
        Map map = new HashMap();
        //公众账号ID
        map.put("appid",appid);
        //商户号
        map.put("mch_id",partner);
        //随机字符串
        map.put("nonce_str", WXPayUtil.generateNonceStr());
        //商品描述  (网站描述)
        map.put("body","品优购");
        //商户订单号
        map.put("out_trade_no",out_trade_no);
        //标价金额
        map.put("total_fee",total_fee);
        //终端IP
        map.put("spbill_create_ip","127.0.0.1");
        //通知地址
        map.put("notify_url",notifyurl);
        //交易类型
        map.put("trade_type","NATIVE");
        try {
            System.out.println("签名："+partnerkey);
            String paramXml = WXPayUtil.generateSignedXml(map, partnerkey);
            System.out.println("请求参数："+paramXml);
            //2.发送请求
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            httpClient.setHttps(true);
            httpClient.setXmlParam(paramXml);
            httpClient.post();
            //3.获取结果
            String xmlResult = httpClient.getContent();
            Map<String, String> mapResult = WXPayUtil.xmlToMap(xmlResult);
            System.out.println("微信返回的结果："+mapResult);
            Map map1 = new HashMap();
            //生成支付二维码的链接
            map1.put("code_url",map.get("code_url"));
            map1.put("out_trade_no",out_trade_no);
            map1.put("total_fee",total_fee);
            return map1;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

*/

    @Value("${appid}")
    private String appid;

    @Value("${partner}")
    private String partner;

    @Value("${partnerkey}")
    private String partnerkey;

    @Override
    public Map createNative(String out_trade_no, String total_fee) {
        //1.参数封装
        Map param=new HashMap();
        param.put("appid", appid);//公众账号ID
        param.put("mch_id", partner);//商户
        param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        param.put("body", "品优购");
        param.put("out_trade_no", out_trade_no);//交易订单号
        param.put("total_fee", total_fee);//金额（分）
        param.put("spbill_create_ip", "127.0.0.1");
        param.put("notify_url", "http://www.itcast.cn");
        param.put("trade_type", "NATIVE");//交易类型

        try {
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            System.out.println("请求的参数："+xmlParam);

            //2.发送请求
            HttpClient httpClient=new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            httpClient.setHttps(true);
            httpClient.setXmlParam(xmlParam);
            httpClient.post();

            //3.获取结果
            String xmlResult = httpClient.getContent();

            Map<String, String> mapResult = WXPayUtil.xmlToMap(xmlResult);
            System.out.println("微信返回结果"+mapResult);
            Map map=new HashMap<>();
            map.put("code_url", mapResult.get("code_url"));//生成支付二维码的链接
            map.put("out_trade_no", out_trade_no);
            map.put("total_fee", total_fee);

            return map;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new HashMap();
        }

    }


}

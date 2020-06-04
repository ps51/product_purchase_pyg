package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.entity.pojo.Result;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.pojogroup.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.CookieUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @program: pinyougou-parent
 * @description: 购物车控制层
 * @author: Mr.Cherry
 * @create: 2019-11-15 09:17
 **/
@RestController
@RequestMapping("/cart")
public class CartController {


    @Autowired
    HttpServletRequest request;
    @Autowired
    HttpServletResponse response;

    private final String cookieName = "CARTLIST";

    @Reference(timeout = 10000)
    private CartService cartService;

    /**
     * 添加商品到购物车中
     * @param itemId 商品id SKU id
     * @param num 数量
     * @return
     */
    @RequestMapping("/addGoodsToCartList")
    //allowCredentials 默认为 true
    @CrossOrigin(origins = "http://localhost:8004",allowCredentials = "true")
    public Result addGoodsToCartList(Long itemId,Integer num){

        /**加头信息 支持跨域   可以使用SpringMVC的 @CrossOrigin 代替
            //设置可以跨越请求的域名/地址 (当此方法不需要操作cookie时，这一句话就可以)
            response.setHeader("Access-Control-Allow-Origin","http://localhost:8004");
            //如果操作cookie 需要加上这句话 (此时跨域请求的域名必须时确定的地址不能是 * )
            response.setHeader("Access-Control-Allow-Credentials", "true");
         */
        //当前登录人账户
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("当前登录人账户："+username);
        try {
            //1.提取购物车
            List<Cart> cartList = findCartList();
            //2.调用服务方法操作购物车
            cartList = cartService.addGoodsToCartList(cartList, itemId, num);
            if(username.equals("anonymousUser")){
                System.out.println("添加到cookie中 - ");
                //如果未登录
                //3.将新的购物车存入cookie
                String cartListStr = JSON.toJSONString(cartList);
                CookieUtil.setCookie(request,response,cookieName,cartListStr,3600 * 24,"UTF-8");
            }else {
                //如果已登录
                System.out.println("添加到redis中 - ");
                cartService.saveCartListToRedis(cartList,username);
            }
            return new Result(true,"存入购物车成功 - ");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"存入购物车失败 - ");
        }
    }

    @RequestMapping("/findCartList")
    public List<Cart> findCartList(){
        //当前登录人账户
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("当前登录人账户："+username);
        String cartlistString = CookieUtil.getCookieValue(request, cookieName, "UTF-8");
        //如果cookie中没有数据则赋予一个[] 空的集合
        if(cartlistString == null || cartlistString.equals("")){
            cartlistString = "[]";
        }
        //获得cookie中的购物车
        List<Cart> cartList_cookie = JSON.parseArray(cartlistString, Cart.class);
        if(username.equals("anonymousUser")){
            //如果未登录
            //从cookie中提取购物车
            System.out.println("从cookie中提取购物车 - ");
            return cartList_cookie;
        }else {
            //如果已登录
            System.out.println("从redis中提取购物车 - ");
            //合并购物车
            //获取redis中的购物车
            List<Cart> cartList_redis = cartService.findCartListFromRedis(username);
            //判断cookie中是否有数据，没有数据则跳过合并操作
            if(cartList_cookie.size() > 0){
                //合并后的购物车
                List<Cart> cartList = cartService.mergeCartList(cartList_cookie, cartList_redis);
                //将合并后的购物车重新存入redis
                cartService.saveCartListToRedis(cartList,username);
                //清除cookie中的购物车
                CookieUtil.deleteCookie(request,response,cookieName);
                System.out.println("执行合并购物车 - ");
                return cartList;
            }
            return cartList_redis;
        }
    }
}

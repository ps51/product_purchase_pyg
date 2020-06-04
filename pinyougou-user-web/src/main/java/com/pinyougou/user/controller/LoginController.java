package com.pinyougou.user.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: pinyougou-parent
 * @description: 返回用户名称
 * @author: Mr.Cherry
 * @create: 2019-11-14 20:01
 **/
@RestController
@RequestMapping("/login")
public class LoginController {

    @RequestMapping("/name")
    public Map showName(){
        Map map = new HashMap();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        map.put("loginName",username);

        return map;
    }
}

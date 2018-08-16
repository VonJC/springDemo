package com.fjc.springmvc.controller;

import com.fjc.springmvc.annotation.Controller;
import com.fjc.springmvc.annotation.Qualifier;
import com.fjc.springmvc.annotation.RequestMapping;
import com.fjc.springmvc.service.IUserService;

@Controller("userController")
public class UserController {

    @Qualifier("userServiceImpl")
    private IUserService userServiceImpl;

    @RequestMapping("/getUser")
    public String getUser(){
        System.out.println("请求user");
        String id = "8888";
        String name = userServiceImpl.getUser(id);
        System.out.println("name:" + name);
        return name;
    }
}

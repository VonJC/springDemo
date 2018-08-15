package com.fjc.springmvc.controller;

import com.fjc.springmvc.annotation.Controller;
import com.fjc.springmvc.annotation.Qualifier;
import com.fjc.springmvc.annotation.RequestMapping;
import com.fjc.springmvc.service.IUserService;

@Controller("user")
public class UserController {

    @Qualifier("userServiceImpl")
    private IUserService userServiceImpl;

    @RequestMapping("getUser")
    public String getUser(){
        return "fjc";
    }
}

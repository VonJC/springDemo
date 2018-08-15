package com.fjc.springmvc.controller;

import com.fjc.springmvc.annotation.Controller;
import com.fjc.springmvc.annotation.RequestMapping;

@Controller("")
public class UserController {

    @RequestMapping("getUser")
    public String getUser(){
        return "fjc";
    }
}

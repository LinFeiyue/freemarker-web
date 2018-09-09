package com.pache.framework.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 登陆控制器
 */
@Controller
public class LoginController {

    @RequestMapping("/login")
    public String loadLoginPage(){

        return "/login";
    }
}

package com.pache.framework.web.controller.module;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * 主页controller
 */
@Controller
public class indexController {

    @RequestMapping(value = "/index/index-view",method = RequestMethod.GET)
    public String loadPage(HttpServletRequest request, ModelMap modelMap){
    	modelMap.put("user", "llj");
        return "/module/index/index-view";
    }

}

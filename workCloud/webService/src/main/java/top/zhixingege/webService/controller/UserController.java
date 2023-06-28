package top.zhixingege.webService.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import top.zhixingege.common.entity.UserEntity;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/webLogin")
public class UserController {
    @Value("${my.gateway.ip}")
    private String ip;

    @Value("${my.gateway.port}")
    private String port;
    @Value("${server.port}")
    private String applicationPort;

    @PostMapping("/register")
    @ResponseBody
    public String register(HttpServletRequest request, @RequestBody UserEntity registerRequest){
        System.out.println(registerRequest.toString());
        return "register";
    }
    @PostMapping("/emailLogin")
    @ResponseBody
    public String emailLogin(HttpServletRequest request, @RequestBody UserEntity registerRequest){

        return "register";
    }
}

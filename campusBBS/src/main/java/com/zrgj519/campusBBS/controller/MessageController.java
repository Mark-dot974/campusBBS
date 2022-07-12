package com.zrgj519.campusBBS.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/message")
public class MessageController {

    @RequestMapping(path="/systemMessage")
    public String systemMessage(){
        return "/site/message_center_system";
    }
}

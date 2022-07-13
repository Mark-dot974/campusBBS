package com.zrgj519.campusBBS.controller;

import com.alibaba.fastjson.JSONObject;
import com.zrgj519.campusBBS.entity.Event.Event;
import com.zrgj519.campusBBS.entity.Group;
import com.zrgj519.campusBBS.entity.Message;
import com.zrgj519.campusBBS.entity.Page;
import com.zrgj519.campusBBS.entity.User;
import com.zrgj519.campusBBS.service.GroupService;
import com.zrgj519.campusBBS.service.MessageService;
import com.zrgj519.campusBBS.service.UserService;
import com.zrgj519.campusBBS.util.CampusBBSConstant;
import com.zrgj519.campusBBS.util.UserContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageService messageService;
    @Autowired
    private UserContainer userContainer;
    @Autowired
    private UserService userService;
    @Autowired
    private GroupService groupService;

    @RequestMapping(path="/systemMessage")
    public String systemMessage(Model model, Page page){
        page.setRows(messageService.findTopicNoticeCount(userContainer.getUser().getId(), null));
        page.setPath("/message/systemMessage");
        // 查询所有系统通知
        List<Message> systemMessages = messageService.findTopicNotices(userContainer.getUser().getId(), null, page.getOffset(), page.getLimit());
        List<Map<String,Object>> messages = new ArrayList<>();
        for (Message systemMessage : systemMessages) {
            Map<String,Object> map = new HashMap<>();
            String content = systemMessage.getContent();
            Event event = JSONObject.parseObject(content,Event.class);
            // 申请者
            int userId = event.getUserId();
            User user = userService.findUserById(userId);
            map.put("user",user);

            Group group = groupService.getGroupById(event.getEntityId());
            Map<String, Object> data = event.getData();
            Object status = data.get("status");
            map.put("status",status);
            map.put("group",group);
            map.put("message",systemMessage);
            messages.add(map);
        }
        model.addAttribute("messages",messages);
        return "/site/message_center_system";
    }
}

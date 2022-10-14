package com.zrgj519.campusBBS.controller;

import com.alibaba.fastjson.JSONObject;
import com.zrgj519.campusBBS.entity.*;
import com.zrgj519.campusBBS.entity.Event.Event;
import com.zrgj519.campusBBS.service.GroupService;
import com.zrgj519.campusBBS.service.MessageService;
import com.zrgj519.campusBBS.service.PostService;
import com.zrgj519.campusBBS.service.UserService;
import com.zrgj519.campusBBS.util.CampusBBSConstant;
import com.zrgj519.campusBBS.util.UserContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
@RequestMapping("/message")
public class MessageController {
    @Value("1")
    private int a;

    @Autowired
    private MessageService messageService;
    @Autowired
    private UserContainer userContainer;
    @Autowired
    private UserService userService;
    @Autowired
    private GroupService groupService;
    @Autowired
    private PostService postService;

    @RequestMapping(path="/systemMessage")
    public String systemMessage(Model model, Page page,@RequestParam(value = "topic",defaultValue = "group") String topic){
        page.setRows(messageService.findTopicNoticeCount(userContainer.getUser().getId(), topic));
        page.setPath("/message/systemMessage?topic="+topic);

        // 将消息设为已读
        List<Message> topicNotices = messageService.findTopicNotices(userContainer.getUser().getId(), topic, page.getOffset(), page.getLimit());
        List<Integer> ids = this.getIds(topicNotices,topic);
        if(!ids.isEmpty() && ids.size()!=0) {
            messageService.readMessage(ids);
        }
        // 查询所有系统通知
        List<Message> systemMessages = messageService.findTopicNotices(userContainer.getUser().getId(), topic, page.getOffset(), page.getLimit());
        List<Map<String,Object>> messages = new ArrayList<>();
        for (Message systemMessage : systemMessages) {
            Map<String,Object> map = new HashMap<>();
            String content = systemMessage.getContent();
            Event event = JSONObject.parseObject(content,Event.class);
            // 申请者
            int userId = event.getUserId();
            User user = userService.findUserById(userId);
            map.put("user",user);

            int entityId = event.getEntityId();

            if(topic .equals("like")  || topic.equals("comment")){
                Post post;
                post = postService.find(entityId);
                map.put("post",post);
            }else{
                Group group = groupService.getGroupById(entityId);
                map.put("group",group);
            }
            map.put("message",systemMessage);
            messages.add(map);
        }
        int likeUnreadCount = messageService.findTopicNoticeUnreadCount(userContainer.getUser().getId(), "like");
        model.addAttribute("likeUnreadCount",likeUnreadCount);
        int commentUnreadCount = messageService.findTopicNoticeUnreadCount(userContainer.getUser().getId(), "comment");
        model.addAttribute("commentUnreadCount",commentUnreadCount);
        int groupUnreadCount = messageService.findTopicNoticeUnreadCount(userContainer.getUser().getId(), "group");
        groupUnreadCount = groupUnreadCount - likeUnreadCount - commentUnreadCount;
        model.addAttribute("groupUnreadCount",groupUnreadCount);

        model.addAttribute("messages",messages);
        if(topic .equals("like")){
            return "/site/message_center_like";
        }else if(topic.equals("comment")){
            return "/site/message_center_reply";
        }
        return "/site/message_center_system";
    }

    public List<Integer> getIds(List<Message> letterList,String topic){
        List<Integer> ids = new ArrayList<>();
        for (Message message : letterList) {
            if(topic.equals("group")&&(message.getConversationId().equals("like")||message.getConversationId().equals("comment"))) continue;
            int id = message.getId();
            // 只用更新接收者是自己、状态为未读的消息，减少更新操作的数量，提高效率
            User user = userContainer.getUser();
            if(message.getToId() == user.getId() && message.getStatus() == 0)
            {
                ids.add(id);
            }
        }
        return ids;
    }
}
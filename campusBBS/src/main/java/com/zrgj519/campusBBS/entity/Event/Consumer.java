package com.zrgj519.campusBBS.entity.Event;

import com.alibaba.fastjson.JSONObject;
import com.zrgj519.campusBBS.dao.MessageMapper;
import com.zrgj519.campusBBS.entity.Message;
import com.zrgj519.campusBBS.entity.User;
import com.zrgj519.campusBBS.service.GroupService;
import com.zrgj519.campusBBS.service.MessageService;
import com.zrgj519.campusBBS.service.UserService;
import com.zrgj519.campusBBS.util.CampusBBSConstant;
import com.zrgj519.campusBBS.util.UserContainer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class Consumer {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private UserContainer userContainer;

    // 申请加入 申请发给谁-申请者是谁
    @KafkaListener(topics = {CampusBBSConstant.TOPIC_APPLY})
    public void handleAddEntityEvent(ConsumerRecord record){
        Event event = JSONObject.parseObject(record.value().toString(),Event.class);
        Message message = new Message();
        message.setFromId(1);
        message.setToId(event.getEntityUserId());
        message.setCreateTime(new Date());
        message.setConversationId(CampusBBSConstant.TOPIC_APPLY);
        message.setStatus(0);

        // 构造内容，由前端拼接
        Map<String,Object> content = new HashMap<>();
        content.put("userId",event.getUserId());
        content.put("entityId",event.getEntityId());
        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);
    }

    @KafkaListener(topics = {CampusBBSConstant.TOPIC_ACCEPT, CampusBBSConstant.TOPIC_DENIED})
    public void handleOperation(ConsumerRecord record) throws IllegalAccessException {
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        // 查看是否具有权限
        if (groupService.isGroupLeader(event.getEntityId(), userContainer.getUser().getUsername())) {
            // 同意加入
            if (event.getTopic().equals(CampusBBSConstant.TOPIC_ACCEPT)) {
                User user = userService.findUserById(event.getEntityUserId());
                groupService.addGroupMember(user.getUsername(), event.getEntityId());
            }

            Message message = new Message();
            // 申请者id
            message.setToId(event.getEntityUserId());
            message.setFromId(1);
            message.setStatus(0);
            message.setCreateTime(new Date());
            message.setConversationId(event.getTopic());
            Map<String, Object> content = new HashMap<>();
            content.put("entityId", event.getEntityId());
            message.setContent(JSONObject.toJSONString(content));
            messageService.addMessage(message);
        }else{
            throw new IllegalAccessException("非圈主，权限不足");
        }
    }

    @KafkaListener(topics = {CampusBBSConstant.TOPIC_COMMENT,CampusBBSConstant.TOPIC_LIKE})
    public void handleCommentMessage(ConsumerRecord record){
        if(record == null ||record.value() == null){
            return;
        }
        Event event = JSONObject.parseObject(record.value().toString(),Event.class);
        if (event == null){
            return;
        }
        // 发送通知
        Message message = new Message();
        message.setFromId(CampusBBSConstant.SYSTEM_USER_ID);
        message.setConversationId(event.getTopic());
        message.setToId(event.getEntityUserId());
        message.setCreateTime(new Date());

        // 将构造系统消息需要的信息放到Map中，前端进行拼接
        // 格式：用户xxx点赞了你的xxx....
        Map<String,Object> content = new HashMap<>();
        content.put("userId",event.getUserId());
        content.put("entityType",event.getEntityType());
        content.put("entityId",event.getEntityId());
        if(!event.getData().isEmpty()){
            for (Map.Entry<String, Object> entry: event.getData().entrySet()
            ){
                if(entry.getKey().equals("postId"))
                {
                    content.put("entityId",entry.getValue());
                }
            }
        }
        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);
    }
}

package com.zrgj519.campusBBS.entity.Event;

import com.alibaba.fastjson.JSONObject;
import com.zrgj519.campusBBS.dao.MessageMapper;
import com.zrgj519.campusBBS.entity.Message;
import com.zrgj519.campusBBS.service.MessageService;
import com.zrgj519.campusBBS.util.CampusBBSConstant;
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

    // 申请加入
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
}

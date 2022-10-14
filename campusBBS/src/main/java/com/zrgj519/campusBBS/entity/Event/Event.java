package com.zrgj519.campusBBS.entity.Event;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
// 考虑属性时，可以想象用户触发该事件时可以携带那些数据
public class Event {
    // 点赞？评论？关注？
    private String topic;
    // 触发当前事件的用户id
    private int userId;
    // 事件对应的实体类型
    private int entityType;
    // 事件对应实体id
    private int entityId;
    // 发布实体的用户id
    private int entityUserId;
    // 存放多余可能需要存放的数据。冗余字段，用于匹配未来可能不确定的需求。
    // 例如在点赞事件中，之前的字段是不够的，需要使用一个字段来存放当前实体属于那个帖子（discussPostID），好做跳转。
    private Map<String,Object> data = new HashMap<>();
    public String getTopic() {
        return topic;
    }

    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public Event setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityUserId() {
        return entityUserId;
    }

    public Event setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Event setData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

}

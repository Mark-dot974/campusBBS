package com.zrgj519.campusBBS.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Comment {
    private int id;
    private int userId;
    // entity----评论的对象是什么
    private int entityType;
    private int entityId;
    // 当评论的对象是评论时，评论对象的用户的id
    private int targetId;
    private String content;
    private int status;
    private Date createTime;
}

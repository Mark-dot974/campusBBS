package com.zrgj519.campusBBS.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Message {
    private int id;
    private int fromId;
    private int toId;
    // 一个对话有两个对象，构造时默认小的在前,因为还可能是like、comment等，故用String类型
    private String conversationId;
    private String content;
    private int status;
    private Date createTime;
}

package com.zrgj519.campusBBS.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Data
public class Post {
    @Id
    private int id;

    private int userId;

    private String title;

    private String content;

    private String tag;

    private int type;

    private int status;

    private Date createTime;

    private int commentCount;

    private double score;

    private int cid;
}

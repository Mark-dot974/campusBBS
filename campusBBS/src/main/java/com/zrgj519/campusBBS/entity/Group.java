package com.zrgj519.campusBBS.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Data
public class Group {
    @Id
    private int gid;

    private String groupName;

    private String members;

    private int membersCount;

    private Date createTime;

    private String tag;

    private String files;

    private String groupLeader;

    private String groupIntro;

    private String groupHeader;
}

package com.zrgj519.campusBBS.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Data
public class GroupFile {
    @Id
    private int fid;

    private String fileName;

    private Date createTime;

    private int downloadTimes;

    private int gid;

    private String url;
}
package com.zrgj519.campusBBS.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
public class Tag {
    @Id
    private int id;

    private String tagName;

    private String postId;

    private int postCount;

    private List<Post> posts;
}

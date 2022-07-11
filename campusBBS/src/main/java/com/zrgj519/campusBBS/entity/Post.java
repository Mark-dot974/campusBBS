package com.zrgj519.campusBBS.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

@Data
@Document(indexName = "post")
public class Post {
    @Id
    private int id;

    @Field(type = FieldType.Integer)
    private int userId;

    @Field(type = FieldType.Text , analyzer = "ik_max_word" , searchAnalyzer = "ik_smart")
    private String title;

    @Field(type = FieldType.Text , analyzer = "ik_max_word" , searchAnalyzer = "ik_smart")
    private String content;

    @Field(type = FieldType.Text, searchAnalyzer = "ik_smart")
    private String tag;

    @Field(type = FieldType.Integer)
    private int type;

    @Field(type = FieldType.Integer)
    private int status;

    @Field(type = FieldType.Date)
    private Date createTime;

    @Field(type = FieldType.Integer)
    private int commentCount;

    @Field(type = FieldType.Double )
    private double score;

    @Field(type = FieldType.Integer)
    private int cid;
}

package com.zrgj519.campusBBS.dao;

import com.zrgj519.campusBBS.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {
    /**
     * 查找某个类型的某条内容的评论，并且将评论进行分页展示
     * @param entityType        评论对象的类型
     * @param entityId          评论对象的id
     * @param offset            查询的起始位置
     * @param limit             查询多少条
     * @return
     */
    List<Comment> selectCommentByEntity(int entityType , int entityId , int offset , int limit);

    /**
     *  查找某个类型的的某条内容下评论的数量
     * @param entityType
     * @param entityId
     * @return
     */
    int selectCountByEntity(int entityType , int entityId);

    /**
     * 插入评论
     * @param comment
     * @return
     */
    int insertComment(Comment comment);

    Comment selectCommentById(int entityId);
}

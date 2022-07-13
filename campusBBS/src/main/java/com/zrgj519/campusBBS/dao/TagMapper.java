package com.zrgj519.campusBBS.dao;

import com.zrgj519.campusBBS.entity.Group;
import com.zrgj519.campusBBS.entity.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TagMapper {
    // 判断当前表中是否创建了对应的tag    0--无   1--有
    int tagCount(String tagName);

    int insertTag(@Param("tagName") String tagName, @Param("postId") String postId,@Param("tagPostCount")int tagPostCount);

    // 查询指定tag的postId
    String selectPostsByTag(String tagName);

    int updateTagPost(@Param("tagName") String tagName, @Param("postId") String postId,@Param("tagPostCount")int tagPostCount);

    // 返回根据postCount排序的前limit条数据
    List<Tag> selectHotTags(int limit);

    int deleteTag(Integer id);

    Tag find(Integer id);

    void updateTag(Tag tag);

    List<Tag> findTag(Integer id,String tagName,String postId,Integer offset,Integer limit);

    int selectCountOfTag(Integer id,String tagName,String postId);
}

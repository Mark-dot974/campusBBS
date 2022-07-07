package com.zrgj519.campusBBS.dao;

import com.zrgj519.campusBBS.entity.Post;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface PostMapper {

    List<Post> selectAllPosts();
    /**
     * 根据板块id查找数据
     * @param cid       0 --- 推荐
     * @param offset    帖子的起始位置
     * @param limit     要查多少条
     * @return          改页所有帖子
     */
    List<Post> selectByCid(int cid, int offset, int limit);

    /**
     * 查询板块下所有帖子的数量
     * @param cid
     * @return
     */
    int selectPostsCount(int cid);

    int insertPost(Post post);

    Post selectPostById(int id);

    int selectAllPostsCount();
}

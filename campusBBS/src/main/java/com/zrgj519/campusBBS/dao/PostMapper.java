package com.zrgj519.campusBBS.dao;

import com.sun.org.apache.xpath.internal.operations.Bool;
import com.zrgj519.campusBBS.entity.Post;
import com.zrgj519.campusBBS.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

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

    List<Post> showPost();

    /**
    * 删除帖子
    * */
    int deletePost(int id);

    Post find(int id);

    void updatePost(Post post);

    List<Post> findPost(Integer id,String title,String tag,Integer offset,Integer limit);

    int selectCountOfPost(Integer id,String title,String tag);

}

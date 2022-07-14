package com.zrgj519.campusBBS.service;

import com.zrgj519.campusBBS.dao.PostMapper;
import com.zrgj519.campusBBS.dao.TagMapper;
import com.zrgj519.campusBBS.entity.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class PostService {
    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private TagService tagService;

    @Autowired
    private ElasticsearchService elasticsearchService;

    public List<Post> getAllPosts(){
        return postMapper.selectAllPosts();
    }

    public int getAllPostsCount(){
        return postMapper.selectAllPostsCount();
    }

    // 除了要添加帖子外，还要在数据库创建对应的tag
    public int addPost(Post post){
        if(post == null){
            throw new IllegalArgumentException("参数不能为空");
        }
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));
        int result = postMapper.insertPost(post);
        int postId = post.getId();
        String tagArr = post.getTag();
        String[] tags = tagArr.split(",");
        // 将帖子添加到对应的tag中
        // 一个tag的PostId在数据库中只能有255长度，后期使用redis优化
        for (String tag : tags) {
            tagService.addPostToTag(tag,postId);
        }
        return result;
    }


    public List<Post> getPostsByCid(int cid,int offset,int limit){
        return postMapper.selectByCid(cid,offset,limit);
    }

    public int getPostsCountByCid(int cid){
        return postMapper.selectPostsCount(cid);
    }

    public int getPostsCount(Integer id,String title,String tag){
        return postMapper.selectCountOfPost(id,title,tag);
    }


    public Post getPostById(int id){
        return postMapper.selectPostById(id);
    }

    public Page<Post> findPostsByUserInterest(String userInterest, int offset, int limit) {
        return elasticsearchService.searchDiscussPostByTag(userInterest,offset,limit);
    }

    public List<Post> showPost(){
        List<Post> posts = postMapper.showPost();
        return posts;
    }

    public int deletePost(int id) {
        return postMapper.deletePost(id);
    }

    public void updatePost(Post post){ postMapper.updatePost(post); }

    public Post find(int id){
        return postMapper.find(id);
    }

    public void updateCommentCount(int entityId, int count) {
        postMapper.updateCommentCount(entityId,count);
    }

    public List<Post> selectPersonalPost(int userId,Integer offset,Integer limit){
        return postMapper.selectPersonalPost(userId,offset,limit);
    }

    public List<Post> findPost(Integer id,String title,String tag,Integer offset,Integer limit){
        return postMapper.findPost(id,title,tag,offset,limit);
    }


    public int selectCountOfPersonalPost(Integer userId){
        return postMapper.selectCountOfPersonalPost(userId);
    }
}

package com.zrgj519.campusBBS.service;

import com.zrgj519.campusBBS.dao.PostMapper;
import com.zrgj519.campusBBS.entity.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class PostService {
    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserService userService;

    public List<Post> getAllPosts(){
        return postMapper.selectAllPosts();
    }

    public int getAllPostsCount(){
        return postMapper.selectAllPostsCount();
    }

    public int addPost(Post post){
        if(post == null){
            throw new IllegalArgumentException("参数不能为空");
        }
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));
        return postMapper.insertPost(post);
    }

    public List<Post> getPostsByCid(int cid,int offset,int limit){
        return postMapper.selectByCid(cid,offset,limit);
    }

    public int getPostsCountByCid(int cid){
        return postMapper.selectPostsCount(cid);
    }

    public Post getPostById(int id){
        return postMapper.selectPostById(id);
    }
}

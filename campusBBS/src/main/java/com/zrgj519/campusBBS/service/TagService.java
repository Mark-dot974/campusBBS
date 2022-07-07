package com.zrgj519.campusBBS.service;

import com.zrgj519.campusBBS.dao.TagMapper;
import com.zrgj519.campusBBS.entity.Post;
import com.zrgj519.campusBBS.entity.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TagService {
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private PostService postService;

    // 将postId添加到对应的tag中
    public void addPostToTag(String tag,int postId){
        int tagCount = tagMapper.tagCount(tag);
        if(tagCount == 0) {
            tagMapper.insertTag(tag,String.valueOf(postId),1);
        }
        else {
            // 先获取到原本tag下的postId
            String tagPost = tagMapper.selectPostsByTag(tag);
            StringBuilder postIds = new StringBuilder(tagPost);
            postIds.append(","+postId);
            int tagPostCount = getTagPostCount(tag);
            tagMapper.updateTagPost(tag,postIds.toString(),tagPostCount+1);
        }
    }

    // 统计tag下有多少个帖子
    public int getTagPostCount(String tag){
        String s = tagMapper.selectPostsByTag(tag);
        String[] posts = s.split(",");
        return posts.length;
    }

    public List<Tag> getHotTags(int limit){
        return tagMapper.selectHotTags(limit);
    }

    public List<Post> getPostsByTag(String tag){
        String postsIds = tagMapper.selectPostsByTag(tag);
        String[] postsId = postsIds.split(",");
        List<Post> posts = new ArrayList<>();
        for (String postId : postsId) {
            Post post = postService.getPostById(Integer.valueOf(postId));
            posts.add(post);
        }
        return posts;
    }
}

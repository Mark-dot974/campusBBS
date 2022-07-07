package com.zrgj519.campusBBS.controller;

import com.zrgj519.campusBBS.entity.Page;
import com.zrgj519.campusBBS.entity.Post;
import com.zrgj519.campusBBS.entity.User;
import com.zrgj519.campusBBS.service.PostService;
import com.zrgj519.campusBBS.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class IndexController {
    @Autowired
    private PostService postService;
    @Autowired
    private UserService userService;

    @RequestMapping(path="/index",method = RequestMethod.GET)
    public String getPosts(Model model, Page page){
        page.setRows(postService.getAllPostsCount());
        page.setPath("/index");

        List<Post> allPosts = postService.getAllPosts();
        List<Map<String,Object>> postsInfo = new ArrayList<>();
        // 封装用户信息
        for (Post post : allPosts) {
            Map<String,Object> postInfo = new HashMap<>();
            User userById = userService.findUserById(post.getUserId());
            postInfo.put("publisher",userById);
            postInfo.put("post",post);
            String tag = post.getTag();
            if(tag!=null){
                String[] split = tag.split(",");
                postInfo.put("tags",tag);
            }
            postsInfo.add(postInfo);
        }
        model.addAttribute("postsInfo",postsInfo);
        return "/site/index";
    }

    @RequestMapping(path="/header")
    public String getHeader(){
        return "/site/header";
    }

    @RequestMapping(path="/footer")
    public String getFooter(){
        return "/site/footer";
    }

    @RequestMapping(path="/aside")
    public String getAside(){
        return "/site/aside";
    }

    @RequestMapping("/manage")
    public String manage(){
        return "/site/post_management";
    }
}

package com.zrgj519.campusBBS.controller;

import com.zrgj519.campusBBS.entity.Page;
import com.zrgj519.campusBBS.entity.Post;
import com.zrgj519.campusBBS.entity.Tag;
import com.zrgj519.campusBBS.entity.User;
import com.zrgj519.campusBBS.service.PostService;
import com.zrgj519.campusBBS.service.TagService;
import com.zrgj519.campusBBS.service.UserService;
import com.zrgj519.campusBBS.util.CampusBBSConstant;
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
    @Autowired
    private TagService tagService;

    @RequestMapping(path="/header")
    public String getHeader(){
        return "/site/header";
    }

    @RequestMapping(path="/footer")
    public String getFooter(){
        return "/site/footer";
    }

    @RequestMapping(path="/aside_left")
    public String getAside_left(){
        return "/site/profile_aside_left";
    }

    @RequestMapping(path="/aside")
    public String getAside(Model model){
        // 获取侧边栏内容（tag）
        List<Tag> hotTags = tagService.getHotTags(CampusBBSConstant.HOT_TAG_COUNT);
        model.addAttribute("hotTags",hotTags);
        return "/site/aside";
    }

    @RequestMapping(path = "/denied", method = RequestMethod.GET)
    public String getDeniedPage() {
        return "/site/404error";
    }

    @RequestMapping(path = "/index", method = RequestMethod.GET)
    public String getPosts(Model model, Page page) {
    @RequestMapping("/collaborate-detail")
    public String getCollaborateDetail() {
        return "/site/collaborate-detail";
    }

    @RequestMapping("/collaborate")
    public String getCollaborate() {
        return "/site/collaborate";
    }

    @RequestMapping(path = "/index", method = RequestMethod.GET)
    public String getPosts(Model model, Page page) {
        // 获取推荐内容
        page.setRows(postService.getAllPostsCount());
        page.setPath("/index");

        // 根据用户是否登录，来获取到不同的内容
        User user = userContainer.getUser();
        List<Map<String, Object>> postsInfo = new ArrayList<>();

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
//        // 未登录
//        if(user == null){
//       }
//        // 登录
//        else{
//            // 使用后面的搜索功能查询到对应的帖子
//        }
        model.addAttribute("postsInfo", postsInfo);
        return "/site/index";
    }

    @RequestMapping("/collaborate")
    public String getCollaborate() {
        return "/site/collaborate";
    }

}

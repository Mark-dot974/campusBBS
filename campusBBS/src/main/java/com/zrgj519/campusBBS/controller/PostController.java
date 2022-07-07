package com.zrgj519.campusBBS.controller;

import com.zrgj519.campusBBS.entity.Page;
import com.zrgj519.campusBBS.entity.Post;
import com.zrgj519.campusBBS.entity.User;
import com.zrgj519.campusBBS.service.PostService;
import com.zrgj519.campusBBS.service.UserService;
import com.zrgj519.campusBBS.util.UserContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.jws.WebParam;
import java.util.*;

@Controller
@RequestMapping("/post")
public class PostController {
    @Autowired
    private PostService postService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserContainer userContainer;

    @RequestMapping(path="/publish",method = RequestMethod.GET)
    public String getPostPage() throws IllegalAccessException {
        User user = userContainer.getUser();
        if(user == null) throw new IllegalAccessException("未登录用户！");
        return "/site/post";
    }

    // 权限拦截
    // 前端使用ajax请求发布帖子，发布成功之后，先在发布页面提示发布成功，然后跳转到首页
    @RequestMapping(path = "/publish" , method = RequestMethod.POST)
    public String publish(Post post) throws IllegalAccessException {
        User user = userContainer.getUser();
        if(user == null) throw new IllegalAccessException("未登录用户！");
        post.setUserId(user.getId());
        post.setCreateTime(new Date());
        int i = postService.addPost(post);
        return "redirect:/index";
    }

    @RequestMapping(path = "/getPostsByCid" , method = RequestMethod.GET)
    public String getPosts(@RequestParam(value = "cid",defaultValue = "1") int cid , Model model, Page page){
        page.setRows(postService.getPostsCountByCid(cid));
        if(page.getCurrent()> page.getTotal()) page.setCurrent(page.getTotal());
        page.setPath("/post/getPostsByCid?cid="+cid);
        String cname="";
        if(cid == 1) cname = "失物招领";
        if(cid == 2) cname = "社交";
        if(cid == 3) cname = "情感";
        if(cid == 4) cname = "学习";
        if(cid == 5) cname = "互帮互助";
        if(cid == 6) cname = "其他";
        model.addAttribute("cname",cname);
        List<Post> postsByCid = postService.getPostsByCid(cid, page.getOffset(), page.getLimit());
        List<Map<String,Object>> postsInfo = new ArrayList<>();
        // 封装用户信息
        for (Post post : postsByCid) {
            Map<String,Object> postInfo = new HashMap<>();
            User userById = userService.findUserById(post.getUserId());
            postInfo.put("user",userById);
            postInfo.put("post",post);
            String tag = post.getTag();
            if(tag!=null){
                String[] split = tag.split(",");
                postInfo.put("tags",split);
            }
            postsInfo.add(postInfo);
        }
        model.addAttribute("postsInfo",postsInfo);
        return "/site/category";
    }

    @RequestMapping(path = "/getPostContent",method = RequestMethod.POST)
    @ResponseBody
    public String getPostContent(@RequestParam("id") int id){
        return postService.getPostById(id).getContent();
    }

    @RequestMapping(path = "/detail",method = RequestMethod.GET)
    public String detail(@RequestParam("id") int id,Model model){
        Post post = postService.getPostById(id);
        model.addAttribute("post",post);
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user",user);
        return "/site/detail";
    }
}

package com.zrgj519.campusBBS.controller;

import com.zrgj519.campusBBS.entity.Post;
import com.zrgj519.campusBBS.entity.User;
import com.zrgj519.campusBBS.service.PostService;
import com.zrgj519.campusBBS.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class ManagementController {
    @Autowired
    UserService userService;

    @Autowired
    PostService postService;

    @RequestMapping("/admin/users")
    public String showUser(Model model){
        List<User> users = userService.showUser();
        model.addAttribute("users",users);
        return "/site/user_account_management";
    }

    
    @RequestMapping("/admin/posts")
    public String showPosts(Model model){
        List<Post> allPosts = postService.getAllPosts();
        model.addAttribute("posts",allPosts);
        return "/site/post_management";
    }
}

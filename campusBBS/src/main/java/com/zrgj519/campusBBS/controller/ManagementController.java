package com.zrgj519.campusBBS.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zrgj519.campusBBS.entity.Group;
import com.zrgj519.campusBBS.entity.Post;
import com.zrgj519.campusBBS.entity.User;
import com.zrgj519.campusBBS.service.GroupService;
import com.zrgj519.campusBBS.service.PostService;
import com.zrgj519.campusBBS.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ManagementController {
    @Autowired
    UserService userService;

    @Autowired
    PostService postService;

    @Autowired
    GroupService groupService;
//    @RequestMapping("/showUsers")
//    public String showUser(Model model){
//        List<User> users = userService.showUser();
//        model.addAttribute("users",users);
//        return "/site/user_account_management";
//    }
//    @RequestMapping("/showPosts")
//    public String showPosts(Model model){
//        List<Post> allPosts = postService.showPost();
//        model.addAttribute("posts",allPosts);
//        return "/site/post_management";
//    }
//    用户管理
    @RequestMapping("/delUsers")
    public String deleteUser(int id){
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }

    @GetMapping("/findUsers")
    public String finduser(int id,Model model){
        User user = userService.find(id);
        model.addAttribute("user",user);
        return "/site/update_user_account";
    }
    @PostMapping("/updateUser")
    public String updateUser(User user){
        userService.updateUser(user);
        return "redirect:/admin/users";
    }    @RequestMapping("/admin/users")
    public String findUserByCondition(Model model,Integer id,String username,String email){
        List<User> users = userService.findUser(id,username,email);
        model.addAttribute("users",users);
        return "/site/user_account_management";
    }
    //    帖子管理
    @RequestMapping("/delPosts")
    public String deletePost(int id){
        postService.deletePost(id);
        return "redirect:/admin/posts";
    }
    @PostMapping("/updatePost")
    public String updatePost(Post post){
       postService.updatePost(post);
        return "redirect:/admin/posts";
    }
    @GetMapping("/findPosts")
    public String findpost(int id,Model model){
        Post post = postService.find(id);
        model.addAttribute("post",post);
        return "/site/update_post";
    }

    @RequestMapping("/admin/posts")
    public String findPostByCondition(Model model,Integer id,String title,String tag){
        List<Post> allPosts = postService.findPost(id,title,tag);
        model.addAttribute("posts",allPosts);
        return "/site/post_management";
    }

//    圈子管理
    @RequestMapping("/delGroups")
    public String deleteGroup(Integer gid){
        groupService.deleteGroup(gid);
        return "redirect:/admin/groups";
    }
    @PostMapping("/updateGroup")
    public String updateGroup(Group group){
        groupService.updateGroup(group);
        return "redirect:/admin/groups";
    }
    @GetMapping("/findGroups")
    public String findgroup(Integer gid,Model model){
        Group group = groupService.find(gid);
        model.addAttribute("group",group);
        return "/site/update_group";
    }
    @RequestMapping("/admin/groups")
    public String findGroupByCondition(Model model,Integer gid,String groupName,String members){
        List<Group> allGroups = groupService.findGroup(gid,groupName,members);
        model.addAttribute("groups",allGroups);
        return "/site/group_management";
    }

}

package com.zrgj519.campusBBS.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zrgj519.campusBBS.entity.*;
import com.zrgj519.campusBBS.service.GroupService;
import com.zrgj519.campusBBS.service.PostService;
import com.zrgj519.campusBBS.service.TagService;
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

    @Autowired
    TagService tagService;
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
    }
    @RequestMapping("/admin/users")
    public String findUserByCondition(Model model, Integer id, String username, String email,
                                      Page page){
        page.setRows(userService.getAllUsersCount(id,username,email));
        if(username == null || username.length() == 0)
        {
            if(email == null || email.length() == 0){
                page.setPath("/admin/users");
            }else{
                page.setPath("/admin/users?email="+email);
            }
        }else{
            if(email == null || email.length() == 0){
                page.setPath("/admin/users?username="+username);
            }else{
                page.setPath("/admin/users?email="+email+"&username="+username);
            }
        }
        List<User> users = userService.findUser(id,username,email,page.getOffset(), page.getLimit());
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
    public String findPostByCondition(Model model,Integer id,String title,String tag,
                                      Page page){
        page.setRows(postService.getPostsCount(id,title,tag));
        if(title == null || title.length() == 0)
        {
            if(tag == null || tag.length() == 0){
                page.setPath("/admin/posts");
            }else{
                page.setPath("/admin/posts?tag="+tag);
            }
        }else{
            if(tag == null || tag.length() == 0){
                page.setPath("/admin/posts?title="+title);
            }else{
                page.setPath("/admin/posts?tag="+tag+"&title="+title);
            }
        }
        List<Post> allPosts = postService.findPost(id,title,tag,page.getOffset(), page.getLimit());
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
    public String findGroupByCondition(Model model,Integer gid,String groupName,String members,Page page){
        page.setRows(groupService.getGroupsCount(gid,groupName,members));
        if(groupName == null || groupName.length() == 0)
        {
            if(members == null || members.length() == 0){
                page.setPath("/admin/groups");
            }else{
                page.setPath("/admin/groups?members="+members);
            }
        }else{
            if(members == null || members.length() == 0){
                page.setPath("/admin/groups?groupName="+groupName);
            }else{
                page.setPath("/admin/groups?members="+members+"&groupName="+groupName);
            }
        }
        List<Group> allGroups = groupService.findGroup(gid,groupName,members, page.getOffset(), page.getLimit());

        model.addAttribute("groups",allGroups);
        return "/site/group_management";
    }
    //    标签管理
    @RequestMapping("/delTags")
    public String deleteTag(Integer id){
        tagService.deleteTag(id);
        return "redirect:/admin/tags";
    }
    @PostMapping("/updateTag")
    public String updateTag(Tag tag){
        tagService.updateTag(tag);
        return "redirect:/admin/tags";
    }
    @GetMapping("/findTags")
    public String findtag(Integer gid,Model model){
        Tag tag = tagService.find(gid);
        model.addAttribute("tag",tag);
        return "/site/update_tag";
    }
    @RequestMapping("/admin/tags")
    public String findTagByCondition(Model model,Integer id,String tagName,String members,Page page){
        page.setRows(tagService.getTagsCount(id,tagName,members));
        if(tagName == null || tagName.length() == 0)
        {
            if(members == null || members.length() == 0){
                page.setPath("/admin/tags");
            }else{
                page.setPath("/admin/tags?members="+members);
            }
        }else{
            if(members == null || members.length() == 0){
                page.setPath("/admin/tags?tagName="+tagName);
            }else{
                page.setPath("/admin/tags?members="+members+"&tagName="+tagName);
            }
        }
        List<Tag> allTags = tagService.findTag(id,tagName,members, page.getOffset(), page.getLimit());

        model.addAttribute("tags",allTags);
        return "/site/tag_management";
    }

}

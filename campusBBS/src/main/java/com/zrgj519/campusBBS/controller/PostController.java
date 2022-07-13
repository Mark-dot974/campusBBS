package com.zrgj519.campusBBS.controller;

import com.zrgj519.campusBBS.entity.Page;
import com.zrgj519.campusBBS.entity.Post;
import com.zrgj519.campusBBS.entity.User;
import com.zrgj519.campusBBS.service.ElasticsearchService;
import com.zrgj519.campusBBS.service.PostService;
import com.zrgj519.campusBBS.service.TagService;
import com.zrgj519.campusBBS.service.UserService;
import com.zrgj519.campusBBS.util.UserContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    @Autowired
    private TagService tagService;
    @Autowired
    private ElasticsearchService elasticsearchService;

    @RequestMapping(path="/publish",method = RequestMethod.GET)
    public String getPostPage(){
        return "/site/post";
    }

    // 权限拦截
    // 发布帖子时，除了将帖子添加到数据库，还要将帖子添加到对应的tag表中
    @RequestMapping(path = "/publish" , method = RequestMethod.POST)
    public String publish(Post post) {
        User user = userContainer.getUser();
        post.setUserId(user.getId());
        post.setCreateTime(new Date());
        postService.addPost(post);
        // 同时同步数据到es服务器
        elasticsearchService.savePost(post);
        return "redirect:/index";
    }

    @RequestMapping(path = "/getPostsByCid" , method = RequestMethod.GET)
    public String getPosts(@RequestParam(value = "cid",defaultValue = "1") int cid , Model model, Page page){
        page.setRows(postService.getPostsCountByCid(cid));
        if(page.getCurrent()> page.getTotal()) page.setCurrent(page.getTotal());
        page.setPath("/post/getPostsByCid?cid="+cid);
        String cname="";
        // 因为协作板块与其他版块内容不同， 故单独使用controller方法处理
        if(cid == 1) {
            return "redirect:/group/getAll";
        }
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
                if(split!=null && split.length!=0&&split[0].length()!=0)  {
                    postInfo.put("tags",split);
                }else{
                    postInfo.put("tags",null);
                }
            }
            postsInfo.add(postInfo);
        }
        model.addAttribute("postsInfo",postsInfo);
        return "/site/category";
    }

    @RequestMapping(path = "/getPostsByTag" , method = RequestMethod.GET)
    public String getPostsByTag(Model model,Page page,@RequestParam("tagName")String tagName){
        page.setRows(tagService.getTagPostCount(tagName));
        List<Post> postsByTag = tagService.getPostsByTag(tagName);
        List<Map<String,Object>> postsInfo = new ArrayList<>();
        // 封装用户信息
        for (Post post : postsByTag) {
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
        model.addAttribute("tagName",tagName);
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

    @RequestMapping("/search")
    public String search(Model model,String keyword,Page page){
        System.out.println("keyword = " + keyword);
        // es的分页是从0开始的
        org.springframework.data.domain.Page<Post> result
                = elasticsearchService.searchDiscussPost("高数", page.getCurrent() - 1, page.getLimit());
        // 聚合数据
        System.out.println("result = " + result);
        List<Map<String,Object>> posts = new ArrayList<>();
        if(result!=null){
            for (Post post : result) {
                Map<String,Object> p = new HashMap<>();
                User userById = userService.findUserById(post.getUserId());
                p.put("user",userById);
                p.put("post",post);
                String tag = post.getTag();
                if(tag!=null){
                    String[] tags = tag.split(",");
                    p.put("tags",tags);
                }
                posts.add(p);
            }
        }
        model.addAttribute("postsInfo",posts);
        model.addAttribute("searchName","搜索结果");
        return "/site/category";
    }
}

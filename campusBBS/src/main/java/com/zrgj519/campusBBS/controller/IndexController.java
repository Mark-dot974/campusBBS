package com.zrgj519.campusBBS.controller;

import com.zrgj519.campusBBS.entity.Page;
import com.zrgj519.campusBBS.entity.Post;
import com.zrgj519.campusBBS.entity.Tag;
import com.zrgj519.campusBBS.entity.User;
import com.zrgj519.campusBBS.service.*;
import com.zrgj519.campusBBS.util.CampusBBSConstant;
import com.zrgj519.campusBBS.util.UserContainer;
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
    @Autowired
    private UserContainer userContainer;
    @Autowired
    private LikeService likeService;
    @Autowired
    private CommentService commentService;

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

    @RequestMapping(path="/message_center_aside")
    public String getMca(){
        return "/site/message_center_aside";
    }
//
//    @RequestMapping("/message_center_reply")
//    public String getMessageReply(){ return "/site/message_center_reply";}
//
//    @RequestMapping("/message_center_like")
//    public String getMessageLike(){ return "/site/message_center_like";}
//
    @RequestMapping("/message_center_system")
    public String getMessageSystem(){ return "/site/message_center_system";}
//
//    @RequestMapping("/personal_page")
//    public String getPersonalPage(){ return "/site/personal_page";}

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

    @RequestMapping("/personalPage")
    public String getPersonalPage(){
        return "/site/personal_page";
    }
    @RequestMapping("/groupList")
    public String gert(){
        return "/site/group_list";
    }

    // 首页不分页
    @RequestMapping(path = "/index", method = RequestMethod.GET)
    public String getPosts(Model model, Page page) {
        // 获取推荐内容
        page.setRows(postService.getAllPostsCount());
        page.setPath("/index");

        // 根据用户是否登录，来获取到不同的内容
        User user = userContainer.getUser();
        List<Post> posts = new ArrayList<>();
        // 用户未登录
        if(user == null)
        {
            posts = postService.getAllPosts();
        }
        // 登录
        else{
            String userInterest = user.getInterest();
            // 用户没有选标签
            if(userInterest == null || userInterest.equals("")){
                posts = postService.getAllPosts();
            }
            // 根据用户兴趣查找出来的内容
            else{
                // 使用es根据用户兴趣查找内容
                org.springframework.data.domain.Page<Post> postsByUserInterest
                        = postService.findPostsByUserInterest(userInterest, page.getOffset(), page.getLimit());
                List<Post> allPost = postService.getAllPosts();
                // 根据用户兴趣查找不到帖子
                if(postsByUserInterest == null){
                    posts = allPost;
                }
                // 有帖子
                else{
                    int i = 0;
                    int allPostsIndex = 0;
                    // 把es中查到的内容取出来放到list中,3条感兴趣的，1条allPosts中的。
                    for (Post post : postsByUserInterest) {
                        i++;
                        posts.add(post);
                        // 避免重复
                        allPost.remove(post);
                        if(i%3==0){
                            posts.add(allPost.remove(allPostsIndex++));
                        }
                    }
                    posts.addAll(allPost);
                }
            }
        }
        // 封装用户信息
        List<Map<String,Object>> postsInfo = new ArrayList<>();
        for (Post post : posts) {
            Map<String,Object> postInfo = new HashMap<>();
            User userById = userService.findUserById(post.getUserId());
            postInfo.put("user",userById);
            postInfo.put("post",post);

            /**
             * 填充点赞评论数量
             */
            long likeCount = likeService.findEntityLikeCount(CampusBBSConstant.ENTITY_TYPE_POST, post.getId());
            postInfo.put("likeCount",likeCount);
            int commentCount = commentService.findCommentCount(CampusBBSConstant.ENTITY_TYPE_POST, post.getId());
            postInfo.put("commentCount",commentCount);

            String tag = post.getTag();
            if(tag!=null){
                String[] split = tag.split(",");
                if(split!=null && split.length!=0&&split[0].length()!=0)  postInfo.put("tags",split);
                else postInfo.put("tags",null);
            }
            postsInfo.add(postInfo);
        }
        model.addAttribute("postsInfo", postsInfo);
        return "/site/index";
    }

}
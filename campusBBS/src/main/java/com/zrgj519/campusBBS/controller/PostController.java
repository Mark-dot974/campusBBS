package com.zrgj519.campusBBS.controller;

import com.zrgj519.campusBBS.entity.Comment;
import com.zrgj519.campusBBS.entity.Page;
import com.zrgj519.campusBBS.entity.Post;
import com.zrgj519.campusBBS.entity.User;
import com.zrgj519.campusBBS.service.*;
import com.zrgj519.campusBBS.util.CampusBBSConstant;
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
    @Autowired
    private LikeService likeService;
    @Autowired
    private CommentService commentService;

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
            long likeCount = likeService.findEntityLikeCount(CampusBBSConstant.ENTITY_TYPE_POST, post.getId());
            postInfo.put("likeCount",likeCount);
            postsInfo.add(postInfo);
        }
        model.addAttribute("postsInfo",postsInfo);
        return "/site/category";
    }

    @RequestMapping(path = "/getPostsByTag" , method = RequestMethod.GET)
    public String getPostsByTag(Model model,Page page,@RequestParam("tagName")String tagName){
        page.setRows(tagService.getTagPostCount(tagName));
        page.setPath("/post/getPostsByTag?tagName="+tagName);
        List<Post> postsByTag = tagService.getPostsByTag(tagName);
        List<Map<String,Object>> postsInfo = new ArrayList<>();
        // 封装用户信息
        for (Post post : postsByTag) {
            Map<String,Object> postInfo = new HashMap<>();
            User userById = userService.findUserById(post.getUserId());
            postInfo.put("user",userById);
            postInfo.put("post",post);
            long likeCount = likeService.findEntityLikeCount(CampusBBSConstant.ENTITY_TYPE_POST, post.getId());
            postInfo.put("likeCount",likeCount);
            int commentCount = commentService.findCommentCount(CampusBBSConstant.ENTITY_TYPE_POST, post.getId());
            postInfo.put("commentCount",commentCount);
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
    public String detail(@RequestParam("id") int id,Model model,Page page){
        Post post = postService.getPostById(id);
        String tag = post.getTag();
        if(tag!=null){
            String[] split = tag.split(",");
            if(split!=null && split.length!=0&&split[0].length()!=0)  {
                model.addAttribute("tags",split);
            }else{
                model.addAttribute("tags",null);
            }
        }
        model.addAttribute("post",post);
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user",user);

        /**
         * 点赞相关
         */

        // 设置帖子的点赞数量
        long likeCount = likeService.findEntityLikeCount(CampusBBSConstant.ENTITY_TYPE_POST, id);
        model.addAttribute("likeCount",likeCount);
        // 设置帖子的点赞状态
        int likeStatus;
        if(userContainer.getUser() == null)
        {
            likeStatus=0;
        }
        else{
            likeStatus = likeService.findEntityLikeStatus(userContainer.getUser().getId(), CampusBBSConstant.ENTITY_TYPE_POST, id);
        }
        model.addAttribute("likeStatus",likeStatus);


        /**
         * 评论相关
         */
        // 评论的分页信息
        page.setLimit(5);
        page.setPath("/discuss/detail/"+ id);
        page.setRows(post.getCommentCount());

        // 获取评论列表信息
        // 评论：给帖子的评论
        // 回复：给评论的评论
        // 评论列表
        List<Comment> commentList = commentService.findCommentsByEntity(CampusBBSConstant.ENTITY_TYPE_POST, post.getId(), page.getOffset(), page.getLimit());
        // 评论VO(View Object)列表
        List<Map<String,Object>> commentVoList = new ArrayList<>();
        if(commentList!=null){
            for (Comment comment : commentList) {
                // 评论VO
                Map<String , Object> commentVo = new HashMap<>();
                commentVo.put("comment",comment);
                // 添加评论的评论者
                commentVo.put("user",userService.findUserById(comment.getUserId()));
                //添加评论的点赞数量
                likeCount = likeService.findEntityLikeCount(CampusBBSConstant.ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("likeCount",likeCount);
                // 未登录时
                if(userContainer.getUser() == null)
                {
                    likeStatus=0;
                }
                else {
                    likeStatus = likeService.findEntityLikeStatus(userContainer.getUser().getId(), CampusBBSConstant.ENTITY_TYPE_COMMENT, comment.getId());
                }
                commentVo.put("likeStatus",likeStatus);

                // 获取帖子的回复列表,有多少显示多少
                List<Comment> replyList = commentService.findCommentsByEntity(CampusBBSConstant.ENTITY_TYPE_COMMENT,comment.getId(),0,Integer.MAX_VALUE);
                // 构造回复的VO列表
                List<Map<String,Object>> replyVoList = new ArrayList<>();
                if(replyVoList!=null){
                    for (Comment reply : replyList) {
                        Map<String,Object> replyVo = new HashMap<>();
                        // 回复
                        replyVo.put("reply",reply);
                        // 作者，回复者
                        replyVo.put("user",userService.findUserById(reply.getUserId()));
                        // 回复对象
                        // 有两种可能，在帖子的评论下评论（回复），但是这时是不显示回复对象的，所以target是0；
                        // 第二种可能是楼主回复评论者，或评论者回复楼主，这时有target
                        // 被回复者
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVo.put("target",target);
                        // 设置reply的点赞数量
                        likeCount = likeService.findEntityLikeCount(CampusBBSConstant.ENTITY_TYPE_COMMENT,reply.getId());
                        replyVo.put("likeCount",likeCount);
                        if(userContainer.getUser() == null)
                        {
                            likeStatus=0;
                        }
                        else likeStatus = likeService.findEntityLikeStatus(userContainer.getUser().getId(),CampusBBSConstant.ENTITY_TYPE_COMMENT,reply.getId());
                        replyVo.put("likeStatus",likeStatus);
                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replys",replyVoList);
                // 回复数量
                int replyCount = commentService.findCommentCount(CampusBBSConstant.ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount",replyCount);
                commentVoList.add(commentVo);
            }
        }
        model.addAttribute("comments",commentVoList);
        return "/site/detail";
    }


    @RequestMapping("/search")
    public String search(Model model,String keyword,Page page){
        // es的分页是从0开始的
        org.springframework.data.domain.Page<Post> result
                = elasticsearchService.searchDiscussPost(keyword, page.getCurrent() - 1, page.getLimit());
        // 聚合数据
        List<Map<String,Object>> posts = new ArrayList<>();
        if(result!=null){
            for (Post post : result) {
                Map<String,Object> p = new HashMap<>();
                User userById = userService.findUserById(post.getUserId());
                p.put("user",userById);
                p.put("post",post);
                String tag = post.getTag();
                if(tag!=null){
                    String[] split = tag.split(",");
                    if(split!=null && split.length!=0&&split[0].length()!=0)  {
                        p.put("tags",split);
                    }else{
                        p.put("tags",null);
                    }
                }
                long likeCount = likeService.findEntityLikeCount(CampusBBSConstant.ENTITY_TYPE_POST, post.getId());
                p.put("likeCount",likeCount);
                posts.add(p);
            }
        }
        model.addAttribute("postsInfo",posts);
        model.addAttribute("searchName","搜索结果");
        return "/site/category";
    }

    @RequestMapping("/personalPost")
    public String findPersonalPost(Model model,int userId,Page page){
        page.setRows(postService.selectCountOfPersonalPost(userId));
        page.setPath("/post/personalPost?userId="+userId);
        User user = userService.findUserById(userId);
        model.addAttribute("user",user);
        List<Post> post = postService.selectPersonalPost(userId,page.getOffset(),page.getLimit());
        List<Map<String,Object>> posts = new ArrayList<>();
        for (Post post1 : post) {
            Map<String,Object> map = new HashMap<>();
            long likeCount = likeService.findEntityLikeCount(CampusBBSConstant.ENTITY_TYPE_POST, post1.getId());
            map.put("post",post1);
            map.put("likeCount",likeCount);
            int commentCount = commentService.findCommentCount(CampusBBSConstant.ENTITY_TYPE_POST, post1.getId());
            map.put("commentCount",commentCount);
            String tag = post1.getTag();
            if(tag!=null){
                String[] split = tag.split(",");
                if(split!=null && split.length!=0&&split[0].length()!=0)  {
                    map.put("tags",split);
                }else{
                    map.put("tags",null);
                }
            }
            posts.add(map);
        }
        model.addAttribute("posts",posts);
        return "/site/personal_post";
    }
}
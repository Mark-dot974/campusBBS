package com.zrgj519.campusBBS.controller;

import com.zrgj519.campusBBS.entity.User;
import com.zrgj519.campusBBS.service.LikeService;
import com.zrgj519.campusBBS.util.CampusBBSUtil;
import com.zrgj519.campusBBS.util.UserContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController {
    @Autowired
    private LikeService likeService;

    @Autowired
    private UserContainer hostHolder;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 点赞
     * @param entityType        点赞对象的类型
     * @param entityId          点赞对象的Id
     * @param entityUserId      点赞对象发布者的Id
     * @return
     */
    @RequestMapping(path = "/like" , method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType , int entityId , int entityUserId,int postId){
        User user = hostHolder.getUser();
        // 点赞
        likeService.like(user.getId(),entityType,entityId,entityUserId);
        // 数量
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        // 状态
        int likeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);
        // 返回的结果
        Map<String,Object> map = new HashMap<>();
        map.put("likeCount",likeCount);
        map.put("likeStatus",likeStatus);

        return CampusBBSUtil.getJSONString(0,null,map);
    }
}

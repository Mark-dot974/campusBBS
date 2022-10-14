package com.zrgj519.campusBBS.controller;


import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.zrgj519.campusBBS.entity.Post;
import com.zrgj519.campusBBS.entity.User;
import com.zrgj519.campusBBS.service.LikeService;
import com.zrgj519.campusBBS.service.UserService;
import com.zrgj519.campusBBS.util.CampusBBSConstant;
import com.zrgj519.campusBBS.util.CampusBBSUtil;
import com.zrgj519.campusBBS.util.UserContainer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserContainer userContainer;

    @Value("${qiniu.key.access}")
    private String accessKey;

    @Value("${qiniu.key.secret}")
    private String secretKey;

    @Value("${qiniu.bucket.header.name}")
    private String headerBucketName;

    @Value("${qiniu.bukcet.header.url}")
    private String headerBucketUrl;

    @Autowired
    private LikeService likeService;

    @RequestMapping("/set")
    public String getSetPage(Model model){
        // 上传文件名称
        String fileName = CampusBBSUtil.generateUUID();
        // 设置响应信息
        StringMap policy = new StringMap();
        // 成功返回：code:0
        policy.put("returnBody",CampusBBSUtil.getJSONString(0));
        Auth auth = Auth.create(accessKey,secretKey);
        String uploadToken = auth.uploadToken(headerBucketName,fileName,3600,policy);
        model.addAttribute("uploadToken",uploadToken);
        model.addAttribute("fileName",fileName);
        // 生成上传凭证
        model.addAttribute("loginUser",userContainer.getUser());
        return "/site/profile_set";
    }

    @RequestMapping("/updateHeader")
    @ResponseBody
    public String updateHeader(String fileName){
        String url = headerBucketUrl + "/" + fileName;
        User user = userContainer.getUser();
        userService.updateHeader(user.getId(),url);
        return CampusBBSUtil.getJSONString(0);
    }

    @RequestMapping("/updateInfo")
    public String updateInfo(Model model,User user){
        user.setPassword(CampusBBSUtil.md5(user.getPassword()+userContainer.getUser().getSalt()));
        userService.updateUser(user);
        model.addAttribute("loginUser",userContainer.getUser());
        return "/site/profile_set";
    }

    @RequestMapping("/profile/{id}")
    public String findUserById(@PathVariable("id") int id, Model model){
        User user = userService.findUserById(id);
        model.addAttribute("user",user);
        model.addAttribute("loginUser",userContainer.getUser());
        boolean self =false;
        if(userContainer.getUser()!=null){
            if(user.getUsername().equals(userContainer.getUser().getUsername())){
                self = true;
            }
        }
        model.addAttribute("self",self);
        String tag = user.getInterest();
        if(tag!=null){
            String[] split = tag.split(",");
            if(split!=null && split.length!=0&&split[0].length()!=0)  model.addAttribute("tags",split);
            else model.addAttribute("tags",null);
        }
        long likeCount = likeService.findUserLikeCount(user.getId());
        model.addAttribute("likeCount",likeCount);
        return "/site/personal_page";
    }

    @RequestMapping("/message")
    public String getMessage(Model model)
    {
        model.addAttribute("user",userContainer.getUser());
        return "/site/personal_message";
    }

}

package com.zrgj519.campusBBS.controller;

import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.zrgj519.campusBBS.entity.*;
import com.zrgj519.campusBBS.entity.Event.Event;
import com.zrgj519.campusBBS.entity.Event.Producer;
import com.zrgj519.campusBBS.service.GroupService;
import com.zrgj519.campusBBS.service.UserService;
import com.zrgj519.campusBBS.util.CampusBBSConstant;
import com.zrgj519.campusBBS.util.CampusBBSUtil;
import com.zrgj519.campusBBS.util.UserContainer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/group")
public class GroupController {
    @Autowired
    private GroupService groupService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserContainer userContainer;

    @Value("${qiniu.key.access}")
    private String accessKey;

    @Value("${qiniu.key.secret}")
    private String secretKey;

    @Value("${qiniu.bucket.file.name}")
    private String fileBucketName;

    @Value("${qiniu.bukcet.file.url}")
    private String fileBucketUrl;

    @Autowired
    private Producer producer;

    @RequestMapping("/create")
    @ResponseBody
    public String createGroup(Group group){
        System.out.println("create a group");
        Group groupByName = groupService.getGroupByName(group.getGroupName());
        if(groupByName!=null){
            return CampusBBSUtil.getJSONString(1,"协作圈名已存在~");
        }
        group.setMembers(userContainer.getUser().getUsername());
        group.setMembersCount(1);
        group.setGroupLeader(userContainer.getUser().getUsername());
        group.setCreateTime(new Date());
        groupService.addGroup(group);
        return CampusBBSUtil.getJSONString(0,"创建成功");
    }

    @RequestMapping("/getAll")
    // mode:  0 --- 全部   1 -- 我加入的   2 -- 我创建的
    public String getAllGroup(Model model, Page page, @RequestParam(value = "mode",defaultValue = "0")int mode){
        page.setRows(groupService.getGroupTotalCount());
        page.setPath("/group/getAll?mode="+mode);
        User user1 = userContainer.getUser();
        model.addAttribute("loginUser",user1);
        model.addAttribute("mode",mode);

        List<Group> allGroups = groupService.getAll(page.getOffset(),page.getLimit(),mode);
        List<Map<String,Object>> groupsInfo = new ArrayList<>();
        for (Group group : allGroups) {
            Map<String,Object> map = new HashMap<>();
            List<User> members = new ArrayList<>();
            String m = group.getMembers();
            String[] users = m.split(",");
//            int length = users.length;
            for (String userName : users) {
                User user = userService.findUserByName(userName);
                members.add(user);
            }
            String tag = group.getTag();
            String[] tags = tag.split(",");
//            map.put("memberCount",length);
            map.put("tags",tags);
            map.put("members",members);
            map.put("group",group);
            groupsInfo.add(map);
        }
        model.addAttribute("groups",groupsInfo);
        return "/site/collaborate";
    }

    @RequestMapping("/groupDetail")
    public String getGroupDetail(Model model,int gid){

        // 上传文件名称
        String fileName = CampusBBSUtil.generateUUID();
        // 设置响应信息
        StringMap policy = new StringMap();
        // 成功返回：code:0
        policy.put("returnBody",CampusBBSUtil.getJSONString(0));
        Auth auth = Auth.create(accessKey,secretKey);
        String uploadToken = auth.uploadToken(fileBucketName,fileName,3600,policy);
        model.addAttribute("uploadToken",uploadToken);
        model.addAttribute("fileName",fileName);
        model.addAttribute("gid",gid);
        Group group = groupService.getGroupById(gid);
        model.addAttribute("group",group);
        model.addAttribute("loginUser",userContainer.getUser());
        // 填充圈员信息
        List<User> members = new ArrayList<>();
        String m = group.getMembers();
        String[] users = m.split(",");
        for (String userName : users) {
            User user = userService.findUserByName(userName);
            members.add(user);
        }
        model.addAttribute("members",members);

        // 填充文件信息
        List<GroupFile> allFiles = groupService.getAllFiles(gid);
        model.addAttribute("files",allFiles);
        return "/site/collaborate-detail";
    }

    @RequestMapping("/uploadFile")
    @ResponseBody
    public String uploadFile(String fileName,int gid){
        if (StringUtils.isBlank(fileName)) {
            return CampusBBSUtil.getJSONString(1, "文件名不能为空!");
        }
        String url = fileBucketUrl + "/" + fileName;
        GroupFile file = new GroupFile();
        file.setFileName(fileName);
        file.setCreateTime(new Date());
        file.setGid(gid);
        file.setUrl(url);
        System.out.println("file = " + file.toString());
        groupService.uploadFile(file);
        return CampusBBSUtil.getJSONString(0);
    }

    @RequestMapping("/apply")
    @ResponseBody
    public String applyForGroup(String groupName,int gid,String leader,int userId){
        User groupLeader = userService.findUserByName(leader);
        // 触发申请事件
        Event event = new Event();
        event.setTopic(CampusBBSConstant.TOPIC_APPLY)
                .setEntityId(gid)
                .setUserId(userId)
                .setEntityUserId(groupLeader.getId());

        producer.fireEvent(event);
        return CampusBBSUtil.getJSONString(0);
    }
}

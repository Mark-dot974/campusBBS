package com.zrgj519.campusBBS.controller;

import com.zrgj519.campusBBS.entity.Group;
import com.zrgj519.campusBBS.entity.GroupFile;
import com.zrgj519.campusBBS.entity.Page;
import com.zrgj519.campusBBS.entity.User;
import com.zrgj519.campusBBS.service.GroupService;
import com.zrgj519.campusBBS.service.UserService;
import com.zrgj519.campusBBS.util.CampusBBSUtil;
import com.zrgj519.campusBBS.util.UserContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public String getAllGroup(Model model, Page page){
        page.setRows(groupService.getGroupTotalCount());
        page.setPath("/group/getAll");
        List<Group> allGroups = groupService.getAll();
        System.out.println("allGroups = " + allGroups);
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
        Group group = groupService.getGroupById(gid);
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
}
package com.zrgj519.campusBBS.controller;

import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.zrgj519.campusBBS.annotation.GroupLeaderRequired;
import com.zrgj519.campusBBS.annotation.GroupMemberRequired;
import com.zrgj519.campusBBS.entity.*;
import com.zrgj519.campusBBS.entity.Event.Event;
import com.zrgj519.campusBBS.entity.Event.Producer;
import com.zrgj519.campusBBS.service.FileService;
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
import org.springframework.web.multipart.MultipartFile;

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

    @Autowired
    private FileService fileService;

    @RequestMapping("/create")
    @ResponseBody
    public String createGroup(Group group){
        Group groupByName = groupService.getGroupByName(group.getGroupName());
        if(groupByName!=null){
            return CampusBBSUtil.getJSONString(1,"协作圈名已存在~");
        }
        group.setMembers(userContainer.getUser().getUsername());
        group.setMembersCount(1);
        group.setGroupHeader("http://rib3987wa.hn-bkt.clouddn.com/%E9%BB%98%E8%AE%A4%E7%BB%84%E5%A4%B4%E5%83%8F.jpg");
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
            if(tag!=null){
                String[] split = tag.split(",");
                if(split!=null && split.length!=0&&split[0].length()!=0)  map.put("tags",split);
                else map.put("tags",null);
            }
//            map.put("memberCount",length);
            map.put("members",members);
            map.put("group",group);
            groupsInfo.add(map);
        }
        model.addAttribute("groups",groupsInfo);
        return "/site/collaborate";
    }

    @RequestMapping("/groupDetail")
    public String getGroupDetail(Model model,int gid,Page page){
        page.setRows(fileService.filesCount(gid));
        page.setPath("/group/groupDetail?gid="+gid);
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
        boolean isMember=false;
        for (String userName : users) {
            if (userContainer.getUser() != null && userName.equals(userContainer.getUser().getUsername())) {
                isMember = true;
            }
            User user = userService.findUserByName(userName);
            members.add(user);
        }
        model.addAttribute("isMember",isMember);
        model.addAttribute("members",members);

        // 填充文件信息
        List<GroupFile> allFiles = fileService.getAllFiles(gid);

        model.addAttribute("files",allFiles);
        return "/site/collaborate-detail";
    }

    @RequestMapping("/uploadFile")
    @ResponseBody
    @GroupMemberRequired
    public String uploadFile(String key, int gid, MultipartFile file){
        if (StringUtils.isBlank(key)) {
            return CampusBBSUtil.getJSONString(1, "文件名不能为空!");
        }
        String url = fileBucketUrl + "/" + key;
        GroupFile groupFile = new GroupFile();
        String originalFilename = file.getOriginalFilename();
        GroupFile fileByName = fileService.findFileByName(originalFilename);
        if(fileByName!=null){
            return CampusBBSUtil.getJSONString(1,"文件已存在！");
        }
        groupFile.setFileName(originalFilename);
        groupFile.setCreateTime(new Date());
        groupFile.setGid(gid);
        groupFile.setUrl(url);
        fileService.uploadFile(groupFile);
        return CampusBBSUtil.getJSONString(0);
    }

    // 未实现
    @RequestMapping("/updateFile")
    @ResponseBody
    @GroupMemberRequired
    public String uploadFile(String key,int fid){
        if (StringUtils.isBlank(key)) {
            return CampusBBSUtil.getJSONString(1, "文件名不能为空!");
        }
        String url = fileBucketUrl + "/" + key;
        fileService.updateFile(fid,url);
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
                .setEntityUserId(groupLeader.getId())
                .setData("result",0);

        producer.fireEvent(event);
        return CampusBBSUtil.getJSONString(0);
    }

    // 同意：你已经成为了（）的一员啦~
    // 很遗憾，你的申请被拒绝了
    @RequestMapping("/operate")
    @ResponseBody
    @GroupLeaderRequired
    public String accept(String operation,int userId,int gid){
        // 防止违规行为
        Group group = groupService.find(gid);
        if(!userContainer.getUser().getUsername().equals(group.getGroupLeader())){
            return CampusBBSUtil.getJSONString(1,"只有组长才有此操作权限");
        }
        Event event = new Event()
                .setEntityId(gid)
                .setEntityUserId(userId)
                .setTopic(operation);
          producer.fireEvent(event);
          return CampusBBSUtil.getJSONString(0);
    }

    @RequestMapping("/invite")
    @ResponseBody
    @GroupMemberRequired
    public String invite(String username,int gid){
        User userByName = userService.findUserByName(username);
        if(userByName == null){
            return CampusBBSUtil.getJSONString(1,"你邀请的用户不存在!");
        }
        boolean isMember = groupService.isGroupMember(gid, username);
        if(isMember){
            return CampusBBSUtil.getJSONString(1,"你邀请的用户已经加入了该圈子~");
        }
        groupService.addGroupMember(username,gid);
        return CampusBBSUtil.getJSONString(0);
    }

    @RequestMapping("/personalGroup")
    public String findPersonalGroup(Model model,String members,Page page){
        page.setRows(groupService.selectCountOfPersonalGroup(members));
        page.setPath("/group/personalGroup?members="+members);
        User user = userService.selectByNameInGroup(members);
        model.addAttribute("user",user);
        List<Group> group = groupService.selectPersonalGroup(members,page.getOffset(),page.getLimit());
        model.addAttribute("group",group);
        return "/site/personal_group";
    }
}

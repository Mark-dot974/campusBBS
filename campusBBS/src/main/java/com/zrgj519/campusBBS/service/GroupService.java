package com.zrgj519.campusBBS.service;

import com.zrgj519.campusBBS.dao.FileMapper;
import com.zrgj519.campusBBS.dao.GroupMapper;
import com.zrgj519.campusBBS.entity.Group;
import com.zrgj519.campusBBS.entity.GroupFile;
import com.zrgj519.campusBBS.entity.Post;
import com.zrgj519.campusBBS.util.UserContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupService {
    @Autowired
    private GroupMapper groupMapper;

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private UserContainer userContainer;


    public List<Group> getAll(int offset,int limit,int mode){
        // 所有
        if(mode == 0) return groupMapper.selectAll(offset, limit,null,null);
        // 我创建的
        if(mode == 2) return groupMapper.selectAll(offset, limit,userContainer.getUser().getUsername(),null);
        // 我加入的
        if(mode == 1) return groupMapper.selectAll(offset, limit,null,userContainer.getUser().getUsername());
        else return null;
    }

    public void addGroup(Group group){
        groupMapper.insertGroup(group);
    }

    public Group getGroupByName(String groupName){
        return groupMapper.selectGroupByName(groupName);
    }

    public int getGroupTotalCount(){
        return groupMapper.selectGroupCount();
    }

    public Group getGroupById(int gid) {
        return groupMapper.selectGroupById(gid);
    }


    public int deleteGroup(Integer gid) {
        return groupMapper.deleteGroup(gid);
    }


    public void addGroupMember(String username,int gid){
        Group group = groupMapper.selectGroupById(gid);
        String members = group.getMembers();
        groupMapper.updateGroupMember(members+","+username,gid,group.getMembersCount()+1);
    }


    public void updateGroup(Group group){ groupMapper.updateGroup(group); }

    public Group find(Integer gid){
        return groupMapper.find(gid);
    }

    public List<Group> findGroup(Integer gid,String groupName,String members,Integer offset,Integer limit){
        return groupMapper.findGroup(gid,groupName,members,offset,limit);
    }

    public boolean isGroupMember(int gid,String memberName){
        Group group = groupMapper.selectGroupById(gid);
        String m = group.getMembers();
        String[] members = m.split(",");
        for (String member : members) {
            if(member.equals(memberName)){
                return true;
            }
        }
        return false;
    }

    public boolean isGroupLeader(int gid,String userName){
        Group group = groupMapper.selectGroupById(gid);
        return group.getGroupLeader().equals(userName);
    }

    public int getGroupsCount(Integer gid,String groupName,String members){
        return groupMapper.selectCountOfGroup(gid,groupName,members);
    }

    public List<Group> selectPersonalGroup(String members, Integer offset, Integer limit){
        return groupMapper.selectPersonalGroup(members,offset,limit);
    }

    public Integer selectCountOfPersonalGroup(String members){
        return groupMapper.selectCountOfPersonalGroup(members);
    }
}
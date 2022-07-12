package com.zrgj519.campusBBS.service;

import com.zrgj519.campusBBS.dao.FileMapper;
import com.zrgj519.campusBBS.dao.GroupMapper;
import com.zrgj519.campusBBS.entity.Group;
import com.zrgj519.campusBBS.entity.GroupFile;
import com.zrgj519.campusBBS.entity.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupService {
    @Autowired
    private GroupMapper groupMapper;

    @Autowired
    private FileMapper fileMapper;

    public List<Group> getAll(){
        return groupMapper.selectAll();
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

    public List<GroupFile> getAllFiles(int gid){
        return fileMapper.selectAllFilesById(gid);
    }



    public int deleteGroup(Integer gid) {
        return groupMapper.deleteGroup(gid);
    }

    public void updateGroup(Group group){ groupMapper.updateGroup(group); }

    public Group find(Integer gid){
        return groupMapper.find(gid);
    }

    public List<Group> findGroup(Integer gid,String groupName,String members){
        return groupMapper.findGroup(gid,groupName,members);
    }

}
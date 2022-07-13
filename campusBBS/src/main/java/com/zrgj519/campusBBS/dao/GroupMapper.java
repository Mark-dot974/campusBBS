package com.zrgj519.campusBBS.dao;

import com.zrgj519.campusBBS.entity.Group;
import com.zrgj519.campusBBS.entity.GroupFile;
import com.zrgj519.campusBBS.entity.Post;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GroupMapper {
    List<Group> selectAll(int offset,int limit,String leaderName,String memberName);

    void insertGroup(Group group);

    Group selectGroupByName(String groupName);

    int selectGroupCount();

    Group selectGroupById(int gid);


    int deleteGroup(Integer gid);

    Group find(Integer gid);

    void updateGroup(Group group);

    List<Group> findGroup(Integer gid,String groupName,String members,Integer offset,Integer limit);

    int selectCountOfGroup(Integer gid,String groupName,String members);

    List<Group> selectPersonalGroup(String members,Integer offset,Integer limit);

    int selectCountOfPersonalGroup(String members);
}
package com.zrgj519.campusBBS.dao;

import com.zrgj519.campusBBS.entity.Group;
import com.zrgj519.campusBBS.entity.GroupFile;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GroupMapper {
    List<Group> selectAll(int offset,int limit,String leaderName,String memberName);

    void insertGroup(Group group);

    Group selectGroupByName(String groupName);

    int selectGroupCount();

    Group selectGroupById(int gid);
}

package com.zrgj519.campusBBS.dao;

import com.zrgj519.campusBBS.entity.GroupFile;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FileMapper {
    List<GroupFile> selectAllFilesById(int gid);

    void insertFile(GroupFile file);
}
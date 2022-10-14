package com.zrgj519.campusBBS.service;

import com.zrgj519.campusBBS.dao.FileMapper;
import com.zrgj519.campusBBS.entity.GroupFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileService {
    @Autowired
    private FileMapper fileMapper;

    public List<GroupFile> getAllFiles(int gid){
        return fileMapper.selectAllFilesById(gid);
    }

    public int filesCount(int gid){
        return this.getAllFiles(gid).size();
    }

    public GroupFile findFileByName(String fileName){
        return fileMapper.selectFileByFileName(fileName);
    }

    public void uploadFile(GroupFile file){
        fileMapper.insertFile(file);
    }

    public void updateFile(int fid,String url){
        fileMapper.updateFile(fid,url);
    }
}

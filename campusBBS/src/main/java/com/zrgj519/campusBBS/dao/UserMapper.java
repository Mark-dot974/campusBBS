package com.zrgj519.campusBBS.dao;

import com.zrgj519.campusBBS.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

// 使用springboot进行开发必须要使用Mapper表示该接口，因为Mapper注解表示Mybatis，而Repository表示Spring
@Mapper
public interface UserMapper {
    User selectById(int id);

    User selectByName(String username);

    User selectByEmail(String email);

    int insertUser(User user);

    int updateStatus(int id, int status);

    int updateHeader(int id, String headerUrl);

    int updatePassword(int id, String password);

//   展示所有用户
    List<User> showUser();

    int deleteUser(int id);

//   根据id寻找用户信息
    User find(int id);

//    修改用户信息
    void updateUser(User user);

    List<User> findUser(Integer id,String username,String email,Integer offset,Integer limit);

    Integer selectUsersCount(Integer id,String username,String email);

}

package com.zrgj519.campusBBS;

import com.zrgj519.campusBBS.dao.UserMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CampusBbsApplication.class)
public class testDao {
    @Autowired
    UserMapper userMapper;

    @Test
    public void testSelect(){
        System.out.println(userMapper.selectByName("mark"));
    }
}

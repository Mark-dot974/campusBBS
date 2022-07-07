package com.zrgj519.campusBBS.util;


import com.zrgj519.campusBBS.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserContainer {
    private ThreadLocal<User> users = new ThreadLocal<>();
    public void setUser(User user){
        users.set(user);
    }

    public User getUser(){
        return users.get();
    }

    public void clear(){
        users.remove();
    }
}

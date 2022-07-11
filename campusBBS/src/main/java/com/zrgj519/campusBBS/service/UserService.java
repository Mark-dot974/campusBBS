package com.zrgj519.campusBBS.service;

import com.zrgj519.campusBBS.dao.LoginTicketMapper;
import com.zrgj519.campusBBS.dao.UserMapper;
import com.zrgj519.campusBBS.entity.LoginTicket;
import com.zrgj519.campusBBS.entity.User;
import com.zrgj519.campusBBS.util.CampusBBSConstant;
import com.zrgj519.campusBBS.util.CampusBBSUtil;
import com.zrgj519.campusBBS.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    public User findUserById(int id){
        return userMapper.selectById(id);
    }

    public Map<String,Object> register(User user){
        Map<String,Object> map = new HashMap<>();
        // 空值处理
        if(user == null)
        {
            throw new IllegalArgumentException("参数不能为空！");
        }
        if(StringUtils.isBlank(user.getUsername()))
        {
            map.put("usernameMsg","账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空!");
            return map;
        }
        // 验证账号，分别根据账号、邮箱查找当前账号是否已经存在
        User u = userMapper.selectByName(user.getUsername());
        if(u!=null){
            map.put("usernameMsg","该账号已经存在！");
            return map;
        }
        u = userMapper.selectByEmail(user.getEmail());
        if(u!=null)
        {
            map.put("emailMsg","该邮箱已经被注册");
            return map;
        }

        // 开始注册
        user.setSalt(CampusBBSUtil.generateUUID().substring(0,5));
        user.setPassword(CampusBBSUtil.md5(user.getPassword()+user.getSalt()));
        user.setType(0);
        // 默认为1
        user.setStatus(1);
        user.setActivationCode(CampusBBSUtil.generateUUID());
        user.setHeaderUrl("http://rdivs98sy.hb-bkt.clouddn.com/72a246e5799249c19d4615a0d25f0b8f");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        // 发送激活邮件
        // 前端页面的上下文，用于存取变量
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/site/register_activation", context);
        mailClient.sendMail(user.getEmail(), "激活账号", content);
        return map;
    }

    public int activation(int userId, String code) {
        // 先根据userId查找注册的用户信息
        User user = userMapper.selectById(userId);
        if(user == null){
            return 0;
        }
        if (user.getStatus() == 1) {
            return CampusBBSConstant.ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)) {
            userMapper.updateStatus(userId, 1);
            return CampusBBSConstant.ACTIVATION_SUCCESS;
        } else {
            return CampusBBSConstant.ACTIVATION_FAILURE;
        }
    }
    public Map<String, Object> login(String username, String password, long expiredSeconds){
        // 验证客户端传递的信息是否可用
        Map<String,Object> map = new HashMap<>();
        if(StringUtils.isBlank(username)){
            map.put("usernameMsg","账号不能为空！");
            return map;
        }
        if(StringUtils.isBlank(password))
        {
            map.put("passwordMsg","密码不能为空！");
            return map;
        }

        // 验证账号
        User user = userMapper.selectByName(username);
        if(user==null){
            map.put("usernameMsg","该账号不存在！");
            return map;
        }

        // 验证状态(激活 or 未激活)
        if(user.getStatus()==0){
            map.put("usernameMsg","该账号未激活！");
            return map;
        }

        // 验证密码
        String encodedPassword = CampusBBSUtil.md5(password+user.getSalt());
        if(!encodedPassword.equals(user.getPassword())){
            map.put("passwordMsg","密码不正确！");
            return map;
        }

        // 验证通过，登录成功，记录登录状态
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setStatus(0);
        loginTicket.setTicket(CampusBBSUtil.generateUUID());
        loginTicket.setExpired(new Date(System.currentTimeMillis()+expiredSeconds*1000));
        loginTicketMapper.insertLoginTicket(loginTicket);
        map.put("ticket",loginTicket.getTicket());
        return map;
    }

    public LoginTicket findLoginTicketByTicket(String ticket){
        return loginTicketMapper.selectByTicket(ticket);
    }

    public List<User> showUser(){
        List<User> users = userMapper.showUser();
        return users;
    }

    // 获取用户的权限
    public Collection<? extends GrantedAuthority> getAuthorities(int userId){
        User user = this.findUserById(userId);

        List<GrantedAuthority> list = new ArrayList<>();
        list.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                switch (user.getType()){
                    case 1:
                        return CampusBBSConstant.AUTHORITY_ADMIN;
                    case 2:
                        return CampusBBSConstant.TEAM_LEADER;
                    default:
                        return CampusBBSConstant.AUTHORITY_USER;
                }
            }
        });
        return list;
    }

    public void logout(String ticket) {
        SecurityContextHolder.clearContext();
        loginTicketMapper.updateStatus(ticket,1);
    }

    public void updateHeader(int id, String url) {
        userMapper.updateHeader(id,url);
    }

    public User findUserByName(String userName) {
        return userMapper.selectByName(userName);
    }
}

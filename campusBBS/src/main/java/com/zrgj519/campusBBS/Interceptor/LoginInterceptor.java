package com.zrgj519.campusBBS.Interceptor;

import com.zrgj519.campusBBS.entity.LoginTicket;
import com.zrgj519.campusBBS.entity.User;
import com.zrgj519.campusBBS.service.UserService;
import com.zrgj519.campusBBS.util.UserContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    UserService userService;

    @Autowired
    UserContainer userContainer;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取到cookie中携带的登录ticket
        String ticket="";
        if(request!=null){
            Cookie[] cookies = request.getCookies();
            if(cookies!=null){
                for (Cookie cookie : cookies) {
                    if(cookie.getName().equals("ticket")){
                        ticket = cookie.getValue();
                    }
                }
            }
        }
        LoginTicket loginTicket = userService.findLoginTicketByTicket(ticket);
        // 0 --- 有效  1---用户退出登录
        if(loginTicket!=null && loginTicket.getStatus()==0){
            User user = userService.findUserById(loginTicket.getUserId());
            // 本次访问携带user
            userContainer.setUser(user);
            // 用户登录后获取到用户的权限，并且存入SecurityContext，以便于Security进行授权
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    user,user.getPassword(),userService.getAuthorities(user.getId()));
            SecurityContextHolder.setContext(new SecurityContextImpl(authentication));
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = userContainer.getUser();
        if(user!=null && modelAndView!=null){
            modelAndView.addObject("loginUser",user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        userContainer.clear();
        SecurityContextHolder.clearContext();
    }
}

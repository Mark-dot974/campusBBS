package com.zrgj519.campusBBS.Interceptor;

import com.zrgj519.campusBBS.annotation.GroupLeaderRequired;
import com.zrgj519.campusBBS.annotation.GroupMemberRequired;
import com.zrgj519.campusBBS.entity.User;
import com.zrgj519.campusBBS.service.GroupService;
import com.zrgj519.campusBBS.util.CampusBBSUtil;
import com.zrgj519.campusBBS.util.UserContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.lang.reflect.Method;

// 关于小组，因为协作圈众多，不能直接给用户赋予是否是组员组长的身份，因为你怎么知道他是那个组的？所以不能用springSecurity做权限控制
// 只能使用拦截器。
@Component
public class GroupInterceptor implements HandlerInterceptor {
    @Autowired
    private UserContainer userContainer;
    @Autowired
    private GroupService groupService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // // 判断拦截的是否是controller方法,然后根据反射获取到method对象，判断是否有注解
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            GroupMemberRequired groupMemberRequired = method.getAnnotation(GroupMemberRequired.class);
            GroupLeaderRequired groupLeaderRequired = method.getAnnotation(GroupLeaderRequired.class);
            User user = userContainer.getUser();
            if ((groupMemberRequired != null || groupLeaderRequired!=null) && user == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return false;
            }
            if (groupMemberRequired != null) {
                //  如果没有gid这个参数则为null
                String _gid = request.getParameter("gid");
                if (_gid == null) {
                    response.sendRedirect(request.getContextPath() + "/denied");
                    return false;
                }
                int gid = Integer.parseInt(_gid);
                boolean isMember = groupService.isGroupMember(gid, userContainer.getUser().getUsername());
                if (!isMember) {
                    String xRequestedWith = request.getHeader("x-requested-with");
                    if ("XMLHttpRequest".equals(xRequestedWith)) {
                        response.setContentType("application/plain;charset=utf-8");
                        PrintWriter writer = response.getWriter();
                        writer.write(CampusBBSUtil.getJSONString(403, "你没有访问此功能的权限!"));
                    } else {
                        response.sendRedirect(request.getContextPath() + "/denied");
                    }
                    return false;
                }
            }
            if (groupLeaderRequired != null) {
                //  如果没有gid这个参数则为null
                String _gid = request.getParameter("gid");
                if (_gid == null) {
                    response.sendRedirect(request.getContextPath() + "/denied");
                    return false;
                }
                int gid = Integer.parseInt(_gid);
                boolean isLeader = groupService.isGroupLeader(gid, userContainer.getUser().getUsername());
                if (!isLeader) {
                    String xRequestedWith = request.getHeader("x-requested-with");
                    if ("XMLHttpRequest".equals(xRequestedWith)) {
                        response.setContentType("application/plain;charset=utf-8");
                        PrintWriter writer = response.getWriter();
                        writer.write(CampusBBSUtil.getJSONString(403, "你没有访问此功能的权限!"));
                    } else {
                        response.sendRedirect(request.getContextPath() + "/denied");
                    }
                    return false;
                }
            }
        }
        return true;
    }
}

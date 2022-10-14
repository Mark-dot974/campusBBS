package com.zrgj519.campusBBS.config;

import com.zrgj519.campusBBS.util.CampusBBSConstant;
import com.zrgj519.campusBBS.util.CampusBBSUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(WebSecurity web) throws Exception {
        // 不拦截resources目录下的内容
        web.ignoring().antMatchers("/resources/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers(
                        "/post/publish",
                        "/group/create",
                        "/group/apply",
                        "/group/operate",
                        "/group/invite",
                        "/group/uploadFile",
                        "/group/personalGroup",
                        "/user/*",
                        "/like",
                        "/comment/**",
                        "/message/**",
                        "/"
                )
                .hasAnyAuthority(
                        CampusBBSConstant.AUTHORITY_USER,
                        CampusBBSConstant.AUTHORITY_ADMIN
                )
//                .antMatchers(
//                        "/group/operate"
//                )
//                .hasAnyAuthority(
//                        CampusBBSConstant.TEAM_LEADER
//                )
//                .antMatchers(
//                        "/group/uploadFile",
//                        "/group/invite"
//                )
//                .hasAnyAuthority(
//                        CampusBBSConstant.TEAM_MEMBER
//                )
                .antMatchers(
                        "/admin/**"
                )
                .hasAnyAuthority(
                        CampusBBSConstant.AUTHORITY_ADMIN
                )
                .anyRequest().permitAll()
                .and().csrf().disable();

        // 验证失败时处理
        http.exceptionHandling()
                // 没有登录，根据请求类型进行处理（ajax？html）
                //AuthenticationEntryPoint 用来解决匿名用户访问无权限资源时的异常
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
                        String xRequestedWith = request.getHeader("x-requested-with");
                        if ("XMLHttpRequest".equals(xRequestedWith)) {
                            response.setContentType("application/plain;charset=utf-8");
                            PrintWriter writer = response.getWriter();
                            writer.write(CampusBBSUtil.getJSONString(403, "你还没有登录哦！"));
                        } else {
                            response.sendRedirect(request.getContextPath() + "/login");
                        }
                    }
                })
                // 权限不足
                //AccessDeineHandler 用来解决认证过的用户访问无权限资源时的异常
                .accessDeniedHandler(new AccessDeniedHandler() {
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
                        String xRequestedWith = request.getHeader("x-requested-with");
                        if ("XMLHttpRequest".equals(xRequestedWith)) {
                            response.setContentType("application/plain;charset=utf-8");
                            PrintWriter writer = response.getWriter();
                            writer.write(CampusBBSUtil.getJSONString(403, "你没有访问此功能的权限!"));
                        } else {
                            response.sendRedirect(request.getContextPath() + "/denied");
                        }
                    }
                });
        // Security底层默认会拦截/logout请求，进行退出处理
        // 如果是让springSecurity执行自己的退出逻辑，最终会直接返回，不会执行我们的代码
        // 故此处写一个不存在的路径给security处理，如此就会继续往后执行
        http.logout().logoutUrl("/securityLogout");
    }
}

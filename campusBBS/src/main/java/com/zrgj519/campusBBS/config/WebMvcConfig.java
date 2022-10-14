package com.zrgj519.campusBBS.config;

import com.zrgj519.campusBBS.Interceptor.GroupInterceptor;
import com.zrgj519.campusBBS.Interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


// 配置拦截器：拦截器用于统一操作，在执行被拦截操作之前执行的代码。
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    // 权限控制
    @Autowired
    private LoginInterceptor loginInterceptor;
    @Autowired
    private GroupInterceptor groupInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 静态资源不拦截，其他所有请求都拦截
        registry.addInterceptor(loginInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");

        registry.addInterceptor(groupInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg")
                .addPathPatterns("/group/**");
    }
}

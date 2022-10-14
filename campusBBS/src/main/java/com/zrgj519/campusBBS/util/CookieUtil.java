package com.zrgj519.campusBBS.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class CookieUtil {
    public static String getValue(HttpServletRequest request,String name)
    {
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(5);

        if(request == null || name == null)
        {
            throw new IllegalArgumentException("参数为空！");
        }
        Cookie [] cookies = request.getCookies();
        if(cookies!=null){
            for (Cookie cookie : cookies) {
                if(cookie.getName().equals(name))
                {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}

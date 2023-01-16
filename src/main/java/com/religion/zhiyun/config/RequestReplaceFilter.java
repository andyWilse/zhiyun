package com.religion.zhiyun.config;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Description:
 * @PackageName: net.dlet.dhdemo.configure
 * @Name: RequestReplaceFilter
 * @Author: cure
 * @CreateDate: 2020/09/06 4:58
 * @ModifyUser:
 * @ModifyDate:
 * @ModifyDesc: 修改内容
 * @DayNameFull: 星期日
 * @ProjectName: dhdemo
 * @Version: 1.0
 **/
@Component
public class RequestReplaceFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (!(request instanceof MyRequestWrapper)) {
            //request = new MyRequestWrapper(request);

        }
        filterChain.doFilter(request, response);
    }
}


package com.louis.springbootinit.config;

import com.louis.springbootinit.utils.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @author louis
 * @version 1.0
 * @date 2023/12/10 20:25
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加登录拦截器
        registry.addInterceptor(new LoginInterceptor())
                .excludePathPatterns(
                        // 选择不需要拦截的请求路径
                        "/patient/login",
                        "/patient/register",
                        "/voucher/**",
                        "/blog/code",
                        "/upload/**",
                        "/shop/**",
                        "/shop-type/**"
                ).order(0);
    }

}
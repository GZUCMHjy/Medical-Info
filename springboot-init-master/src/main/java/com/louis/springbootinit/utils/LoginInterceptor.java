package com.louis.springbootinit.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.louis.springbootinit.model.dto.patient.PatientDto;
import com.louis.springbootinit.model.entity.Patient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author louis
 * @version 1.0
 * @date 2023/12/10 20:09
 */
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {
    // 无法注入 因为不属于Spring自带的对象

    public LoginInterceptor (){
    }
    // 这里解释一下 为什么 实现HandlerInterceptor接口 不是全部实现
    // 按理来说 实现接口，必须实现接口里面所有的方法，这里只要实现preHandle和afterCompletion
    // 是因为HandlerInterceptor 里面的方法是默认实现方法了 所以我们就可以自行选择方法进行重写override 不选则默认它规定好的方法执行
    // 所以接口的方法不一定是要全部重写，要看它是否是接口默认的抽象方法，而不是已经实现好的方法！
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 获取前端请求头的token
        String token = request.getHeader("token");
        if(StrUtil.isEmpty(token)){
            response.setStatus(401);
            return false;
        }
        // 2. 基于token获取session中用户
        String key = "user_login_" + token;
        Patient patient = (Patient)request.getSession().getAttribute(key);

        // session过期或者删除
        if(patient == null){
            response.setStatus(401);
            return false;
        }
        PatientDto patientDto = BeanUtil.copyProperties(patient, PatientDto.class);
        PatientHolder.savePatient(patientDto);
        // 3. 手动刷新token有效期
        int sessionTimeoutInSeconds = 30 * 60;
        request.getSession().setMaxInactiveInterval(sessionTimeoutInSeconds);
        // 4. 放行
        return true;

    }
}
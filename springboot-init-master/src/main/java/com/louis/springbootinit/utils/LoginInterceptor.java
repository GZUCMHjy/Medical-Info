package com.louis.springbootinit.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.louis.springbootinit.model.dto.doctor.DoctorDto;
import com.louis.springbootinit.model.dto.patient.PatientDto;
import com.louis.springbootinit.model.entity.Doctor;
import com.louis.springbootinit.model.entity.Patient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Optional;

import static com.louis.springbootinit.constant.CommonConstant.USER_LOGIN_KEY;

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
        // String token = request.getHeader("token");
        // 2. 基于token获取session中用户
        Optional<Patient> patientOptional = null;
        Optional<Doctor> doctorOptional = null;
        try{
            patientOptional = Optional.ofNullable((Patient)request.getSession().getAttribute(USER_LOGIN_KEY));
            Patient patient = patientOptional.get();
            PatientDto patientDto = BeanUtil.copyProperties(patient, PatientDto.class);
            UserHolder.saveUser(patientDto);
        }catch (Exception e){
            doctorOptional = Optional.ofNullable((Doctor)request.getSession().getAttribute(USER_LOGIN_KEY));
            Doctor doctor = doctorOptional.get();
            DoctorDto doctorDto = BeanUtil.copyProperties(doctor, DoctorDto.class);
            UserHolder.saveUser(doctorDto);
        }finally {
            // 最后都要检查一下
            if(!patientOptional.isPresent() && !doctorOptional.isPresent()){
                // session过期
                response.setStatus(401);
                return false;
            }
        }
        // 3. 手动刷新token有效期
        int sessionTimeoutInSeconds = 30 * 60;
        request.getSession().setMaxInactiveInterval(sessionTimeoutInSeconds);
        // 4. 放行
        return true;

    }
}
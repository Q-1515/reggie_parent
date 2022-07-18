package com.reggie.Interceptor;

import com.reggie.annotation.IgnoreToken;
import com.reggie.properties.JwtProperties;
import com.reggie.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 统一进行jwt令牌的校验
 */
@Component
@Slf4j
public class JwtTokenAdminInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * jwt令牌的校验
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("拦截到请求：{}", request.getRequestURI());

        if (!(handler instanceof HandlerMethod)) {
            //如果不是映射到controller某个方法的请求，则直接放行，例如请求的是/doc.html
            return true;
        }

        //获取请求的方法
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        //判断当前被拦截的Controller方法上是否加入了IgonreToken注解
        boolean methodAnnotation = handlerMethod.hasMethodAnnotation(IgnoreToken.class);
        if (methodAnnotation) {
            return true;
        }


        //从请求头获取jwt令牌
        String token = request.getHeader(jwtProperties.getAdminTokenName());

        try {
            Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), token);
        } catch (Exception e) {
            log.error("jwt令牌解析失败");
            //401代表用户没有访问权限，需要进行身份认证
            response.setStatus(401);
            return false;
        }
        return true;
    }
}

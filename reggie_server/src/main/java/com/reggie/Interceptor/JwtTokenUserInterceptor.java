package com.reggie.Interceptor;

import com.reggie.annotation.IgnoreToken;
import com.reggie.constant.JwtClaimsConstant;
import com.reggie.context.BaseContext;
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
 * 统一进行jwt校验
 */
@Component
@Slf4j
public class JwtTokenUserInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 客户端令牌校验
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("拦截到了请求:{}", request.getRequestURI());

        if (!(handler instanceof HandlerMethod)) {
            //如果不是映射到controller某个方法的请求，则直接放行，例如请求的是/doc.html
            return true;
        }

        //判断当前被拦截的Controller方法上是否加入了IgonreToken注解
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        //加入了IgnoreToken注解，直接放行
        boolean hasMethodAnnotation = handlerMethod.hasMethodAnnotation(IgnoreToken.class);
        if (hasMethodAnnotation) {
            return true;
        }

        //从请求头获取jwt令牌
        String jwt = request.getHeader(jwtProperties.getUserTokenName());

        try {
            //解析jwt令牌
            Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), jwt);
            //获取当前登录用户的id
            Long userId = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
            // 保存当前登录用户的id到线程本地变量
            BaseContext.setCurrentId(userId);
        } catch (Exception e) {
            log.info("令牌解析失败");
            //401代表用户没有访问权限，需要进行身份认证
            response.setStatus(401);
            return false;
        }
        return true;
    }
}

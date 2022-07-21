package com.reggie.aspect;


import com.reggie.annotation.AutoFill;
import com.reggie.constant.AutoFillConstant;
import com.reggie.context.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/*
 *公共字段自动填充  例如 CreateTime
 *                   UpdateTime
 *                   CreateUser
 *                   UpdateUser
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    /**
     * 定义切入点
     */
    @Pointcut("execution(* com.reggie.mapper.*.*(..)) && @annotation(com.reggie.annotation.AutoFill)")
    public void autoFillPointcut() {
    }


    /**
     * 前置增强填充字段
     * @param joinPoint
     */
    @Before("autoFillPointcut()")
    public void autoFillAdvice(JoinPoint joinPoint) {
        log.info("公共字段自动填充...");


        //获取当前时间、当前登录用户id
        LocalDateTime nowTime = LocalDateTime.now();
        Long empId = BaseContext.getCurrentId();

        //获取当前要调用的对象
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return;
        }

        //操作的实体对象
        Object entity = args[0];

        //获取要调用的方法对象
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        AutoFill autoFill = method.getAnnotation(AutoFill.class);
        //获取autoFill注解type上的字符
        String type = autoFill.type();

        //判断是否是insert
        if (type.equals(AutoFillConstant.INSERT)) {
            try {
                //获取要调用的set方法
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);


                //运行方法填充数值
                setCreateTime.invoke(entity, nowTime);
                setUpdateTime.invoke(entity, nowTime);
                setCreateUser.invoke(entity, empId);
                setUpdateUser.invoke(entity, empId);

            } catch (Exception e) {
                log.info("公共字段填充失败");
                throw new RuntimeException(e);
            }
        } else {//否侧运行判断是否是update
            try {

                //获取要调用的set方法
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                //运行方法填充数值
                setUpdateTime.invoke(entity, nowTime);
                setUpdateUser.invoke(entity, empId);
            } catch (Exception e) {
                log.info("公共字段填充失败");
                throw new RuntimeException(e);
            }
        }
    }
}

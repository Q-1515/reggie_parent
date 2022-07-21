package com.reggie.handler;


import com.reggie.exception.BaseException;
import com.reggie.result.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;


/**
 * 全局异常处理
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获所有的异常
     *
     * @param ex 异常对象
     * @return 异常信息
     */
    @ExceptionHandler
    public R<String> exceptionHandler(BaseException ex) {
        log.info("捕获到异常：{}", ex.getMessage());
        return R.error(ex.getMessage());
    }


    /**
     * 捕获SQL异常
     *
     * @param ex 异常对象
     * @return 异常信息
     */
    @ExceptionHandler
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex) {
        log.info("捕获到SQL异常：{}", ex.getMessage());

        //Duplicate entry 'xiaohua' for key 'idx_username'
        if (ex.getMessage().contains("Duplicate entry")) {
            //获取重复员工账号 'xiaohua'
            String s = ex.getMessage().split(" ")[2];
            return R.error(s + "已存在");

        }
        return R.error("未知异常");
    }
}

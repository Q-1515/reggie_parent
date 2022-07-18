package com.reggie.handler;


import com.reggie.exception.BaseException;
import com.reggie.result.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * 全局异常处理
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获所有的异常
     * @param ex 异常对象
     * @return 异常信息
     */
    @ExceptionHandler
    public R exceptionHandler(BaseException ex) {
        log.info("捕获到异常：{}", ex.getMessage());
        return R.error(ex.getMessage());

    }
}

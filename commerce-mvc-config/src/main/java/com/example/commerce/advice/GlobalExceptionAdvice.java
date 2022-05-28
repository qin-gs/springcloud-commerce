package com.example.commerce.advice;

import cn.hutool.http.server.HttpServerRequest;
import com.example.commerce.vo.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionAdvice {

    @ExceptionHandler(value = Exception.class)
    public CommonResponse<String> handlerCommerceException(HttpServerRequest request, Exception ex) {
        CommonResponse<String> response = new CommonResponse<>(-1, "business error");

        response.setData(ex.getMessage());
        log.error("commerce service error {}", ex.getMessage(), ex);
        return response;
    }
}

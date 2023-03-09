package com.xxd.platform.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/*
*
* gloabal exception handle
* */
@ControllerAdvice(annotations = {RestController.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandle {
    /*
    * handle exception for sql
    * */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandle(SQLIntegrityConstraintViolationException ex){
        log.error(ex.getMessage());
        if(ex.getMessage().contains("Duplicate entry")){
            String[] split = ex.getMessage().split(" ");
            String msg = split[2];
            return R.error(msg);
        }



        return R.error("unknowen error");
    }
    @ExceptionHandler(CustomExceptionHandle.class)
    public R<String> exceptionHandle(CustomExceptionHandle ex){
        log.info(ex.getMessage());
        return R.error(ex.getMessage());
    }
}

package com.klimovich.myRestService.controller;

import com.klimovich.myRestService.entity.MyResponse;
import com.klimovich.myRestService.entity.MyResponseStatus;
import com.klimovich.myRestService.exception.NotFoundSolutionException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AdviceController {
    private static final Logger logger = LoggerFactory.getLogger(AdviceController.class);

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public MyResponse handleException(@NotNull Exception e) {
        logger.error("Bad Request Exception");
        MyResponse response = new MyResponse(new MyResponseStatus(400, HttpStatus.BAD_REQUEST));
        response.getResponseStatus().addMessage(e.getMessage());
        return response;
    }

    @ExceptionHandler(NotFoundSolutionException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public MyResponse handleNotFoundSolution(@NotNull NotFoundSolutionException e) {
        logger.error("Not Found Solution Exception");
        MyResponse response = new MyResponse(new MyResponseStatus(404, HttpStatus.NOT_FOUND));
        response.getResponseStatus().addMessage(e.getMessage());
        return response;
    }
}

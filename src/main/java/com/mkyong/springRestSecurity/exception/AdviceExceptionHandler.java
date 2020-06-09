package com.mkyong.springRestSecurity.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ControllerAdvice
public class AdviceExceptionHandler {

    @ExceptionHandler(BookNotFoundException.class)
    public void springHandleNotFound(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.NOT_FOUND.value());
    }

//    customize the entire JSON error response
//    @ExceptionHandler(BookNotFoundException.class)
//    public ResponseEntity<CustomErrorResponse> customHandleNotFound(Exception ex, WebRequest request){
//        CustomErrorResponse errorResponse = new CustomErrorResponse();
//        errorResponse.setTimestamp(LocalDateTime.now());
//        errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
//        errorResponse.setError(ex.getMessage());
//        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
//    }

    @ExceptionHandler(BookUnSupportedFieldPatchException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public String springUnSupportedFieldPatch(BookUnSupportedFieldPatchException ex){
        return ex.getMessage();
    }

}

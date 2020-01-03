package com.bitcoin.card.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // Let Spring handle the exception, we just override the status code
	@ExceptionHandler(UserNotFoundException.class)
    public void springHandleNotFound(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.NOT_FOUND.value());
    }
	
    // Let Spring handle the exception, we just override the status code
	@ExceptionHandler(SQLException.class)
    public void springHandleSQLError(HttpServletResponse response, SQLException sqlE) throws IOException {
		
        response.sendError(HttpStatus.BAD_REQUEST.value(), sqlE.getLocalizedMessage());
       
    }
    



}

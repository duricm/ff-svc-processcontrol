package com.futurefoundation.processcontrol.error;

import org.springframework.http.HttpStatus;


import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

import org.apache.log4j.Logger;

@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {
	
	private final Logger LOGGER = Logger.getLogger(this.getClass());

    // Let Spring handle the exception, we just override the status code
	@ExceptionHandler(UserNotFoundException.class)
    public void springHandleNotFound(HttpServletResponse response) throws IOException {
		
		LOGGER.error("Error!!! Responding with not found message.");
		
        response.sendError(HttpStatus.NOT_FOUND.value());
    }
	
    // Let Spring handle the exception, we just override the status code
	@ExceptionHandler(SQLException.class)
    public void springHandleSQLError(HttpServletResponse response, SQLException sqlE) throws IOException {
		
		LOGGER.error("Error!!! Responding with bad request message.");
		
		String message = "";

			
		
        response.sendError(HttpStatus.BAD_REQUEST.value(), message);
       
    }
	
    // Let Spring handle the exception, we just override the status code
	@ExceptionHandler(UnauthorizedException.class)
    public void springHandleBadToken(HttpServletResponse response) throws IOException {
		
		LOGGER.error("Error!!! Responding with unauthorized user message.");
		
        response.sendError(HttpStatus.UNAUTHORIZED.value());
       
    }
	
    // Let Spring handle the exception, we just override the status code
	@ExceptionHandler(BadRequestException.class)
    public void springHandleBadRequest(HttpServletResponse response) throws IOException {
		
		LOGGER.error("Bad request!");
		
        response.sendError(HttpStatus.BAD_REQUEST.value());
        
       
    }
	
    // Let Spring handle the exception, we just override the status code
	@ExceptionHandler(WrongFileTypeException.class)
    public void springHandleWrongFileType(HttpServletResponse response) throws IOException {
		
		LOGGER.error("Error!!! Wrong file type. ");
		
        response.sendError(HttpStatus.BAD_REQUEST.value());
       
    }
    



}

package com.richitec.http.controller;

import java.io.IOException;
import javax.servlet.sip.ServletParseException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

public class ErrorController {

	@ExceptionHandler(IOException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public @ResponseBody String handleIOException(IOException e){
		return e.getMessage();
	}
	
	@ExceptionHandler(ServletParseException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public @ResponseBody String handleServletException(ServletParseException e){
		return e.getMessage();
	}
}

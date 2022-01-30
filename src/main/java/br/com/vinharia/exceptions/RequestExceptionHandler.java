package br.com.vinharia.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RequestExceptionHandler {

	@ExceptionHandler(value = {RequestException.class})
	public ResponseEntity<Object> handleException(RequestException e) {
		return new ResponseEntity<Object>(e.getMessage(), e.getStatus());
	}
	
}
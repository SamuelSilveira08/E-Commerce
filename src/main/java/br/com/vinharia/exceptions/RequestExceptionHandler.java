package br.com.vinharia.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RequestExceptionHandler {

	@ExceptionHandler(value = {RequestException.class})
	public ResponseEntity<Object> handleException(RequestException e) {
		ErrorResponse res = new ErrorResponse();
		res.setMessage(e.getMessage());
		res.setStatus(e.getStatus());
		return new ResponseEntity<>(res, e.getStatus());
	}
	
}
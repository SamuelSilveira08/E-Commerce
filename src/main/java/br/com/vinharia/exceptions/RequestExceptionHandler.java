package br.com.vinharia.exceptions;

import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import io.jsonwebtoken.ExpiredJwtException;

@RestControllerAdvice
public class RequestExceptionHandler {

	@ExceptionHandler(value = { RequestException.class })
	public ResponseEntity<Object> handleException(RequestException e) {
		ErrorResponse res = new ErrorResponse();
		res.setMessage(e.getMessage());
		res.setStatus(e.getStatus());
		return new ResponseEntity<>(res, e.getStatus());
	}

	@ExceptionHandler(value = { HttpClientErrorException.class })
	public ResponseEntity<Object> handleRuntimeException(HttpClientErrorException e) {
		ErrorResponse res = new ErrorResponse();
		res.setMessage(e.getMessage());
		res.setStatus(e.getStatusCode());
		return new ResponseEntity<>(res, res.getStatus());
	}

	@ExceptionHandler(value = { BadCredentialsException.class })
	public ResponseEntity<Object> handleBadCrendentialsException(BadCredentialsException e) {
		ErrorResponse res = new ErrorResponse();
		res.setMessage(e.getMessage());
		res.setStatus(HttpStatus.UNAUTHORIZED);
		return new ResponseEntity<>(res, res.getStatus());
	}
	
	@ExceptionHandler(value = { AccessDeniedException.class })
	public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException e) {
		ErrorResponse res = new ErrorResponse();
		res.setMessage(e.getMessage());
		res.setStatus(HttpStatus.FORBIDDEN);
		return new ResponseEntity<>(res, res.getStatus());
	}

	@ExceptionHandler(value = { NullPointerException.class })
	public ResponseEntity<Object> handleInternalServerErrorException(NullPointerException e) {
		ErrorResponse res = new ErrorResponse();
		res.setMessage(e.getMessage());
		res.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
		res.setStackTrace(e.getStackTrace());
		return new ResponseEntity<>(res, res.getStatus());
	}
	
	@ExceptionHandler(value = { MethodArgumentNotValidException.class, ConstraintViolationException.class })
	public ResponseEntity<Object> handleBadCrendentialsException(Exception e) {
		ErrorResponse res = new ErrorResponse();
		res.setMessage(e.getMessage());
		res.setStatus(HttpStatus.BAD_REQUEST);
		return new ResponseEntity<>(res, res.getStatus());
	}
	
	@ExceptionHandler(value = { ExpiredJwtException.class })
	public ResponseEntity<Object> handleExpiredJwtException(ExpiredJwtException e) {
		ErrorResponse res = new ErrorResponse();
		res.setMessage(e.getMessage());
		res.setStatus(HttpStatus.UNAUTHORIZED);
		return new ResponseEntity<>(res, res.getStatus());
	}
	
	@ExceptionHandler(value = { HttpMessageNotReadableException.class })
	public ResponseEntity<Object> handleConstraintViolationException(HttpMessageNotReadableException e) {
		ErrorResponse res = new ErrorResponse();
		res.setMessage(e.getMessage());
		res.setStatus(HttpStatus.BAD_REQUEST);
		return new ResponseEntity<>(res, res.getStatus());
	}

	@ExceptionHandler(value = { Exception.class })
	public ResponseEntity<Object> handleGenericException(Exception e) {
		ErrorResponse res = new ErrorResponse();
		res.setMessage(e.getMessage());
		res.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
		res.setStackTrace(e.getStackTrace());
		return new ResponseEntity<Object>(res, res.getStatus());
	}

}
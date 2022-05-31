package br.com.vinharia.exceptions;

import org.springframework.http.HttpStatus;

public class BadRequestException extends RequestException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public BadRequestException(String message) {
		super(message, HttpStatus.BAD_REQUEST);
	}

}

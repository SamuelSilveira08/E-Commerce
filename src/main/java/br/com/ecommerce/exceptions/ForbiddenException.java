package br.com.ecommerce.exceptions;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends RequestException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ForbiddenException(String message) {
		super(message, HttpStatus.FORBIDDEN);
	}

}

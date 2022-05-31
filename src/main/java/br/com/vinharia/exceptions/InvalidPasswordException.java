package br.com.vinharia.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidPasswordException extends RequestException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5684907552030689874L;

	public InvalidPasswordException(String message) {
		super(message, HttpStatus.FORBIDDEN);
	}

}

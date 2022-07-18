package br.com.ecommerce.exceptions;

import org.springframework.http.HttpStatus;

public class UsernameAlreadyExistsException extends RequestException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6762242556343111863L;
	
	public UsernameAlreadyExistsException(String message) {
		super(message, HttpStatus.CONFLICT);
	}

}

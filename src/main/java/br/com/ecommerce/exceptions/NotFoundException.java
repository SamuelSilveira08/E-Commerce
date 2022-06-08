package br.com.ecommerce.exceptions;

import org.springframework.http.HttpStatus;

public class NotFoundException extends RequestException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -410861927513574991L;

	public NotFoundException(String message) {
		super(message, HttpStatus.NOT_FOUND);
	}

}

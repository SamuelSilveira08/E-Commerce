package br.com.vinharia.exceptions;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class RequestException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 654384140973248803L;

	private final String message;
	private final HttpStatus status;

	public RequestException(String message, HttpStatus status) {
		super(message);
		this.status = status;
		this.message = message;
	}

}

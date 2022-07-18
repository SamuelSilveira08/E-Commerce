package br.com.ecommerce.service;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtService {

	private static final String SIGNING_KEY = "2VEYgS9YL5eb7Bgvd9TsR6eaADT1ltIi";
	private static final String REFRESH_TOKEN_SIGNING_KEY = "sl6yPLmsjEryNxLKj1h00tjLXx5ufl7n";
	private static final int EXPIRATION_TIME = (int) TimeUnit.DAYS.toSeconds(3);
	private static final int REFRESH_TOKEN_EXPIRATION_TIME = (int) TimeUnit.DAYS.toSeconds(3);

	
	/**
	 * Generate the access token of the application
	 * 
	 * @author Samuel
	 * 
	 * @param authentication the data of user requiring authentication
	 * @return the JWT generated for the giving user
	 */
	
	public String generateToken(Authentication authentication) {
		return generateToken(SIGNING_KEY, authentication.getName(), EXPIRATION_TIME);
	}
	
	/**
	 * Generate the refresh token
	 * 
	 * @author Samuel
	 * 
	 * @param username The email (username) of the user requiring the refresh token
	 * @return the JWT that will be used to refresh the access token
	 */

	public String generateRefreshToken(String username) {
		return generateToken(REFRESH_TOKEN_SIGNING_KEY, username, REFRESH_TOKEN_EXPIRATION_TIME);
	}
	
	/**
	 * Extract expiration time out of a JWT
	 * 
	 * @author Samuel
	 * 
	 * @param token JWT
	 * @return expiration time (Date)
	 */

	public Date getExpirationTime(String token) {
		Claims claims = getClaims(token, SIGNING_KEY);
		return claims.getExpiration();
	}
	
	/**
	 * Extract username out of a JWT
	 * 
	 * @author Samuel
	 * 
	 * @param token JWT
	 * @return username of the authenticated user
	 */

	public String getUsernameFromToken(String token) {
		Claims claims = getClaims(token, SIGNING_KEY);
		return claims.getSubject();
	}
	
	/**
	 * Extract username out of a refresh token
	 * 
	 * @author Samuel
	 * 
	 * @param token JWT
	 * @return username of the user which refresh token belongs to
	 */

	public String getUsernameFromRefreshToken(String token) {
		Claims claims = getClaims(token, REFRESH_TOKEN_SIGNING_KEY);
		return claims.getSubject();
	}
	
	/**
	 * Decode and extract data of an user out of a JWT
	 * 
	 * @author Samuel
	 * 
	 * @param token JWT
	 * @param signingKey a String used to encode the JWT
	 * @return the Claims (data) of a JWT
	 */

	private Claims getClaims(String token, String signingKey) {
		return Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token).getBody();
	}
	
	/** 
	 * Method that encapsulates the logic to generated a JWT
	 * 
	 * @author Samuel
	 * 
	 * @param signingKey String used to encode the JWT
	 * @param subject the username (email) of the user to generate JWT for
	 * @param expirationTime how much the token will remain valid
	 * @return a String corresponding to the generated JWT
	 */

	private String generateToken(String signingKey, String subject, int expirationTime) {
		Map<String, Object> claims = new HashMap<>();

		Instant currentDate = Instant.now();
		Instant expiresAt = currentDate.plusSeconds(expirationTime);

		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(currentDate.toEpochMilli()))
				.setExpiration(new Date(expiresAt.toEpochMilli())).signWith(SignatureAlgorithm.HS512, signingKey)
				.compact();
	}

}

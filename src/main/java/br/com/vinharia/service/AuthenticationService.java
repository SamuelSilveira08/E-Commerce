package br.com.vinharia.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.vinharia.domain.User;
import br.com.vinharia.domain.UserPrincipal;
import br.com.vinharia.dto.JwtResponse;
import br.com.vinharia.dto.RefreshTokenRequest;
import br.com.vinharia.dto.UserDTO;
import br.com.vinharia.repositories.UserRepository;

@Service
public class AuthenticationService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtService jwtService;

	// Constructor injection with @Lazy because of circle dependencies
	// (AuthenticationService depends on AuthenticationManager bean declared in
	// SecurityConfig and SecurityConfig depends on AuthenticationService)

	public AuthenticationService(@Lazy AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("Username %s not found".formatted(email)));
		return new UserPrincipal(user);
	}

	/**
	 * Authenticate an user in the application for the first time
	 * 
	 * @param userDTO containing user's credentials (email and password)
	 * @return a {@link JwtResponse} containing the JWT, the type of the token, the
	 *         expiration date and the refresh token
	 */

	public JwtResponse authenticateUser(UserDTO userDTO) {
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDTO.getEmail(),
				userDTO.getPassword());
		Authentication authenticatedUser = authenticationManager.authenticate(authentication);

		return createJwtResponse(authenticatedUser);
	}

	// If I don't need to send Authorization header to refresh JWT, how is the
	// authentication made in the method below?
	// R: Since the method above is ALWAYS called before the method below, and
	// inside it we pass username and password to authenticate,
	// if authentication is successful, then this username and password are from a
	// valid principal/user, and both access token and
	// refresh token are generated using the same username, which already has been
	// validated (I mean, password is correct), so there's
	// no need to authenticate it again in the method below.

	/**
	 * Authenticate an user through their refresh token
	 * 
	 * @param refreshToken
	 * @return a {@link JwtResponse} containing the JWT, the type of the token, the
	 *         expiration date and the refresh token
	 */

	public JwtResponse authenticateUser(RefreshTokenRequest refreshToken) {
		String username = jwtService.getUsernameFromRefreshToken(refreshToken.getRefreshToken());
		String password = loadUserByUsername(username).getPassword();

		Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
		return createJwtResponse(authentication);
	}

	/**
	 * Private method responsible for creating the JwtResponse
	 * 
	 * @param authenticatedUser authenticated user or user requiring authentication
	 * @return a {@link JwtResponse} containing the JWT, the type of the token, the
	 *         expiration date and the refresh token
	 */

	private JwtResponse createJwtResponse(Authentication authenticatedUser) {
		String token = jwtService.generateToken(authenticatedUser);
		Date expiresAt = jwtService.getExpirationTime(token);
		String refreshToken = jwtService.generateRefreshToken(authenticatedUser.getName());

		return new JwtResponse(token, "Bearer", expiresAt, refreshToken);
	}

}

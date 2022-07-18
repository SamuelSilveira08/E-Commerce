package br.com.ecommerce.controllers;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.ecommerce.domain.UserPrincipal;
import br.com.ecommerce.dto.ChangePasswordDTO;
import br.com.ecommerce.dto.JwtResponse;
import br.com.ecommerce.dto.RefreshTokenRequest;
import br.com.ecommerce.dto.UserDTO;
import br.com.ecommerce.service.AuthenticationService;
import br.com.ecommerce.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private AuthenticationService authService;

	/**
	 * 
	 * @param pageNumber
	 * @param numberItems
	 * @param sortBy
	 * @return {@link UserService#getUsers(Integer, Integer, String)}
	 * @throws AccessDeniedException if authenticated user is not an admin
	 */
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	@PreAuthorize(value = "hasRole('ADMIN')")
	public List<UserDTO> getAllUsers(@RequestParam(defaultValue = "0") Integer pageNum,
			@RequestParam(defaultValue = "10") Integer numberItems, @RequestParam(defaultValue = "id") String sortBy) {
		return userService.getUsers(pageNum, numberItems, sortBy);
	}

	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public UserDTO getUser(@PathVariable Integer id, @AuthenticationPrincipal UserPrincipal authenticatedUser) {
		return userService.getUser(id, authenticatedUser);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public UserDTO saveUser(@RequestBody @Valid UserDTO userDTO) {
		return userService.saveUser(userDTO);
	}

	@PostMapping("/auth/login")
	@ResponseStatus(HttpStatus.OK)
	public JwtResponse jwtAuth(@RequestBody UserDTO userDTO) {
		return authService.authenticateUser(userDTO);
	}

	@PostMapping("/auth/refresh")
	@ResponseStatus(HttpStatus.OK)
	public JwtResponse jwtRefreshToken(@RequestBody RefreshTokenRequest refreshToken) {
		return authService.authenticateUser(refreshToken);
	}

	@PutMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public UserDTO updateUser(@RequestBody @Valid UserDTO userDTO, @PathVariable Integer id,
			@AuthenticationPrincipal UserPrincipal authenticatedUser) {
		return userService.updateUser(userDTO, id, authenticatedUser);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public void deleteUser(@PathVariable Integer id, @AuthenticationPrincipal UserPrincipal authenticatedUser) {
		userService.deleteUser(id, authenticatedUser);
	}

	@PutMapping("/change-password")
	@ResponseStatus(HttpStatus.OK)
	public void changePassword(@RequestBody ChangePasswordDTO passwordDTO,
			@AuthenticationPrincipal UserPrincipal authenticatedUser) {
		userService.changePassword(passwordDTO, authenticatedUser);
	}
}

package br.com.vinharia.controllers;

import java.util.List;

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

import br.com.vinharia.domain.UserPrincipal;
import br.com.vinharia.dto.CartDTO;
import br.com.vinharia.service.CartService;

@RestController
@RequestMapping("/carts")
public class CartController {

	@Autowired
	private CartService cartService;
	
	/**
	 * 
	 * @param pageNumber
	 * @param numberItems
	 * @param sortBy
	 * @return {@link CartService#getAllCarts(Integer, Integer, String)}
	 * @throws AccessDeniedException if authenticated user is not an admin
	 */
	
	// TODO change name of the project
	
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	@PreAuthorize(value = "hasRole('ADMIN')")
	public List<CartDTO> getAllCarts(@RequestParam(defaultValue = "0") Integer pageNumber,
			@RequestParam(defaultValue = "10") Integer numberItems, @RequestParam(defaultValue = "id") String sortBy) {
		return cartService.getAllCarts(pageNumber, numberItems, sortBy);
	}

	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public CartDTO getCartById(@PathVariable Integer id, @AuthenticationPrincipal UserPrincipal user) {
		return cartService.getCartById(id, user);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CartDTO saveCart(@RequestBody CartDTO cartDTO, @AuthenticationPrincipal UserPrincipal user) {
		return cartService.saveCart(cartDTO, user);
	}

	@PutMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public CartDTO updateCart(@RequestBody CartDTO cartDTO, @PathVariable Integer id, @AuthenticationPrincipal UserPrincipal user) {
		return cartService.updateCart(cartDTO, id, user);
	}

	/**
	 * 
	 * @param id
	 * @throws AccessDeniedException if authenticated user is not an admin
	 */
	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	@PreAuthorize(value = "hasRole('ADMIN')")
	public void deleteCart(@PathVariable Integer id) {
		cartService.deleteCart(id);
	}

}

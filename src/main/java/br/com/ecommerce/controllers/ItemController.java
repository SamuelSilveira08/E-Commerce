package br.com.ecommerce.controllers;

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

import br.com.ecommerce.domain.UserPrincipal;
import br.com.ecommerce.dto.ItemDTO;
import br.com.ecommerce.service.ItemService;

@RestController
@RequestMapping("/items")
public class ItemController {

	@Autowired
	private ItemService itemService;

	/**
	 * 
	 * @param pageNumber
	 * @param numberItems
	 * @param sortBy
	 * @return {@link ItemService#getItems(Integer, Integer, String)}
	 * @throws AccessDeniedException if authenticated user is not an admin
	 */
	
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	@PreAuthorize(value = "hasRole('ADMIN')")
	public List<ItemDTO> getAllItems(@RequestParam(defaultValue = "0") Integer pageNumber,
			@RequestParam(defaultValue = "10") Integer numberItems, @RequestParam(defaultValue = "id") String sortBy) {
		return itemService.getItems(pageNumber, numberItems, sortBy);
	}

	@GetMapping("/byCart/{cartId}")
	@ResponseStatus(HttpStatus.OK)
	public List<ItemDTO> getAllItemsOfACart(@RequestParam(defaultValue = "0") Integer pageNumber,
			@RequestParam(defaultValue = "10") Integer numberItems, @RequestParam(defaultValue = "id") String sortBy,
			@PathVariable Integer cartId, @AuthenticationPrincipal UserPrincipal user) {
		return itemService.getItemsOfACart(pageNumber, numberItems, sortBy, cartId, user);
	}

	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ItemDTO getItemById(@PathVariable Integer id, @AuthenticationPrincipal UserPrincipal user) {
		return itemService.getItemById(id, user);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ItemDTO saveItem(@RequestBody ItemDTO itemDTO, @AuthenticationPrincipal UserPrincipal user) {
		return itemService.saveItem(itemDTO, user);
	}

	@PutMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ItemDTO updateItem(@RequestBody ItemDTO itemDTO, @PathVariable Integer id, @AuthenticationPrincipal UserPrincipal user) {
		return itemService.updateItem(itemDTO, id, user);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public void deleteItemById(@PathVariable Integer id, @AuthenticationPrincipal UserPrincipal user) {
		itemService.deleteItem(id, user);
	}

}

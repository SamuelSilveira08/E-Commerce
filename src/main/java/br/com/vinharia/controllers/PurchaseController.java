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
import br.com.vinharia.dto.PurchaseDTO;
import br.com.vinharia.service.PurchaseService;

@RestController
@RequestMapping("/purchases")
public class PurchaseController {

	@Autowired
	private PurchaseService purchaseService;

	/**
	 * 
	 * @param pageNumber
	 * @param numberItems
	 * @param sortBy
	 * @return {@link PurchaseService#getPurchases(Integer, Integer, String)}
	 * @throws AccessDeniedException if authenticated user is not an admin
	 */
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	@PreAuthorize(value = "hasRole('ADMIN')")
	public List<PurchaseDTO> getPurchases(@RequestParam(defaultValue = "0") Integer pageNumber,
			@RequestParam(defaultValue = "10") Integer numberItems, @RequestParam(defaultValue = "id") String sortBy) {
		return purchaseService.getPurchases(pageNumber, numberItems, sortBy);
	}

	@GetMapping("/byUser/{userId}")
	@ResponseStatus(HttpStatus.OK)
	public List<PurchaseDTO> getPurchasesByUser(@RequestParam(defaultValue = "0") Integer pageNumber,
			@RequestParam(defaultValue = "10") Integer numberItems, @RequestParam(defaultValue = "id") String sortBy,
			@PathVariable Integer userId, @AuthenticationPrincipal UserPrincipal user) {
		return purchaseService.getPurchasesByUserId(pageNumber, numberItems, sortBy, userId, user);
	}

	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public PurchaseDTO getPurchaseById(@PathVariable Integer id, @AuthenticationPrincipal UserPrincipal user) {
		return purchaseService.getPurchase(id, user);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public PurchaseDTO savePurchase(@RequestBody PurchaseDTO purchaseDTO, @AuthenticationPrincipal UserPrincipal user) {
		return purchaseService.savePurchase(purchaseDTO, user);
	}

	@PutMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public PurchaseDTO updatePurchase(@RequestBody PurchaseDTO purchaseDTO, @PathVariable Integer id,
			@AuthenticationPrincipal UserPrincipal user) {
		return purchaseService.updatePurchase(purchaseDTO, id, user);
	}

	/**
	 * 
	 * @param id
	 * @param user
	 * @throws AccessDeniedException if authenticated user is not an admin
	 */

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	@PreAuthorize(value = "hasRole('ADMIN')")
	public void deletePurchaseById(@PathVariable Integer id, @AuthenticationPrincipal UserPrincipal user) {
		purchaseService.deletePurchase(id, user);
	}

}

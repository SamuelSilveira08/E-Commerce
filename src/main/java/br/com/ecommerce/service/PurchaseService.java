package br.com.ecommerce.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import br.com.ecommerce.domain.Purchase;
import br.com.ecommerce.domain.UserPrincipal;
import br.com.ecommerce.dto.PurchaseDTO;
import br.com.ecommerce.exceptions.BadRequestException;
import br.com.ecommerce.exceptions.ForbiddenException;
import br.com.ecommerce.exceptions.NotFoundException;
import br.com.ecommerce.repositories.PurchaseRepository;

@Service
public class PurchaseService {

	@Autowired
	private PurchaseRepository purchaseRepository;

	@Autowired
	private ModelMapper mapper;

	/**
	 * 
	 * Return paginated data from all purchases
	 * 
	 * @author Samuel
	 * 
	 * @param pageNum     Number of the page from which data will be retrieved
	 * @param numberItems Number of items per page
	 * @param sortBy      Name of the field from Purchase class to use on sorting
	 *                    data
	 * @return a list with all purchases (number of purchases is up to numberItems
	 *         parameter) from given pageNum, sorted by given Purchase class' field
	 * @throws BadRequestException when passing a sorting field that doesn't exist
	 */
	public List<PurchaseDTO> getPurchases(Integer pageNum, Integer numberItems, String sortBy) {
		try {
			Purchase.class.getDeclaredField(sortBy);
		} catch (NoSuchFieldException e) {
			throw new BadRequestException("Field %s not found.".formatted(sortBy));
		}
		Pageable pageable = PageRequest.of(pageNum, numberItems, Sort.by(sortBy));
		Page<Purchase> page = purchaseRepository.findAll(pageable);
		List<Purchase> result = page.hasContent() ? page.getContent() : new ArrayList<>();
		List<PurchaseDTO> purchases = Arrays.asList(mapper.map(result, PurchaseDTO[].class));
		return purchases;
	}

	/**
	 * 
	 * Return data of all purchases of an user through their id
	 * 
	 * @author Samuel
	 * 
	 * @param pageNum       Number of the page from which data will be retrieved
	 * @param numberItems   Number of items per page
	 * @param sortBy        Name of the field from Purchase class to use on sorting
	 *                      data
	 * @param userId        Id from user to get purchases from
	 * @param userPrincipal Authenticated user
	 * @return a list with all purchases (number of purchases is up to numberItems
	 *         parameter) from given User (through userId), filtered by pageNum and
	 *         sorted by given Purchase class' field
	 * @throws BadRequestException when giving a sorting field that doesn't exist
	 * @throws ForbiddenException  when trying to access data from another user than
	 *                             the authenticated one (admins will have access
	 *                             though)
	 */
	public List<PurchaseDTO> getPurchasesByUserId(Integer pageNum, Integer numberItems, String sortBy, Integer userId,
			UserPrincipal userPrincipal) {
		try {
			Purchase.class.getDeclaredField(sortBy);
		} catch (NoSuchFieldException e) {
			throw new BadRequestException("Field %s not found.".formatted(sortBy));
		}
		if (userId == userPrincipal.getUser().getId() || userPrincipal.isAdmin()) {
			Pageable pageable = PageRequest.of(pageNum, numberItems, Sort.by(sortBy));
			Page<Purchase> page = purchaseRepository.findAllByUserId(userId, pageable);
			List<Purchase> result = page.hasContent() ? page.getContent() : new ArrayList<>();
			List<PurchaseDTO> purchases = Arrays.asList(mapper.map(result, PurchaseDTO[].class));
			return purchases;
		} else {
			throw new ForbiddenException("You're only allowed to access your own purchases");
		}
	}

	/**
	 * Return data from a purchase through its id
	 * 
	 * @author Samuel
	 * 
	 * @param id            identifier of the purchase
	 * @param userPrincipal authenticated user
	 * @return DTO (Data Transfer Object) containing given purchase's data
	 * @throws ForbiddenException if authenticated user doesn't have permission to
	 *                            access purchase data
	 * @throws NotFoundException  if there is no purchase with given id in database
	 */

	public PurchaseDTO getPurchase(Integer id, UserPrincipal userPrincipal) {
		Purchase result = purchaseRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Purchase with id %d not found".formatted(id)));
		if (result.getUser().getEmail() == userPrincipal.getUsername() || userPrincipal.isAdmin()) {
			PurchaseDTO dto = mapper.map(result, PurchaseDTO.class);
			return dto;
		} else {
			throw new ForbiddenException("You're only allowed to access your own purchases");
		}
	}

	/**
	 * Save a new purchase in database and return its data
	 * 
	 * @author Samuel
	 * 
	 * @param purchaseDTO   DTO containing data to be persisted
	 * @param userPrincipal authenticated user
	 * @return DTO containing data (generated id included) of the persisted purchase
	 * @throws ForbiddenException if authenticated user tries to save a purchase
	 *                            with other user's identifier (userId). Admins can
	 *                            do it, though
	 */

	public PurchaseDTO savePurchase(PurchaseDTO purchaseDTO, UserPrincipal userPrincipal) {
		Purchase purchase = mapper.map(purchaseDTO, Purchase.class);
		if (purchase.getUser().getEmail() == userPrincipal.getUsername() || userPrincipal.isAdmin()) {
			purchaseRepository.save(purchase);
			return purchaseDTO;
		} else {
			throw new ForbiddenException("You're only allowed to save your own purchases");
		}
	}

	/**
	 * Update a purchase and return updated purchase's data
	 * 
	 * @author Samuel
	 * 
	 * @param purchaseDTO   DTO containing data of the purchase to be updated
	 * @param id            identifier of the purchase to be updated
	 * @param userPrincipal authenticated user
	 * @return DTO containing data of the updated purchase
	 * @throws ForbiddenException if authenticated user tries to updated a purchase
	 *                            of another user (userId). Admins are allowed to do
	 *                            it, though
	 */

	public PurchaseDTO updatePurchase(PurchaseDTO purchaseDTO, Integer id, UserPrincipal userPrincipal) {
		purchaseRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Purchase with given id %d not found".formatted(id)));
		purchaseDTO.setId(id);
		Purchase purchase = mapper.map(purchaseDTO, Purchase.class);
		if (purchase.getUser().getEmail() == userPrincipal.getUsername() || userPrincipal.isAdmin()) {
			purchaseRepository.save(purchase);
			return purchaseDTO;
		} else {
			throw new ForbiddenException("You're only allowed to update your own purchases");
		}
	}

	/**
	 * Delete an user's purchase (admins only)
	 * 
	 * @author Samuel
	 * 
	 * @param id            identifier of the purchase to be deleted
	 * @param userPrincipal authenticated user
	 * @throws NotFoundException if there is no purchase with given identifier in
	 *                           database
	 */

	public void deletePurchase(Integer id, UserPrincipal userPrincipal) {
		Purchase purchaseToDelete = purchaseRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Purchase with id %d not found".formatted(id)));
		purchaseRepository.delete(purchaseToDelete);
	}

}

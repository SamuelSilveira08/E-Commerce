package br.com.vinharia.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.validation.ConstraintViolationException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import br.com.vinharia.domain.Cart;
import br.com.vinharia.domain.User;
import br.com.vinharia.domain.UserPrincipal;
import br.com.vinharia.dto.CartDTO;
import br.com.vinharia.exceptions.BadRequestException;
import br.com.vinharia.exceptions.ForbiddenException;
import br.com.vinharia.exceptions.NotFoundException;
import br.com.vinharia.repositories.CartRepository;

@Service
public class CartService {

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private ModelMapper mapper;

	/**
	 * Return paginated data from all carts saved in database
	 * 
	 * @author Samuel
	 * 
	 * @param pageNum     number of the page to be returned
	 * @param numberItems number of items (carts) per page
	 * @param sortBy      cart class' field to sort data by
	 * @return list with all carts from given page (number of items is up to numberItems
	 *         parameter), sorted by sortBy parameter.
	 * @throws BadRequestException if sortBy parameters doesn't exist in Cart class
	 */

	public List<CartDTO> getAllCarts(Integer pageNum, Integer numberItems, String sortBy) {
		try {
			Cart.class.getDeclaredField(sortBy);
		} catch (NoSuchFieldException e) {
			throw new BadRequestException("Field %s not found.".formatted(sortBy));
		}
		Pageable pageable = PageRequest.of(pageNum, numberItems, Sort.by(sortBy));
		Page<Cart> page = cartRepository.findAll(pageable);
		List<Cart> result = page.hasContent() ? page.getContent() : new ArrayList<>();
		List<CartDTO> dtos = Arrays.asList(mapper.map(result, CartDTO[].class));
		return dtos;
	}

	/**
	 * Return a cart by its id
	 * 
	 * @author Samuel
	 * 
	 * @param id            identifier of the cart to retrieve data
	 * @param userPrincipal authenticated user
	 * @return DTO containing cart's data
	 * @throws ForbiddenException if authenticated user is not allowed to access
	 *                            cart's data
	 * @throws NotFoundException  if there is no carts with given id in database
	 */

	public CartDTO getCartById(Integer id, UserPrincipal userPrincipal) {
		User user = userPrincipal.getUser();
		if (user.getCart() != null) {
			if (id == user.getCart().getId() || userPrincipal.isAdmin()) {
				Cart result = cartRepository.findById(id)
						.orElseThrow(() -> new NotFoundException("Cart with id %d not found.".formatted(id)));
				CartDTO dto = mapper.map(result, CartDTO.class);
				return dto;
			} else {
				throw new ForbiddenException("This user cannot access this cart's data");
			}
		}
		if (userPrincipal.getAuthorities().stream().anyMatch(authority -> authority.getAuthority() == "ROLE_ADMIN")) {
			Cart result = cartRepository.findById(id)
					.orElseThrow(() -> new NotFoundException("Cart with id %d not found.".formatted(id)));
			CartDTO dto = mapper.map(result, CartDTO.class);
			return dto;
		}
		return null;
	}

	/**
	 * Save a cart in database
	 * 
	 * @author Samuel
	 * 
	 * @param cartDTO       contains cart's data to be persisted
	 * @param userPrincipal authenticated user
	 * @return DTO with persisted cart's data (generated id included)
	 * @throws ForbiddenException           if user is trying to create a cart to
	 *                                      another user (admins are allowed to,
	 *                                      though)
	 * @throws ConstraintViolationException if cartDTO doesn't contain user
	 *                                      identifier
	 */

	public CartDTO saveCart(CartDTO cartDTO, UserPrincipal userPrincipal) {
		if (cartDTO.getTotalPrice() == null) {
			cartDTO.setTotalPrice(0.0);
		}
		User user = userPrincipal.getUser();
		Cart cart = mapper.map(cartDTO, Cart.class);
		if (user == null) {
			throw new ConstraintViolationException("Cart must be sent with userId", null);
		}
		if (user.getEmail() == userPrincipal.getUsername() || userPrincipal.isAdmin()) {
			cart = cartRepository.save(cart);
			cartDTO.setId(cart.getId());
			return cartDTO;
		} else {
			throw new ForbiddenException("You cannot create a new cart");
		}
	}

	/**
	 * Update a cart's data
	 * 
	 * @author Samuel
	 * 
	 * @param cartDTO       containing cart's data to be updated
	 * @param id            identifier of the cart to be updated
	 * @param userPrincipal authenticated user
	 * @return DTO containing updated cart's data
	 * @throws ForbiddenException if user tries to update the cart and put another
	 *                            user's id in it
	 */

	public CartDTO updateCart(CartDTO cartDTO, Integer id, UserPrincipal userPrincipal) {
		Cart cart = cartRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Cart with given id %d not found".formatted(id)));
		cartDTO.setId(id);
		cart = mapper.map(cartDTO, Cart.class);
		cart.setUser(mapper.map(userService.getUser(cartDTO.getUserId(), userPrincipal), User.class));
		if (cart.getUser().getEmail() == userPrincipal.getUsername() || userPrincipal.isAdmin()) {
			cartRepository.save(cart);
			return cartDTO;
		} else {
			throw new ForbiddenException("This user cannot update this cart");
		}
	}

	/**
	 * Delete an user's cart (admins only)
	 * 
	 * @author Samuel
	 * 
	 * @param id identifier of the cart to be deleted
	 * @throws NotFoundException if there is no carts with given id in database
	 */

	public void deleteCart(Integer id) {
		Cart cartToDelete = cartRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Cart with id %d not found.".formatted(id)));
		cartRepository.delete(cartToDelete);
	}
}

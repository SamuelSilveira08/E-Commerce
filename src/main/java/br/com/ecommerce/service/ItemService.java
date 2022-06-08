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

import br.com.ecommerce.domain.Cart;
import br.com.ecommerce.domain.Item;
import br.com.ecommerce.domain.Product;
import br.com.ecommerce.domain.UserPrincipal;
import br.com.ecommerce.dto.CartDTO;
import br.com.ecommerce.dto.ItemDTO;
import br.com.ecommerce.dto.ProductDTO;
import br.com.ecommerce.exceptions.BadRequestException;
import br.com.ecommerce.exceptions.ForbiddenException;
import br.com.ecommerce.exceptions.NotFoundException;
import br.com.ecommerce.repositories.ItemRepository;

@Service
public class ItemService {

	@Autowired
	private ItemRepository itemRepository;

	@Autowired
	private ModelMapper mapper;

	@Autowired
	private CartService cartService;

	@Autowired
	private ProductService productService;

	/**
	 * Return paginated data from all items in database (admins only)
	 * 
	 * @author Samuel
	 * 
	 * @param pageNum     page number to return
	 * @param numberItems number of items per page to return
	 * @param sortBy      Item class' field to sort data by
	 * @return list with all items from pageNum (number of items is up to
	 *         numberItems parameter) sorted by sortBy parameter
	 * @throws BadRequestException if sortBy parameters is not a field of Item class
	 */

	public List<ItemDTO> getItems(Integer pageNum, Integer numberItems, String sortBy) {
		try {
			Item.class.getDeclaredField(sortBy);
		} catch (NoSuchFieldException e) {
			throw new BadRequestException("Field %s not found.".formatted(sortBy));
		}
		Pageable pageable = PageRequest.of(pageNum, numberItems, Sort.by(sortBy));
		Page<Item> page = itemRepository.findAll(pageable);
		List<Item> result = page.hasContent() ? page.getContent() : new ArrayList<>();
		return Arrays.asList(mapper.map(result, ItemDTO[].class));
	}

	/**
	 * Return paginated data from all items of a given cart
	 * 
	 * @author Samuel
	 * 
	 * @param pageNum     page number to return
	 * @param numberItems number of items per page to return
	 * @param sortBy      Item class' field to sort data by
	 * @param cartId      Identifier of the cart to retrieve items
	 * @return list with all items from pageNum (number of items is up to
	 *         numberItems parameter) sorted by sortBy parameter
	 * @throws BadRequestException if sortBy parameters is not a field of Item class
	 * @throws ForbiddenException  if authenticated user is not the owner of the
	 *                             cart with given id
	 */

	public List<ItemDTO> getItemsOfACart(Integer pageNum, Integer numberItems, String sortBy, Integer cartId,
			UserPrincipal userPrincipal) {
		try {
			Item.class.getDeclaredField(sortBy);
		} catch (NoSuchFieldException e) {
			throw new BadRequestException("Field %s not found.".formatted(sortBy));
		}
		Cart cart = mapper.map(cartService.getCartById(cartId, userPrincipal), Cart.class);
		if (cart.getUser().getEmail() == userPrincipal.getUsername() || userPrincipal.isAdmin()) {
			Pageable pageable = PageRequest.of(pageNum, numberItems, Sort.by(sortBy));
			Page<Item> page = itemRepository.findAllByCartId(cartId, pageable);
			List<Item> result = page.hasContent() ? page.getContent() : new ArrayList<>();
			return Arrays.asList(mapper.map(result, ItemDTO[].class));
		} else {
			throw new ForbiddenException(
					"Authenticated user cannot access data from cart with given id %d".formatted(cartId));
		}

	}

	/**
	 * Return a single item through its id
	 * 
	 * @author Samuel
	 * 
	 * @param id            item's identifier to be retrieved from database
	 * @param userPrincipal authenticated user
	 * @return DTO containing data from item with given id
	 * @throws NotFoundException  if there is no items with given id in database
	 * @throws ForbiddenException if authenticated user is not related to the given
	 *                            item
	 */

	public ItemDTO getItemById(Integer id, UserPrincipal userPrincipal) {
		Item result = itemRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Item with id %d not found.".formatted(id)));
		if (result.getCart().getUser().getEmail() == userPrincipal.getUsername() || userPrincipal.isAdmin()) {
			cartService.getCartById(result.getCart().getId(), userPrincipal);
			return mapper.map(result, ItemDTO.class);
		} else {
			throw new ForbiddenException("This user cannot access this item's data");
		}
	}

	/**
	 * Persist an item in database
	 * 
	 * @author Samuel
	 * 
	 * @param itemDTO       containing item's data to be persisted
	 * @param userPrincipal authenticated user
	 * @return persisted item's data (generated id included)
	 * @throws ForbiddenException if authenticated user is not the same as the
	 *                            Item's user (identified by userId, in ItemDTO)
	 */

	public ItemDTO saveItem(ItemDTO itemDTO, UserPrincipal userPrincipal) {
		Item item = mapper.map(itemDTO, Item.class);

		Cart cartFromItem = mapper.map(cartService.getCartById(item.getCart().getId(), userPrincipal), Cart.class);

		if (cartFromItem.getUser().getEmail() == userPrincipal.getUsername() || userPrincipal.isAdmin()) {
			ProductDTO productDTO = productService.getProduct(item.getProduct().getId());
			item.setProduct(mapper.map(productDTO, Product.class));
			item = itemRepository.save(item);
			CartDTO cartDTO = cartService.getCartById(itemDTO.getCartId(), userPrincipal);
			Cart cart = mapper.map(cartDTO, Cart.class);
			cart.getItems().add(item);
			cart.calculateTotalPrice();
			cartDTO = mapper.map(cart, CartDTO.class);
			cartService.updateCart(cartDTO, cartDTO.getId(), userPrincipal);
			return mapper.map(item, ItemDTO.class);
		} else {
			throw new ForbiddenException("This user cannot save this data");
		}
	}

	/**
	 * Updated an item by its id
	 * 
	 * @author Samuel
	 * 
	 * @param itemDTO       containing data of the item to be updated
	 * @param id            identifier of the item to be updated
	 * @param userPrincipal authenticated user
	 * @return updated item's data
	 * @throws ForbiddenException if authenticated user is not the same as the
	 *                            Item's user (identified by userId, in ItemDTO)
	 * @throws NotFoundException  if there is no items with given id in database
	 */

	public ItemDTO updateItem(ItemDTO itemDTO, Integer id, UserPrincipal userPrincipal) {
		itemRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Item with given id %d not found".formatted(id)));
		itemDTO.setId(id);
		Item item = mapper.map(itemDTO, Item.class);
		Cart cartFromItem = mapper.map(cartService.getCartById(item.getCart().getId(), userPrincipal), Cart.class);

		if (cartFromItem.getUser().getEmail() == userPrincipal.getUsername() || userPrincipal.isAdmin()) {
			ProductDTO productDTO = productService.getProduct(item.getProduct().getId());
			item.setProduct(mapper.map(productDTO, Product.class));
			item = itemRepository.save(item);
			CartDTO cartDTO = cartService.getCartById(itemDTO.getCartId(), userPrincipal);
			Cart cart = mapper.map(cartDTO, Cart.class);
			cart.calculateTotalPrice();
			cartDTO = mapper.map(cart, CartDTO.class);
			cartService.updateCart(cartDTO, cartDTO.getId(), userPrincipal);
			return mapper.map(item, ItemDTO.class);
		} else {
			throw new ForbiddenException("This user cannot update this item");
		}
	}

	/**
	 * Delete an item from database
	 * 
	 * @author Samuel
	 * 
	 * @param id            identifier of the item to be deleted
	 * @param userPrincipal authenticated user
	 * @throws NotFoundException  if there is no items with given id in database
	 * @throws ForbiddenException if authenticated user is not related to the given
	 *                            item
	 */

	public void deleteItem(Integer id, UserPrincipal userPrincipal) {
		Item itemToDelete = itemRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Item with id %d not found.".formatted(id)));
		Cart cartFromItem = itemToDelete.getCart();
		if (cartFromItem.getUser().getEmail() == userPrincipal.getUsername() || userPrincipal.isAdmin()) {
			itemRepository.delete(itemToDelete);
			cartFromItem.getItems().removeIf(item -> item.getId() == itemToDelete.getId());
			cartFromItem.calculateTotalPrice();
			CartDTO cartDTO = mapper.map(cartFromItem, CartDTO.class);
			cartService.updateCart(cartDTO, cartDTO.getId(), userPrincipal);
		} else {
			throw new ForbiddenException("This user cannot delete this item");
		}

	}

}

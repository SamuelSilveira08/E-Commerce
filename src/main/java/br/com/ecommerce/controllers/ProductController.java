package br.com.ecommerce.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
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

import br.com.ecommerce.dto.ProductDTO;
import br.com.ecommerce.service.ProductService;

@RestController
@RequestMapping("/products")
public class ProductController {
	
	@Autowired
	private ProductService service;

	/**
	 * 
	 * @param pageNumber
	 * @param numberItems
	 * @param sortBy
	 * @return {@link ProductService#getProducts(Integer, Integer, String)}
	 * @throws AccessDeniedException if authenticated user is not an admin
	 */
	
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<ProductDTO> getAllProducts(@RequestParam(defaultValue = "0") Integer pageNum,
			@RequestParam(defaultValue = "10") Integer numberItems, @RequestParam(defaultValue = "id") String sortBy) {
		return service.getProducts(pageNum, numberItems, sortBy);
	}
	
	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ProductDTO getProduct(@PathVariable Integer id) {
		return service.getProduct(id);
	}
	
	/**
	 * 
	 * @param productDTO
	 * @return {@link ProductService#saveProduct(ProductDTO)}
	 * @throws AccessDeniedException if authenticated user is not an admin
	 */
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize(value = "hasRole('ADMIN')")
	public ProductDTO saveProduct(@RequestBody ProductDTO productDTO) {
		return service.saveProduct(productDTO);
	}
	
	/**
	 * 
	 * @param productDTO
	 * @param id
	 * @return {@link ProductService#updateProduct(ProductDTO, Integer)}
	 * @throws AccessDeniedException if authenticated user is not an admin
	 */
	
	@PutMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	@PreAuthorize(value = "hasRole('ADMIN')")
	public ProductDTO updateProduct(@RequestBody ProductDTO productDTO, @PathVariable Integer id) {
		return service.updateProduct(productDTO, id);
	}
	
	/**
	 * 
	 * @param id
	 * @throws AccessDeniedException if authenticated user is not an admin
	 */
	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	@PreAuthorize(value = "hasRole('ADMIN')")
	public void deleteProduct(@PathVariable Integer id) {
		service.deleteProduct(id);
	}
	
	
}

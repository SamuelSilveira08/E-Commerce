package br.com.vinharia.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.vinharia.domain.Product;
import br.com.vinharia.dto.ProductDTO;
import br.com.vinharia.service.ProductService;

@RestController
@RequestMapping("/products")
public class ProductController {
	
	@Autowired
	private ProductService service;

	@GetMapping
	public List<ProductDTO> getAllProducts() {
		return service.getProducts();
	}
	
	@GetMapping("/{id}")
	public ProductDTO getProduct(@PathVariable Long id) {
		return service.getProduct(id);
	}
	
	@PostMapping("/products")
	public ProductDTO saveProduct(@RequestBody Product newProduct) {
		return service.saveProduct(newProduct);
	}
	
	@PutMapping("/{id}")
	public ProductDTO updateProduct(@RequestBody Product product, @PathVariable Long id) {
		return service.updateProduct(product);
	}
	
	@DeleteMapping("/{id}")
	public void deleteProduct(@PathVariable Long id) {
		service.deleteProduct(id);
	}
	
	
}

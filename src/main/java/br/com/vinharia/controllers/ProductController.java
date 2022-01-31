package br.com.vinharia.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.vinharia.domain.Product;
import br.com.vinharia.exceptions.RequestException;
import br.com.vinharia.repositories.ProductRepository;

@RestController
@RequestMapping("/products")
public class ProductController {
	
	@Autowired
	private ProductRepository repository;

	@GetMapping
	public List<Product> getAllProducts() {
		return repository.findAll();
	}
	
	@GetMapping("/products/{id}")
	public Product getProduct(@PathVariable Long id) {
		return repository.findById(id).orElseThrow(() -> new RequestException("Product not found.", HttpStatus.NOT_FOUND));
	}
	
	@PostMapping("/products")
	public Product saveProduct(@RequestBody Product newProduct) {
		return repository.save(newProduct);
	}
	
	@PutMapping("/{id}")
	public Product editProduct(@RequestBody Product product, @PathVariable Long id) {
		return repository.save(product);
	}
	
	@DeleteMapping("/{id}")
	public void deleteProduct(@PathVariable Long id) {
		repository.deleteById(id);
	}
	
	
}

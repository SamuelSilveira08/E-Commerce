package br.com.vinharia.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.vinharia.domain.Product;
import br.com.vinharia.repositories.ProductRepository;

@RestController
public class ProductController {
	
	@Autowired
	private ProductRepository repository;

	@GetMapping("/products")
	public List<Product> getAllProducts() {
		return repository.findAll();
	}
	
	@GetMapping("/products/{id}")
	public Product getProduct(@PathVariable Long id) {
		return repository.findById(id).orElseThrow();
	}
	
	@PostMapping("/products")
	public Product saveProduct(@RequestBody Product newProduct) {
		return repository.save(newProduct);
	}
	
}

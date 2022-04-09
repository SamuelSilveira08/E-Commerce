package br.com.vinharia.service;

import java.util.Arrays;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.vinharia.domain.Product;
import br.com.vinharia.dto.ProductDTO;
import br.com.vinharia.exceptions.NotFoundException;
import br.com.vinharia.repositories.ProductRepository;

@Service
public class ProductService {

	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private ModelMapper mapper;
	
	public List<ProductDTO> getProducts() {
		List<Product> result = productRepository.findAll();
		return Arrays.asList(mapper.map(result, ProductDTO[].class));
	}
	
	public ProductDTO getProduct(Long id) {
		var product = productRepository.findById(id).orElseThrow(() -> new NotFoundException("Product with id %d not found.".formatted(id)));
		return mapper.map(product, ProductDTO.class);
	}
	
	public ProductDTO saveProduct(Product product) {
		return mapper.map(productRepository.save(product), ProductDTO.class);
	}
	
	public ProductDTO updateProduct(Product product) {
		return mapper.map(productRepository.save(product), ProductDTO.class);
	}
	
	public void deleteProduct(Long id) {
		var productToDelete = productRepository.findById(id).orElseThrow(() -> new NotFoundException("Product with id %id not found.".formatted(id)));
		productRepository.delete(productToDelete);
	}

}

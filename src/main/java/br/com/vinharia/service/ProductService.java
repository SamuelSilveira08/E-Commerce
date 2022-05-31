package br.com.vinharia.service;

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

import br.com.vinharia.domain.Product;
import br.com.vinharia.dto.ProductDTO;
import br.com.vinharia.exceptions.BadRequestException;
import br.com.vinharia.exceptions.NotFoundException;
import br.com.vinharia.repositories.ProductRepository;

@Service
public class ProductService {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private ModelMapper mapper;

	/**
	 * Return paginated data from all products in database
	 * 
	 * @author Samuel
	 * 
	 * @param pageNum     page number to retrieve data
	 * @param numberItems number of items per page
	 * @param sortBy      Product class' field to sort data by
	 * @return list with all products from pageNum parameter (number of items is up
	 *         to numberItems parameter), sorted by sortBy parameter
	 */

	public List<ProductDTO> getProducts(Integer pageNum, Integer numberItems, String sortBy) {
		try {
			Product.class.getDeclaredField(sortBy);
		} catch (NoSuchFieldException e) {
			throw new BadRequestException("Field %s not found.".formatted(sortBy));
		}
		Pageable pageable = PageRequest.of(pageNum, numberItems, Sort.by(sortBy));
		Page<Product> page = productRepository.findAll(pageable);
		List<Product> result = page.hasContent() ? page.getContent() : new ArrayList<>();
		return Arrays.asList(mapper.map(result, ProductDTO[].class));
	}

	/**
	 * Return a single product's data
	 * 
	 * @author Samuel
	 * 
	 * @param id identifier of the product
	 * @return DTO containing given product's data
	 * @throws NotFoundException if there is no products with given id in database
	 */
	public ProductDTO getProduct(Integer id) {
		Product product = productRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Product with id %d not found.".formatted(id)));
		return mapper.map(product, ProductDTO.class);
	}

	/**
	 * Save a product in database (admins only)
	 * 
	 * @author Samuel
	 * 
	 * @param productDTO containing data of product to be persisted
	 * @return persisted product's data (generated id included)
	 */

	public ProductDTO saveProduct(ProductDTO productDTO) {
		Product product = mapper.map(productDTO, Product.class);
		product = productRepository.save(product);
		productDTO.setId(product.getId());
		return productDTO;
	}

	/**
	 * Update a product's data (admins only)
	 * 
	 * @author Samuel
	 * 
	 * @param productDTO containing product's data to be updated
	 * @param id         identifier of the product to be updated
	 * @return DTO containing update product's data
	 * @throws NotFoundException if there is no products with given id in database
	 */

	public ProductDTO updateProduct(ProductDTO productDTO, Integer id) {
		productRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Product with given id %d not found".formatted(id)));
		productDTO.setId(id);
		Product product = mapper.map(productDTO, Product.class);
		productRepository.save(product);
		return productDTO;
	}

	/**
	 * Delete a product from database (admins only)
	 * 
	 * @author Samuel
	 * 
	 * @param id identifier of the product to be deleted
	 * @throws NotFoundException if there is no products with given id in database
	 */

	public void deleteProduct(Integer id) {
		Product productToDelete = productRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Product with id %id not found.".formatted(id)));
		productRepository.delete(productToDelete);
	}

}

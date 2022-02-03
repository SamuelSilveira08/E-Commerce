package br.com.vinharia.service;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.vinharia.domain.Product;
import br.com.vinharia.dto.ProductDTO;
import br.com.vinharia.repositories.ProductRepository;

@Service
public class ProductService {

	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private ModelMapper mapper;
	
	public List<ProductDTO> getProducts() {
		List<Product> result = productRepository.findAll();
//		return Arrays.asList(mapper.map(result, ProductDTO[].class));
		ProductDTO pDTO = mapper.map(result, ProductDTO.class);
		System.out.println(pDTO.getName());
		System.out.println(pDTO);
		return new ArrayList<ProductDTO>();
	}

}

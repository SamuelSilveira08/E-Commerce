package br.com.vinharia.controllers;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;

import br.com.vinharia.domain.Product;
import br.com.vinharia.dto.ProductDTO;
import br.com.vinharia.repositories.ProductRepository;
import br.com.vinharia.service.ProductService;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

	@Autowired
	private ProductController productController;
	
	@Autowired
	private ModelMapper mapper;
	
	@MockBean
	private ProductRepository productRepository;
	
	@MockBean
	private ProductService productService;
	
	@BeforeEach
	public void setup() {
		RestAssuredMockMvc.standaloneSetup(productController);
	}
	
	@Test
	public void shouldReturnNewProduct_WhenSaveProductIsCalledWithAProduct() {
		
		Product newProduct = new Product(null, "Vinho Tinto 750 ml", 15.0, 100);
		
		when(this.productService.saveProduct(newProduct))
			.thenReturn(mapper.map(newProduct, ProductDTO.class));
		
		RestAssuredMockMvc.given()
					.accept(ContentType.JSON)
					.when()
						.post("/products/products")
					.then()
						.statusCode(HttpStatus.OK.value());
	}

}

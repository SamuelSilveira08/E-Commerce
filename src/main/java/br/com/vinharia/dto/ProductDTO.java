package br.com.vinharia.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDTO {

	public ProductDTO() {
		
	}
	
	private String name;
	private Double price;
	private Integer stock;

}

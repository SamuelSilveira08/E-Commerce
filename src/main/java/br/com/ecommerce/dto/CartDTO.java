package br.com.ecommerce.dto;

import java.util.List;

import org.springframework.hateoas.RepresentationModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO extends RepresentationModel<CartDTO> {

	private Integer id;
	private Double totalPrice;
	private List<ItemDTO> items;
	private Integer userId;
	
}

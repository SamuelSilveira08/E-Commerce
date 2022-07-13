package br.com.ecommerce.dto;

import org.springframework.hateoas.RepresentationModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemDTO extends RepresentationModel<ItemDTO> {

	private Integer id;
	private Integer productId;
	private String productName;
	private Integer productStock;
	private Double productPrice;
	private Integer quantity;
	private Integer cartId;
	private Integer purchaseId;
	
}

package br.com.ecommerce.dto;

import java.util.Date;
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
public class PurchaseDTO extends RepresentationModel<PurchaseDTO> {

	private Integer id;
	private Double price;
	private Date date;
	private Integer userId;
	private List<ItemDTO> items;
	
	// Precisa relacionar com Cart?
	
}

package br.com.ecommerce.domain;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "cart")
@Entity
public class Cart {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@OneToMany(mappedBy = "cart")
	private List<Item> items;

	@NotNull(message = "Field cannot be null. If cart has no items yet, totalPrice must receive 0")
	private Double totalPrice;
	
	@OneToOne(mappedBy = "cart", cascade = CascadeType.MERGE)
	@NotNull(message = "Field user cannot be null!")
	private User user;
	
	
	public Double calculateTotalPrice() {
		this.totalPrice = items.stream().map(item -> item.getProduct().getPrice() * item.getQuantity()).reduce(0.0, Double::sum);
		return this.totalPrice;
	}

}

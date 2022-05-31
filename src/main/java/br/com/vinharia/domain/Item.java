package br.com.vinharia.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "item")
@Entity
public class Item {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "id_product")
	@NotNull(message = "Field product cannot be null!")
	private Product product;

	@ManyToOne
	@JoinColumn(name = "id_cart")
	@NotNull(message = "Field cart cannot be null!")
	private Cart cart;

	@ManyToOne
	@JoinColumn(name = "id_purchase")
	private Purchase purchase;

	@NotNull(message = "Field quantity cannot be null!")
	private Integer quantity;

}

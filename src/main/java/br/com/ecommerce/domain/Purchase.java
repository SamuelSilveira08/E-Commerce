package br.com.ecommerce.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "purchase")
public class Purchase {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotNull(message = "Field price cannot be null - If purchase has no price, pass 0.")
	private Double price;
	
	@OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL, orphanRemoval = true)
	@NotNull(message = "Field products cannot be null!")
	private List<Item> items;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	@NotNull(message = "Field user cannot be null!")
	private User user;
	
	@Temporal(TemporalType.DATE)
	@NotNull(message = "Field date cannot be null!")
	private Date date;
	
	
}

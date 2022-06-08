package br.com.ecommerce.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "user")
public class User {

	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	@Column(name = "id")
	private Integer id;

	@Column(unique = true)
	@Email
	@NotBlank(message = "Field email cannot be null!")
	private String email;

	@NotBlank(message = "Field password cannot be null!")
	@Size(min = 6)
	private String password;

	private boolean admin;

	@Column(name = "first_name", length = 30)
	@NotBlank(message = "Field firstName cannot be null!")
	private String firstName;

	@Column(name = "last_name", length = 30)
	@NotBlank(message = "Field lastName cannot be null!")
	private String lastName;

	@OneToMany(mappedBy = "user")
	private List<Purchase> purchases;

	@OneToOne
	@JoinColumn(name = "cart_id")
	private Cart cart;

}

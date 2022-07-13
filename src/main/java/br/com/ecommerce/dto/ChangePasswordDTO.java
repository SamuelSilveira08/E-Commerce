package br.com.ecommerce.dto;

import org.springframework.hateoas.RepresentationModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChangePasswordDTO extends RepresentationModel<ChangePasswordDTO> {

	private String currentPassword;
	private String newPassword;

}

package br.com.ecommerce.dto;

import java.util.Date;

import org.springframework.hateoas.RepresentationModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=false)
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse extends RepresentationModel<JwtResponse> {

	private String token;
	private String type;
	private Date expiresAt;
	private String refreshToken;
}

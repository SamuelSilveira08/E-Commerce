package br.com.vinharia.dto;

import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class RefreshTokenRequest {

	@NotBlank(message = "You must send refreshToken to refresh the access token")
	private String refreshToken;

}

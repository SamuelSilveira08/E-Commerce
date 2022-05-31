package br.com.vinharia.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {

	private String token;
	private String type;
	private Date expiresAt;
	private String refreshToken;
}

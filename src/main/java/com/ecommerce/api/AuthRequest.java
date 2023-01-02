package com.ecommerce.api;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AuthRequest {
	@NotNull @Length(min = 5, max = 50)
	private String username;
	
	@NotNull @Length(min = 5, max = 100)
	private String password;
}

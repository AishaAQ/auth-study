package com.backend.auth.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class UserDTO {
	
	@NotEmpty
	@Email
	public String email;
	
	@NotEmpty
	@Size(min = 8, max = 256)
	public String password;
	
}

package com.backend.auth.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class ChangePasswordDTO {
	
	@NotEmpty
	public String currentPassword;
	
	@NotEmpty
	@Size(min = 8, max = 256)
	public String newPassword;
	
	@AssertTrue(message = "New password must be different than current password")
	@JsonIgnore
	public boolean isNewPasswordDifferent() {
		if (currentPassword == null || newPassword == null) return true;
		return !currentPassword.equals(newPassword);
	}

}

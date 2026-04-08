package com.edulib.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ResetPasswordRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "New password is required")
    @Size(min = 8, max = 64, message = "Password must be 8-64 characters")
    private String newPassword;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    public ResetPasswordRequest() {}

    public String getEmail()                        { return email; }
    public void setEmail(String email)             { this.email = email; }
    public String getNewPassword()                  { return newPassword; }
    public void setNewPassword(String p)           { this.newPassword = p; }
    public String getConfirmPassword()              { return confirmPassword; }
    public void setConfirmPassword(String p)       { this.confirmPassword = p; }
}

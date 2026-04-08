package com.edulib.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ChangePasswordRequest {

    @NotBlank(message = "Current password is required")
    private String currentPassword;

    @NotBlank(message = "New password is required")
    @Size(min = 8, max = 64, message = "New password must be 8-64 characters")
    private String newPassword;

    public ChangePasswordRequest() {}

    public String getCurrentPassword()              { return currentPassword; }
    public void setCurrentPassword(String p)       { this.currentPassword = p; }
    public String getNewPassword()                  { return newPassword; }
    public void setNewPassword(String p)           { this.newPassword = p; }
}

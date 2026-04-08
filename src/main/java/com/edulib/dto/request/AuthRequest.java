package com.edulib.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthRequest {

    public static class Register {
        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 100, message = "Name must be 2-100 characters")
        private String name;

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        private String email;

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 64, message = "Password must be 8-64 characters")
        private String password;

        public Register() {}

        public String getName()             { return name; }
        public void setName(String name)   { this.name = name; }
        public String getEmail()           { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword()        { return password; }
        public void setPassword(String p)  { this.password = p; }
    }

    public static class Login {
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        private String email;

        @NotBlank(message = "Password is required")
        private String password;

        public Login() {}

        public String getEmail()           { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword()        { return password; }
        public void setPassword(String p)  { this.password = p; }
    }
}

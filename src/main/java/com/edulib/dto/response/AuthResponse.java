package com.edulib.dto.response;

import com.edulib.entity.User;

import java.time.LocalDateTime;

public class AuthResponse {

    public static class TokenResponse {
        private String accessToken;
        private String tokenType;
        private Long expiresIn;
        private UserInfo user;

        public TokenResponse() {}

        public TokenResponse(String accessToken, String tokenType, Long expiresIn, UserInfo user) {
            this.accessToken = accessToken; this.tokenType = tokenType;
            this.expiresIn = expiresIn; this.user = user;
        }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private String accessToken; private String tokenType;
            private Long expiresIn; private UserInfo user;

            public Builder accessToken(String t)  { this.accessToken = t; return this; }
            public Builder tokenType(String t)    { this.tokenType = t; return this; }
            public Builder expiresIn(Long e)      { this.expiresIn = e; return this; }
            public Builder user(UserInfo u)       { this.user = u; return this; }

            public TokenResponse build() {
                return new TokenResponse(accessToken, tokenType, expiresIn, user);
            }
        }

        public String getAccessToken()              { return accessToken; }
        public void setAccessToken(String t)        { this.accessToken = t; }
        public String getTokenType()                { return tokenType; }
        public void setTokenType(String t)          { this.tokenType = t; }
        public Long getExpiresIn()                  { return expiresIn; }
        public void setExpiresIn(Long e)            { this.expiresIn = e; }
        public UserInfo getUser()                   { return user; }
        public void setUser(UserInfo user)          { this.user = user; }
    }

    public static class UserInfo {
        private Long id;
        private String name;
        private String email;
        private User.Role role;
        private LocalDateTime createdAt;

        public UserInfo() {}

        public UserInfo(Long id, String name, String email, User.Role role, LocalDateTime createdAt) {
            this.id = id; this.name = name; this.email = email;
            this.role = role; this.createdAt = createdAt;
        }

        public static UserInfo from(User user) {
            return new UserInfo(user.getId(), user.getName(), user.getEmail(),
                    user.getRole(), user.getCreatedAt());
        }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private Long id; private String name; private String email;
            private User.Role role; private LocalDateTime createdAt;

            public Builder id(Long id)                   { this.id = id; return this; }
            public Builder name(String name)             { this.name = name; return this; }
            public Builder email(String email)           { this.email = email; return this; }
            public Builder role(User.Role role)          { this.role = role; return this; }
            public Builder createdAt(LocalDateTime t)    { this.createdAt = t; return this; }

            public UserInfo build() {
                return new UserInfo(id, name, email, role, createdAt);
            }
        }

        public Long getId()                  { return id; }
        public void setId(Long id)          { this.id = id; }
        public String getName()              { return name; }
        public void setName(String name)    { this.name = name; }
        public String getEmail()            { return email; }
        public void setEmail(String email)  { this.email = email; }
        public User.Role getRole()          { return role; }
        public void setRole(User.Role role) { this.role = role; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime t) { this.createdAt = t; }
    }
}

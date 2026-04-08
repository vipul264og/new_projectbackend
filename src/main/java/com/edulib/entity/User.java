package com.edulib.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_user_email", columnList = "email")
})
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(nullable = false)
    private boolean enabled = true;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Review> reviews = new ArrayList<>();

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public enum Role { ADMIN, USER }

    public User() {}

    public User(Long id, String name, String email, String password, Role role,
                boolean enabled, List<Review> reviews, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id; this.name = name; this.email = email; this.password = password;
        this.role = role; this.enabled = enabled;
        this.reviews = reviews != null ? reviews : new ArrayList<>();
        this.createdAt = createdAt; this.updatedAt = updatedAt;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id; private String name; private String email;
        private String password; private Role role; private boolean enabled = true;
        private List<Review> reviews = new ArrayList<>();
        private LocalDateTime createdAt; private LocalDateTime updatedAt;

        public Builder id(Long id)                   { this.id = id; return this; }
        public Builder name(String name)             { this.name = name; return this; }
        public Builder email(String email)           { this.email = email; return this; }
        public Builder password(String password)     { this.password = password; return this; }
        public Builder role(Role role)               { this.role = role; return this; }
        public Builder enabled(boolean enabled)      { this.enabled = enabled; return this; }
        public Builder reviews(List<Review> reviews) { this.reviews = reviews; return this; }

        public User build() {
            return new User(id, name, email, password, role, enabled, reviews, createdAt, updatedAt);
        }
    }

    public Long getId()                      { return id; }
    public void setId(Long id)              { this.id = id; }
    public String getName()                  { return name; }
    public void setName(String name)        { this.name = name; }
    public String getEmail()                { return email; }
    public void setEmail(String email)      { this.email = email; }
    public String getPassword()             { return password; }
    public void setPassword(String p)       { this.password = p; }
    public Role getRole()                   { return role; }
    public void setRole(Role role)          { this.role = role; }
    public boolean isEnabled()              { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public List<Review> getReviews()        { return reviews; }
    public void setReviews(List<Review> r)  { this.reviews = r; }
    public LocalDateTime getCreatedAt()     { return createdAt; }
    public void setCreatedAt(LocalDateTime t) { this.createdAt = t; }
    public LocalDateTime getUpdatedAt()     { return updatedAt; }
    public void setUpdatedAt(LocalDateTime t) { this.updatedAt = t; }
}

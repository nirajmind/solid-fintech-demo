package com.niraj.solidproject.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

//@Entity
//@Table(name = "users")
@Document(collection = "users") // <-- Changed from @Entity
public class User {

    @Id
    private String id;
    private String name;
    //@Column(unique = true) // Usernames must be unique
    @Indexed(unique = true) // Usernames must be unique
    private String username;
    private String email;
    private String password; // New Field
    private String roles;    // New Field (e.g., "ROLE_USER")

    public User() {}

    public User(String name, String username, String email, String password, String roles) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }

    // --- Getters and Setters for ALL fields ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRoles() { return roles; }
    public void setRoles(String roles) { this.roles = roles; }
}
package com.monthly.ecommercemonolith.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@NoArgsConstructor
@Data
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @NotBlank
    @Size(min = 3, max = 20)
    @Column(name = "username")
    private String username;
    @NotBlank
    @Size(min = 5, max = 50)
    @Email
    @Column(name = "email")
    private String email;
    @NotBlank
    @Size(min = 5, max = 100)
    private String password;

    @OneToOne(mappedBy = "user", cascade = {CascadeType.MERGE,
            CascadeType.PERSIST}, orphanRemoval = true)
    @ToString.Exclude
    private Cart cart;

    @Getter
    @Setter
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST,
            CascadeType.MERGE}, orphanRemoval = true)
    @ToString.Exclude
    private Set<Product> products = new HashSet<>();

    @OneToMany (cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true,mappedBy = "user")
//    @JoinTable(name = "user_addresses", joinColumns = @JoinColumn(name = "user_id"),
//            inverseJoinColumns = @JoinColumn(name = "address_id"))
    @Getter
    @Setter
    private List<Address> addresses = new ArrayList<>();

    public User(String username, String email, String password) {
        this.email = email;
        this.password = password;
        this.username = username;
    }

}

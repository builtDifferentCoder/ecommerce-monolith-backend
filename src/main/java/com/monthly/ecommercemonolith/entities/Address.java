package com.monthly.ecommercemonolith.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="address_id")
    private Long addressId;
    @NotBlank
    @Size(min=5, message = "Street name must be atleast 5 characters.")
    private String street;
    @NotBlank
    @Size(min=5, message = "Building name must be atleast 5 characters.")
    private String buildingName;
    @NotBlank
    @Size(min=4,message = "City name must be atleast 4 characters")
    private String city;
    @NotBlank
    @Size(min=3,message = "State name must be atleast 3 characters")
    private String state;
    @NotBlank
    @Size(min=2,message = "Country name must be atleast 4 characters")
    private String country;
    @NotBlank
    @Size(min=4,message = "Zip code name must be atleast 4 characters")
    private String zipCode;


    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    public Address(String buildingName, String city, String country,
                   String state, String street,String zipCode) {
        this.buildingName = buildingName;
        this.city = city;
        this.country = country;
        this.state = state;
        this.street = street;
        this.zipCode=zipCode;
    }
}

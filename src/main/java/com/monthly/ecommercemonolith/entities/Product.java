package com.monthly.ecommercemonolith.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="products")
@ToString
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank
    @Size(min=3,message = "Product name must contain atleast 3 characters.")
    private String productName;
    private String description;
    private String image;
    private Integer quantity;
    private double discount;
    private double price;
    private double specialPrice;

    @OneToMany(mappedBy = "product",cascade = {CascadeType.PERSIST,
            CascadeType.MERGE},fetch = FetchType.EAGER)
    private List<CartItem>products=new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User user;

    @ManyToOne
    @JoinColumn(name="category_id")
    private Category category;

}

package com.java.electro.store.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cart")
public class Cart {

    @Id
    private String cartId;
    private Date createdAt;
    @OneToOne
    private User user;

    // mapping cartItem
    @OneToMany(mappedBy = "cart",cascade = CascadeType.ALL ,orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();
}

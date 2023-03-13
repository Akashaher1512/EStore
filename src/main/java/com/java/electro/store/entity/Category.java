package com.java.electro.store.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @Column(name = "id")
    private String categoryId;
    @Column(name = "category_title", length = 60 , nullable = false)
    private String title;
    @Column(name = "category_description", length = 500 )
    private String description;
    private String coverImage;

    @OneToMany(mappedBy = "category" ,cascade = CascadeType.ALL  ,fetch = FetchType.LAZY)
    private List<Product> products = new ArrayList<>();


}

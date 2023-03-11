package com.java.electro.store.entity;

import lombok.*;

import javax.persistence.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="users")
public class User {

    @Id
    private String id;

    @Column(name = "user_name")
    private String name;

    @Column(name = "user_email" , unique = true)
    private String email;

    @Column(name = "user_password" , length = 10)
    private String password;
    private String gender;

    @Column(length = 100)
    private String about;

    @Column(name = "user_image_name")
    private String imageName;


}

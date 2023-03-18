package com.java.electro.store.entity;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="users")
public class User implements UserDetails {

    @Id
    private String id;

    @Column(name = "user_name")
    private String name;

    @Column(name = "user_email" , unique = true)
    private String email;

    @Column(name = "user_password" , length = 500)
    private String password;
    private String gender;

    @Column(length = 100)
    private String about;

    @Column(name = "user_image_name")
    private String imageName;

    @OneToMany(mappedBy = "user" ,fetch = FetchType.LAZY , cascade = CascadeType.REMOVE)
    private List<Order> orders = new ArrayList<>();

    @OneToOne(mappedBy = "user" ,cascade = CascadeType.REMOVE)
    private Cart cart;

    @ManyToMany(fetch = FetchType.EAGER ,cascade = CascadeType.ALL)
    private Set<Role> roles = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> authorities = roles.stream().map(role -> new SimpleGrantedAuthority(role.getRoleName())).collect(Collectors.toSet());

        return authorities;
    }

    @Override
    public String getUsername() {
        return this.email;
    }


    // this will automatically implement if we use lombok , so  manually override it
    @Override
    public String getPassword(){
        return this.password;
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

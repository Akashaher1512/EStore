package com.java.electro.store.repository;

import com.java.electro.store.entity.Cart;
import com.java.electro.store.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart , String> {

    Optional<Cart> findByUser(User user);
}

package com.java.electro.store.repository;

import com.java.electro.store.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem , Integer> {
}

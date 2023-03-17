package com.java.electro.store.repository;

import com.java.electro.store.entity.Order;
import com.java.electro.store.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order , String> {
    List<Order> findByUser(User user);



}

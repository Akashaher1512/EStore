package com.java.electro.store.repository;

import com.java.electro.store.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem , Integer> {



}

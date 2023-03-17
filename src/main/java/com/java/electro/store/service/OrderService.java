package com.java.electro.store.service;

import com.java.electro.store.dto.CreateOrderRequest;
import com.java.electro.store.dto.OrderDto;
import com.java.electro.store.dto.PageableResponse;

import java.util.List;

public interface OrderService {
    OrderDto createOrder(CreateOrderRequest orderDto);

    void removeOrder(String orderId);

    List<OrderDto> getOrderOfUser(String userId);

    PageableResponse<OrderDto> getAllOrders(int pageNumber , int pageSize , String sortBy , String sortDir);
}

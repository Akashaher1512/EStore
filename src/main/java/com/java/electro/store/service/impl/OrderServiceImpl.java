package com.java.electro.store.service.impl;

import com.java.electro.store.dto.CreateOrderRequest;
import com.java.electro.store.dto.OrderDto;
import com.java.electro.store.dto.PageableResponse;
import com.java.electro.store.entity.*;
import com.java.electro.store.exception.BadApiRequest;
import com.java.electro.store.exception.ResourceNotFoundException;
import com.java.electro.store.helper.Helper;
import com.java.electro.store.repository.CartRepository;
import com.java.electro.store.repository.OrderRepository;
import com.java.electro.store.repository.UserRepository;
import com.java.electro.store.service.OrderService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public OrderDto createOrder(CreateOrderRequest orderDto) {
        // fetch user
        User user = userRepository.findById(orderDto.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User not found with user_id : " + orderDto.getUserId()));
        Cart cart = cartRepository.findById(orderDto.getCartId()).orElseThrow(() -> new ResourceNotFoundException("cart not found with given cart_id : " + orderDto.getCartId()));

        List<CartItem> cartItems = cart.getItems();

        if(cartItems.size() <= 0){
            throw new BadApiRequest("Invalid number of items in cart");
        }

        Order order = Order.builder().billingName(orderDto.getBillingName())
                .billingPhone(orderDto.getBillingPhone())
                .billingAddress(orderDto.getBillingAddress())
                .orderdDate(new Date())
                .diliverdDate(null)
                .paymentStatus(orderDto.getPaymentStatus())
                .orderStatus(orderDto.getPaymentStatus())
                .orderId(UUID.randomUUID().toString())
                .user(user)
                .build();

        //remaining to set orderItem , amount

        AtomicReference<Integer> orderAmount = new AtomicReference<>(0);

        List<OrderItem> orderItems = cartItems.stream().map(cartItem -> {

            // cartItem -> orderItem
            OrderItem orderItem = OrderItem.builder()
                    .quantity(cartItem.getQuantity())
                    .product(cartItem.getProduct())
                    .totalPrise(cartItem.getQuantity() * cartItem.getProduct().getDiscountedPrice())
                    .order(order)
                    .build();

            orderAmount.set(orderAmount.get() + orderItem.getTotalPrise());

            return orderItem;
        }).collect(Collectors.toList());

        order.setOrderItems(orderItems);
        order.setOrderAmount(orderAmount.get());

        // now clear cart
        cart.getItems().clear();

        cartRepository.save(cart);

        Order savedOrder = orderRepository.save(order);

        return mapToDto(savedOrder);
    }

    @Override
    public void removeOrder(String orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found with given order_id : " + orderId));
        orderRepository.delete(order);
    }

    @Override
    public List<OrderDto> getOrderOfUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with given user_id : " + userId));
        List<Order> orders = orderRepository.findByUser(user);
        List<OrderDto> orderDtos = orders.stream().map(order -> mapToDto(order)).collect(Collectors.toList());

        return orderDtos;
    }

    @Override
    public PageableResponse<OrderDto> getAllOrders(int pageNumber, int pageSize, String sortBy, String sortDir) {

        Sort sort  = (sortDir.equalsIgnoreCase("DESC")) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize,sort);
        Page<Order> page = orderRepository.findAll(pageable);

        PageableResponse<OrderDto> pageableResponse = Helper.getPageableResponse(page, OrderDto.class);

        return pageableResponse;
    }

    private OrderDto mapToDto(Order order){
        return modelMapper.map(order , OrderDto.class);
    }

    private Order mapToDto(OrderDto orderDto){
        return modelMapper.map(orderDto , Order.class);
    }

}

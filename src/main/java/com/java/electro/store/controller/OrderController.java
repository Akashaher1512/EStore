package com.java.electro.store.controller;

import com.java.electro.store.dto.ApiResponseMessage;
import com.java.electro.store.dto.CreateOrderRequest;
import com.java.electro.store.dto.OrderDto;
import com.java.electro.store.dto.PageableResponse;
import com.java.electro.store.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.PermitAll;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderDto> createRequest(@Valid @RequestBody CreateOrderRequest createOrderRequest){
        OrderDto order = orderService.createOrder(createOrderRequest);
        return new ResponseEntity<>(order , HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponseMessage> removeOrder(@PathVariable String orderId){
        orderService.removeOrder(orderId);
        ApiResponseMessage responseMessage = ApiResponseMessage.builder()
                .message("Order is removed !")
                .status(HttpStatus.OK)
                .success(true)
                .build();
        return new ResponseEntity<>(responseMessage ,HttpStatus.OK);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<OrderDto>> getOrdersOfUser(@PathVariable String userId){
        List<OrderDto> orderOfUser = orderService.getOrderOfUser(userId);
        return new ResponseEntity<>(orderOfUser ,HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<PageableResponse<OrderDto>> getAllOrder(
            @RequestParam(value = "pageNumber" ,defaultValue = "0" , required = false) int pageNumber,
            @RequestParam(value = "pageSize" ,defaultValue = "10" , required = false) int pageSize,
            @RequestParam(value = "sortBy" ,defaultValue = "orderId" , required = false) String sortBy,
            @RequestParam(value = "sortDir" ,defaultValue = "ASC" , required = false) String sortDir
    ){
        PageableResponse<OrderDto> allOrders = orderService.getAllOrders(pageNumber, pageSize, sortBy, sortDir);
        return new ResponseEntity<>(allOrders , HttpStatus.OK);
    }

}

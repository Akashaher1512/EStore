package com.java.electro.store.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderDto {
    private String orderId;
    private String orderStatus = "PENDING";
    private String paymentStatus = "NOTPAID";
    private int orderAmount;
    private String billingAddress;
    private String billingPhone;
    private String billingName;
    private Date orderdDate = new Date();
    private Date diliverdDate;
    private List<OrderItemDto> orderItems = new ArrayList<>();
}

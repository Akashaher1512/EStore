package com.java.electro.store.dto;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderItemDto {
    private int orderItemId;
    private int quantity;
    private int totalPrise;
    private ProductDto product;

}

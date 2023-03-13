package com.java.electro.store.dto;

import com.java.electro.store.validate.ImageNameValid;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class ProductDto {
    private String productId;
    private String title;
    private String description;
    private int price;
    private int discountedPrice;
    private int quantity;
    private Date addedDate;
    private boolean live;
    private boolean stock;
    @ImageNameValid
    private String productImageName;

    private CategoryDto category;

}

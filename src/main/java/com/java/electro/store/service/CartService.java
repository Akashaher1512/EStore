package com.java.electro.store.service;

import com.java.electro.store.dto.AddItemToCartRequest;
import com.java.electro.store.dto.CartDto;

public interface CartService {

    // add item to cart
    //case 1 : cart for user is not available :=> then create the cart then add the item
    //case 2 : if cart available then add the item to cart

    CartDto addItemToCart(String userId , AddItemToCartRequest request);

    // remove item from cart
    void removeItemFromCart(String userId , int cartItemId);

    void clearCart(String userId);

    CartDto getCartByUser(String userId);

}

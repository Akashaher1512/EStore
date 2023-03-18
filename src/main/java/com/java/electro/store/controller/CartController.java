package com.java.electro.store.controller;

import com.java.electro.store.dto.AddItemToCartRequest;
import com.java.electro.store.dto.ApiResponseMessage;
import com.java.electro.store.dto.CartDto;
import com.java.electro.store.service.CartService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carts")
@Api(value = "CartController" , description = "REST apis related Cart Management Activity")
public class CartController {

    @Autowired
    private CartService cartService;

    // add item to cart
    @PostMapping("/{userId}")
    public ResponseEntity<CartDto> addItemToCart(@PathVariable String userId ,@RequestBody AddItemToCartRequest addItemToCartRequest){
        CartDto cartDto = cartService.addItemToCart(userId, addItemToCartRequest);
        return  new ResponseEntity<>(cartDto , HttpStatus.OK);
    }

    // remove item from cart
    @DeleteMapping("/{userId}/items/{itemId}")
    public ResponseEntity<ApiResponseMessage> removeItemFromCart(@PathVariable String userId , @PathVariable int itemId){
        cartService.removeItemFromCart(userId ,itemId);
        ApiResponseMessage responseMessage = ApiResponseMessage.builder().message("Cart deleted successfully..!!").success(true).status(HttpStatus.OK).build();

        return new ResponseEntity<>(responseMessage , HttpStatus.OK);
    }

    // clear cart
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponseMessage> clearCart(@PathVariable String userId ){
        cartService.clearCart(userId);
        ApiResponseMessage responseMessage = ApiResponseMessage.builder().message("Cart deleted successfully..!!").success(true).status(HttpStatus.OK).build();

        return new ResponseEntity<>(responseMessage , HttpStatus.OK);
    }

    // get cart by user
    @GetMapping("/{userId}")
    public ResponseEntity<CartDto> getCartByUser(@PathVariable String userId ){
        CartDto cartDto = cartService.getCartByUser(userId);
        return  new ResponseEntity<>(cartDto , HttpStatus.OK);
    }


}

package com.java.electro.store.service.impl;

import com.java.electro.store.dto.AddItemToCartRequest;
import com.java.electro.store.dto.CartDto;
import com.java.electro.store.entity.Cart;
import com.java.electro.store.entity.CartItem;
import com.java.electro.store.entity.Product;
import com.java.electro.store.entity.User;
import com.java.electro.store.exception.BadApiRequest;
import com.java.electro.store.exception.ResourceNotFoundException;
import com.java.electro.store.repository.CartItemRepository;
import com.java.electro.store.repository.CartRepository;
import com.java.electro.store.repository.ProductRepository;
import com.java.electro.store.repository.UserRepository;
import com.java.electro.store.service.CartService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CartDto addItemToCart(String userId, AddItemToCartRequest request) {

        int quantity = request.getQuantity();
        String productId = request.getProductId();

        if(quantity <= 0){
            throw new BadApiRequest("Requested quantity Should greater than zero..!!");
        }

        // fetch the product
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found in database..!!"));

        // fetch user
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user not found with given user_Id : " + userId));

        // fetch cart
        Cart cart = null;

        try{
             cart = cartRepository.findByUser(user).get();
        }
        catch (NoSuchElementException e){
            cart = new Cart();
            cart.setCartId(UUID.randomUUID().toString());
            cart.setCreatedAt(new Date());

        }

        // perform cart operation
        // if cartItem already present in cart
        AtomicReference<Boolean> updated = new AtomicReference<>(false);

        List<CartItem> items = cart.getItems();

         items = items.stream().map(item -> {
            if (item.getProduct().getProductId().equals(productId)) {
                item.setQuantity(quantity);
                item.setTotalPrice(quantity * product.getDiscountedPrice());
                updated.set(true);
            }
            return item;
        }).collect(Collectors.toList());

        // cart.setItems(updatedItems);

        // create items if not already present
        if(!updated.get()){
            CartItem cartItem = CartItem.builder().quantity(quantity).totalPrice(quantity * product.getDiscountedPrice()).cart(cart).product(product).build();
            cart.getItems().add(cartItem);
        }

        cart.setUser(user);

        Cart updatedCart = cartRepository.save(cart);

        return mapTODto(updatedCart);
    }

    @Override
    public void removeItemFromCart(String userId, int cartItemId) {

        // check user have cart or not
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(() -> new ResourceNotFoundException("cart item not found..!!"));

        cartItemRepository.delete(cartItem);

    }

    @Override
    public void clearCart(String userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user not found with given user_Id : " + userId));
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new ResourceNotFoundException("cart of given user not found..!!"));
        cart.getItems().clear();

        cartRepository.save(cart);

    }

    @Override
    public CartDto getCartByUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user not found with given user_Id : " + userId));
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new ResourceNotFoundException("cart of given user not found..!!"));

        return mapTODto(cart);
    }

    // map to dto
    private CartDto mapTODto(Cart cart ){
        return modelMapper.map(cart , CartDto.class);
    }
    // map to Entity
    private Cart mapToEntity(CartDto cartDto){
        return  modelMapper.map(cartDto ,Cart.class);
    }
}

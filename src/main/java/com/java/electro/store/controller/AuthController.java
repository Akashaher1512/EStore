package com.java.electro.store.controller;

import com.java.electro.store.dto.JwtRequest;
import com.java.electro.store.dto.JwtResponse;
import com.java.electro.store.dto.UserDto;
import com.java.electro.store.exception.BadApiRequest;
import com.java.electro.store.security.JwtHelper;
import com.java.electro.store.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtHelper helper;

    @GetMapping("/current")
    public ResponseEntity<UserDto> getCurrentUser(Principal principal){
        String name= principal.getName();
        return  new ResponseEntity<>(modelMapper.map(userDetailsService.loadUserByUsername(name) , UserDto.class), HttpStatus.OK);
    }

    @PostMapping("/login")
    public  ResponseEntity<JwtResponse> login(@RequestBody JwtRequest request){
        doAuthenticate(request.getEmail() , request.getPassword());
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = helper.generateToken(userDetails);

        UserDto user = modelMapper.map(userDetails, UserDto.class);

        JwtResponse response = JwtResponse.builder().jwtToken(token).user(user).build();

        return new ResponseEntity<>(response ,HttpStatus.OK);
    }

    private void doAuthenticate(String email ,String password){
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, password);
        try {
            manager.authenticate(authentication);
        }catch (BadCredentialsException e){
            e.printStackTrace();
            throw new BadApiRequest("Invalid UserName or Password exception");
        }
    }


}

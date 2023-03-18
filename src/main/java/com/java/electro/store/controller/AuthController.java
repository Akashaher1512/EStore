package com.java.electro.store.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.java.electro.store.dto.JwtRequest;
import com.java.electro.store.dto.JwtResponse;
import com.java.electro.store.dto.UserDto;
import com.java.electro.store.entity.User;
import com.java.electro.store.exception.BadApiRequest;
import com.java.electro.store.security.JwtHelper;
import com.java.electro.store.service.UserService;
import io.swagger.annotations.Api;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@Api(value = "Authentication Controller" , description = "REST apis related Authentication Activity")
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

    private Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Value("${googleClientId}")
    private String googleClientId;

    @Value("${newPassword}")
    private String newPassword;

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


    // login with Google
    @PostMapping("/google")
    public ResponseEntity<JwtResponse> loginWithGoogle(@RequestBody Map<String , Object> data) throws IOException {
        // get id token from request
        String idToken = data.get("idToken").toString();

        NetHttpTransport netHttpTransport = new NetHttpTransport();
        JacksonFactory jacksonFactory = JacksonFactory.getDefaultInstance();

        GoogleIdTokenVerifier.Builder verifier = new GoogleIdTokenVerifier.Builder(netHttpTransport, jacksonFactory).setAudience(Collections.singleton(googleClientId));

        GoogleIdToken googleIdToken = GoogleIdToken.parse(verifier.getJsonFactory() , idToken);

        GoogleIdToken.Payload payload = googleIdToken.getPayload();

        logger.info("payload is  {}", payload);

        String email = payload.getEmail();

        User user = null;

        user = userService.findUserByOptional(email).orElseThrow(null);

        if(user == null){
            user = saveUser(email , data.get("name").toString(), data.get("photoUrl").toString());
        }

        ResponseEntity<JwtResponse> jwtResponseResponseEntity = login(JwtRequest.builder().email(user.getEmail()).password(newPassword).build());
        return jwtResponseResponseEntity;
    }

    private User saveUser(String email, String name, String photoUrl) {
        UserDto userDto = UserDto.builder()
                .name(name)
                .email(email)
                .password(newPassword)
                .imageName(photoUrl)
                .roles(new HashSet<>())
                .build();

        UserDto savedUser = userService.createUser(userDto);

         return modelMapper.map(savedUser , User.class);
    }


}

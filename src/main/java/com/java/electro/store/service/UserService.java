package com.java.electro.store.service;

import com.java.electro.store.dto.PageableResponse;
import com.java.electro.store.dto.UserDto;
import com.java.electro.store.entity.User;

import java.util.List;

public interface UserService {

    //create
    UserDto createUser(UserDto userDto);

    //update
    UserDto updateUser(UserDto userDto , String userId);

    //delete
    void deleteUser(String userId);

    //get all users
    PageableResponse<UserDto> getAllUser(int pageNumber , int pageSize , String sortBy , String sortDir);

    // get single user by id
    UserDto getUserById(String userId);

    //get single user by email
    UserDto getUserByEmail(String email);

    //search user
    List<UserDto> searchUser(String keyword);
    // other user specific features
}

package com.java.electro.store.service.impl;

import com.java.electro.store.dto.PageableResponse;
import com.java.electro.store.dto.UserDto;
import com.java.electro.store.entity.User;
import com.java.electro.store.exception.ResourceNotFoundException;
import com.java.electro.store.helper.Helper;
import com.java.electro.store.repository.UserRepository;
import com.java.electro.store.service.UserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Value("${user.profile.image.path}")
    private String imagePath;

    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    @Override
    public UserDto createUser(UserDto userDto) {

        //set unique id in string formay
        String userId = UUID.randomUUID().toString();

        userDto.setId(userId);

        User user = DtoToUser(userDto);

        User savedUser = userRepository.save(user);

        return userToDto(savedUser);
    }

    @Override
    public UserDto updateUser(UserDto userDto, String userId) {

        User user = userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("User not found with given id..!"));
        user.setName(userDto.getName());
        user.setAbout(userDto.getAbout());
        user.setGender(userDto.getGender());
        user.setPassword(userDto.getPassword());
        user.setImageName(userDto.getImageName());

        User updatedUser = userRepository.save(user);

        return userToDto(updatedUser);
    }

    @Override
    public void deleteUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("User not found with given id..!"));

        // delete image from path before deleting user
        // image/user/abc.png
        String fullPath = imagePath + user.getImageName();
        logger.info("Path of img : {} " , fullPath);
        try{
            Path path = Paths.get(fullPath);
            logger.info("Path of img : {} " , path);
            Files.delete(path);
        }
        catch (NoSuchElementException e){
            logger.info("User image not found in folder");
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }

        // delete user
        userRepository.delete(user);

    }

    @Override
    public PageableResponse<UserDto> getAllUser(int pageNumber , int pageSize , String sortBy , String sortDir) {

        Sort sort;

        if(sortDir.equalsIgnoreCase("ASC"))
        {
            sort = Sort.by(sortBy).ascending();

        } else if (sortDir.equalsIgnoreCase("DSC"))
        {
            sort = Sort.by(sortBy).descending();
        }
        else{
            sort = Sort.by(sortBy).ascending();
        }

        Pageable pageable = PageRequest.of(pageNumber ,pageSize ,sort);

        Page<User> page =  userRepository.findAll(pageable);

        // by using helper class convert Entity to Pageable Response
        PageableResponse<UserDto> response = Helper.getPageableResponse(page, UserDto.class);

        return response;
    }

    @Override
    public UserDto getUserById(String userId) {
        User user = userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("User not found with given id..!"));

        return userToDto(user);
    }

    @Override
    public UserDto getUserByEmail(String email) {
        User user =  userRepository.findByEmail(email).orElseThrow(()-> new ResourceNotFoundException("User not found ith given email id..!"));
        return userToDto(user);
    }

    @Override
    public List<UserDto> searchUser(String keyword) {
        List<User> users = userRepository.findByNameContaining(keyword);

        return users.stream().map((user)-> userToDto(user)).collect(Collectors.toList());
    }

    private User DtoToUser(UserDto userDto){

        return modelMapper.map(userDto , User.class);
    }

    private UserDto userToDto(User user){

        return modelMapper.map(user , UserDto.class);
    }

}

package com.java.electro.store.controller;

import com.java.electro.store.dto.ApiResponseMessage;
import com.java.electro.store.dto.ImageResponse;
import com.java.electro.store.dto.PageableResponse;
import com.java.electro.store.dto.UserDto;
import com.java.electro.store.service.FileService;
import com.java.electro.store.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/users")
@Api(value = "userController" , description = "REST apis related User Management Activity")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private FileService fileService;

    @Value("${user.profile.image.path}")
    private  String imageUploadPath;

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    //create
    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto){
        UserDto user =  userService.createUser(userDto);

        return new ResponseEntity<>(user , HttpStatus.CREATED);
    }

    //update
    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(@Valid @RequestBody UserDto userDto ,@PathVariable String userId){
        UserDto user = userService.updateUser(userDto , userId);
        return new ResponseEntity<>(user , HttpStatus.OK);
    }

    //delete
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponseMessage> deleteUser(@PathVariable String userId){
        userService.deleteUser(userId);
        ApiResponseMessage msg = ApiResponseMessage.builder()
                .message(String.format("User with userId : %s deleted successfully..!" , userId))
                .success(true)
                .status(HttpStatus.OK)
                .build();
        return new ResponseEntity<>( msg , HttpStatus.OK);
    }

    //get all
    @GetMapping
    @ApiOperation(value = "Get All Users" ,response = ResponseEntity.class , tags = {"user-controller","user-apis"})
    public ResponseEntity<PageableResponse<UserDto>> getAllUsers(
            @RequestParam(value = "pageNumber" ,defaultValue = "0" , required = false) int pageNumber,
            @RequestParam(value = "pageSize" ,defaultValue = "10" , required = false) int pageSize,
            @RequestParam(value = "sortBy" ,defaultValue = "name" , required = false) String sortBy,
            @RequestParam(value = "sortDir" ,defaultValue = "ASC" , required = false) String sortDir
    ){
        PageableResponse<UserDto> response = userService.getAllUser(pageNumber, pageSize, sortBy, sortDir);

        return new ResponseEntity<>(response , HttpStatus.OK);
    }

    //get by id
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getByUserId(@PathVariable String userId){
        UserDto user = userService.getUserById(userId);
        return new ResponseEntity<>(user , HttpStatus.OK);
    }

    //get by email
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDto> getByEmail(@PathVariable String email){
        UserDto user = userService.getUserByEmail(email);
        return new ResponseEntity<>(user , HttpStatus.OK);
    }

    //search user
    @GetMapping("/search/{keywords}")
    public ResponseEntity<List<UserDto>> searchUser(@PathVariable String keywords){
        List<UserDto> users = userService.searchUser(keywords);
        return new ResponseEntity<>(users , HttpStatus.OK);
    }

    // upload user image
    @PostMapping("/image/{userId}")
    public ResponseEntity<ImageResponse> uploadUserImage(
            @RequestParam("userImage") MultipartFile image,
            @PathVariable String userId
    ) throws IOException {

        String imageName = fileService.uploadImage(image, imageUploadPath);
        ImageResponse imageResponse = ImageResponse.builder().imageName(imageName).message("Image Uploaded successfully..!!").success(true).status(HttpStatus.CREATED).build();

        // update user set user image
        UserDto user = userService.getUserById(userId);
        user.setImageName(imageName);
        UserDto userDto = userService.updateUser(user, userId);

        return new ResponseEntity<>(imageResponse , HttpStatus.CREATED);

    }

    // serve user image
    @GetMapping("/image/{userId}")
    public void serveUserImage(@PathVariable String userId , HttpServletResponse response) throws IOException {

        // get user by id
        UserDto user = userService.getUserById(userId);

        // print image name to console
        logger.info("User Image Name : {} ", user.getImageName() );

        // get image  from file location
        InputStream resource = fileService.getResource(imageUploadPath, user.getImageName());

        // set type of image
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);

        StreamUtils.copy(resource , response.getOutputStream());
    }

}
























package com.java.electro.store.dto;

import com.java.electro.store.entity.Role;
import com.java.electro.store.validate.ImageNameValid;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private String id;
    @Size(min = 3 , max =20 ,message = "Invalid Name..!!")
    private String name;
   // @Email(message = "Invalid Email id..!!")
    @Pattern(regexp = "^[a-z0-9][-a-z0-9._]+@([-a-z0-9]+\\.)+[a-z]{2,5}$" , message = "Invalid Email id..!!")
    @NotBlank(message = "Email is Required..!!")
    private String email;
    @NotBlank(message = "Password is Required..!!")
    private String password;
    @Size(min = 4 , max = 6 ,message = "Invalid Gender..!!")
    private String gender;
    @NotBlank(message = "Invalid about, write something about yourself..!!")
    private String about;
    // @pattern
    // Custom validator
    @ImageNameValid
    private String imageName;

    private Set<RoleDto> roles = new HashSet<>();

}

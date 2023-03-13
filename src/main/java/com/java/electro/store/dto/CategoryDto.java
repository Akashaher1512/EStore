package com.java.electro.store.dto;

import com.java.electro.store.validate.ImageNameValid;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {

    private String categoryId;

    @NotBlank(message = "title should not be blank..!!")
    @Size(min = 2 , message = "title must be of minimum 4 characters..!!")
    private String title;

    @NotBlank(message = "Description required..!!")
    private String description;

    @ImageNameValid
    private String coverImage;


}

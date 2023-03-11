package com.java.electro.store.dto;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDto {

    private String categoryId;

    @NotBlank(message = "title should not be blank..!!")
    @Size(min = 2 , message = "title must be of minimum 4 characters..!!")
    private String title;

    @NotBlank(message = "Description required..!!")
    private String description;

    private String coverImage;
}

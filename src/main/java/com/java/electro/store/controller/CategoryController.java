package com.java.electro.store.controller;

import com.java.electro.store.dto.*;
import com.java.electro.store.service.CategoryService;
import com.java.electro.store.service.FileService;
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
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    private Logger logger = LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    private FileService fileService;

    @Value("${category.coverImage.path}")
    private String imageUploadPath;

    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CategoryDto categoryDto){
        CategoryDto category = categoryService.createCategory(categoryDto);
        return new ResponseEntity<>(category , HttpStatus.CREATED);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> updateCategory(@Valid @RequestBody CategoryDto categoryDto ,@PathVariable String categoryId){
        CategoryDto category = categoryService.updateCategory(categoryDto, categoryId);
        return new ResponseEntity<>(category , HttpStatus.OK);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponseMessage> deleteCategory(@PathVariable String categoryId){
        categoryService.deleteCategory(categoryId);
        ApiResponseMessage responseMessage = ApiResponseMessage.builder().message("Category deleted successfully..!!").success(true).status(HttpStatus.OK).build();

        return new ResponseEntity<>(responseMessage , HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<PageableResponse<CategoryDto>> getAllCategory(
            @RequestParam(value = "pageNumber" ,defaultValue = "0" , required = false) int pageNumber,
            @RequestParam(value = "pageSize" ,defaultValue = "10" , required = false) int pageSize,
            @RequestParam(value = "sortBy" ,defaultValue = "title" , required = false) String sortBy,
            @RequestParam(value = "sortDir" ,defaultValue = "ASC" , required = false) String sortDir
    ){
        PageableResponse<CategoryDto> response = categoryService.getAllCategory(pageNumber, pageSize, sortBy, sortDir);

        return new ResponseEntity<>(response , HttpStatus.OK);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable String categoryId){
        CategoryDto category = categoryService.getCategoryById(categoryId);
        return new ResponseEntity<>(category , HttpStatus.OK);
    }

    @GetMapping("/search/{keyword}")
    public ResponseEntity<List<CategoryDto>> searchCategory(@PathVariable String keyword){
        List<CategoryDto> categoryDtos = categoryService.searchCategory(keyword);

        return new ResponseEntity<>(categoryDtos , HttpStatus.OK);
    }


    // upload image
    @PostMapping("/image/{categoryId}")
    public ResponseEntity<ImageResponse> uploadCategoryImage(@RequestParam("categoryImage") MultipartFile image,
                                                             @PathVariable String categoryId) throws IOException {
        String imageName = fileService.uploadImage(image, imageUploadPath);
        ImageResponse imageResponse = ImageResponse.builder().imageName(imageName).message("Image Uploaded successfully..!!").success(true).status(HttpStatus.CREATED).build();

        // update category set category image
        CategoryDto category = categoryService.getCategoryById(categoryId);
        category.setCoverImage(imageName);
        CategoryDto categoryDto = categoryService.updateCategory(category, categoryId);

        return new ResponseEntity<>(imageResponse , HttpStatus.CREATED);
    }
    // serve image
    @GetMapping("/image/{categoryId}")
    public void serveCategoryImage(@PathVariable String categoryId , HttpServletResponse response) throws IOException {

        // get category by id
        CategoryDto category = categoryService.getCategoryById(categoryId);

        // print image name to console
        logger.info("category Image Name : {} ", category.getCoverImage() );

        // get image  from file location
        InputStream resource = fileService.getResource(imageUploadPath, category.getCoverImage());

        // set type of image
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);

        StreamUtils.copy(resource , response.getOutputStream());
    }
}

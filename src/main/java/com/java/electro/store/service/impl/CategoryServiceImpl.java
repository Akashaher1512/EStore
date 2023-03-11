package com.java.electro.store.service.impl;

import com.java.electro.store.dto.CategoryDto;
import com.java.electro.store.dto.PageableResponse;
import com.java.electro.store.entity.Category;
import com.java.electro.store.entity.User;
import com.java.electro.store.exception.ResourceNotFoundException;
import com.java.electro.store.helper.Helper;
import com.java.electro.store.repository.CategoryRepository;
import com.java.electro.store.service.CategoryService;
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
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Value("${category.coverImage.path}")
    private String imageUploadPath;

    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {

        // creating category id randomly
        String categoryId = UUID.randomUUID().toString();
        categoryDto.setCategoryId(categoryId);

        Category category = mapToEntity(categoryDto);
        Category savedCategory = categoryRepository.save(category);

        return mapToDto(savedCategory);
    }

    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto, String categoryId) {

        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found with id : " + categoryId));

        category.setTitle(categoryDto.getTitle());
        category.setDescription(categoryDto.getDescription());
        category.setCoverImage(categoryDto.getCoverImage());

        Category updatedCategory = categoryRepository.save(category);

        return mapToDto(updatedCategory);
    }

    @Override
    public void deleteCategory(String categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found with id : " + categoryId));

        String fullPath = imageUploadPath + category.getCoverImage();
        logger.info("Path of img : {} " , fullPath);
        try{
            Path path = Paths.get(fullPath);
            logger.info("Path of img : {} " , path);
            Files.delete(path);
        }
        catch (NoSuchElementException e){
            logger.info("Category image not found in folder");
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }

        categoryRepository.delete(category);
    }

    @Override
    public PageableResponse<CategoryDto> getAllCategory(int pageNumber , int pageSize , String sortBy , String sortDir) {
        Sort sort;
        if(sortDir.equalsIgnoreCase("ASC")){
           sort = Sort.by(sortBy).ascending();
        }else if(sortDir.equalsIgnoreCase("DESC")){
            sort = Sort.by(sortBy).descending();
        }
        else{
            sort = Sort.by(sortBy).ascending();
        }

        Pageable pageable = PageRequest.of(pageNumber , pageSize ,sort);

        Page<Category> page = categoryRepository.findAll(pageable);

        PageableResponse<CategoryDto> response = Helper.getPageableResponse(page, CategoryDto.class);

        return response;
    }

    @Override
    public CategoryDto getCategoryById(String categoryId) {

        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found with id : " + categoryId));

        return mapToDto(category);
    }

    @Override
    public List<CategoryDto> searchCategory(String keyword) {

        List<Category> categoryList = categoryRepository.findByTitleContaining(keyword);

        return categoryList.stream().map((category)-> mapToDto(category)).collect(Collectors.toList());
    }

    // category to categoryDto
    private CategoryDto mapToDto(Category category){
        return modelMapper.map(category, CategoryDto.class);
    }
    //categoryDto to category
    private Category mapToEntity(CategoryDto categoryDto){
        return modelMapper.map(categoryDto , Category.class);
    }
}

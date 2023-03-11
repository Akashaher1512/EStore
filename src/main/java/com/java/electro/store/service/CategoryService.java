package com.java.electro.store.service;

import com.java.electro.store.dto.CategoryDto;
import com.java.electro.store.dto.PageableResponse;
import com.java.electro.store.dto.UserDto;
import com.java.electro.store.entity.Category;

import java.util.List;

public interface CategoryService {
    //create
    CategoryDto createCategory(CategoryDto categoryDto);
    //update
    CategoryDto updateCategory(CategoryDto categoryDto , String categoryId);
    //delete
    void deleteCategory(String categoryId);
    //get all
    PageableResponse<CategoryDto> getAllCategory(int pageNumber , int pageSize , String sortBy , String sortDir);
    //get single category detail
    CategoryDto getCategoryById(String categoryId);
    //search
    List<CategoryDto> searchCategory(String keyword);
}

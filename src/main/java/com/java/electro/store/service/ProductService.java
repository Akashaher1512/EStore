package com.java.electro.store.service;

import com.java.electro.store.dto.PageableResponse;
import com.java.electro.store.dto.ProductDto;

import java.util.List;

public interface ProductService {

    ProductDto createProduct(ProductDto productDto);

    ProductDto updateProduct(ProductDto productDto , String productId);

    void deleteProduct(String productDto);

    ProductDto getProductById(String productId);

    PageableResponse<ProductDto> getAllProducts(int pageNumber ,int pageSize , String sortBy , String sortDir);

    PageableResponse<ProductDto> getAllLiveProducts(int pageNumber ,int pageSize , String sortBy , String sortDir);

    PageableResponse<ProductDto> searchByTitle(String subTitle , int pageNumber ,int pageSize , String sortBy , String sortDir);

}

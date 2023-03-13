package com.java.electro.store.service.impl;

import com.java.electro.store.dto.CategoryDto;
import com.java.electro.store.dto.PageableResponse;
import com.java.electro.store.dto.ProductDto;
import com.java.electro.store.entity.Category;
import com.java.electro.store.entity.Product;
import com.java.electro.store.exception.ResourceNotFoundException;
import com.java.electro.store.helper.Helper;
import com.java.electro.store.repository.CategoryRepository;
import com.java.electro.store.repository.ProductRepository;
import com.java.electro.store.service.ProductService;
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
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ModelMapper modelMapper;
    private Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Value("${product.image.path}")
    private String imagePath;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public ProductDto createProduct(ProductDto productDto) {

        // generate product id
        String productId = UUID.randomUUID().toString();

        // generate added date
        Date addedDate = new Date();

        productDto.setProductId(productId);
        productDto.setAddedDate(addedDate);

        Product product = productRepository.save(mapToEntity(productDto));

        return mapToDto(product);
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto, String productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product Not fount with id : " + productId));

        product.setDescription(productDto.getDescription());
        product.setLive(productDto.isLive());
        product.setPrice(productDto.getPrice());
        product.setTitle(productDto.getTitle());
        product.setQuantity(productDto.getQuantity());
        product.setStock(productDto.isStock());
        product.setDiscountedPrice(productDto.getDiscountedPrice());
        product.setProductImageName(productDto.getProductImageName());

        Product savedProduct = productRepository.save(product);

        return mapToDto(savedProduct);
    }

    @Override
    public void deleteProduct(String productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product Not fount with id : " + productId));

        // delete product images
        String filePath = imagePath + product.getProductImageName();

        try{
            Path path = Path.of(filePath);
            Files.delete(path);
        }
        catch (NoSuchElementException e){
            logger.info("No such image found in file path : {} ",filePath);
            e.printStackTrace();

        }catch (IOException e){
            e.printStackTrace();
        }


        // delete product
        productRepository.delete(product);
    }

    @Override
    public ProductDto getProductById(String productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product Not fount with id : " + productId));

        ProductDto productDto = mapToDto(product);

        return productDto;
    }

    @Override
    public PageableResponse<ProductDto> getAllProducts(int pageNumber, int pageSize, String sortBy, String sortDir) {

        Sort sort;

        if(sortDir.equalsIgnoreCase("ASC")){
            sort = Sort.by(sortBy).ascending();
        } else if (sortDir.equalsIgnoreCase("DSC")) {
            sort = Sort.by(sortBy).descending();
        }
        else {
            sort = Sort.by(sortBy).ascending();
        }

        Pageable pageable = PageRequest.of(pageNumber ,pageSize , sort);

        Page<Product> products = productRepository.findAll(pageable);

        PageableResponse<ProductDto> pageableResponse = Helper.getPageableResponse(products, ProductDto.class);

        return pageableResponse;
    }

    @Override
    public PageableResponse<ProductDto> getAllLiveProducts(int pageNumber, int pageSize, String sortBy, String sortDir) {

        Sort sort;

        if(sortDir.equalsIgnoreCase("ASC")){
            sort = Sort.by(sortBy).ascending();
        } else if (sortDir.equalsIgnoreCase("DSC")) {
            sort = Sort.by(sortBy).descending();
        }
        else {
            sort = Sort.by(sortBy).ascending();
        }

        Pageable pageable = PageRequest.of(pageNumber ,pageSize , sort);

        Page<Product> products = productRepository.findByLiveTrue(pageable);

        PageableResponse<ProductDto> pageableResponse = Helper.getPageableResponse(products, ProductDto.class);

        return pageableResponse;
    }

    @Override
    public PageableResponse<ProductDto> searchByTitle(String subTitle, int pageNumber, int pageSize, String sortBy, String sortDir) {

        Sort sort;

        if(sortDir.equalsIgnoreCase("ASC")){
            sort = Sort.by(sortBy).ascending();
        } else if (sortDir.equalsIgnoreCase("DSC")) {
            sort = Sort.by(sortBy).descending();
        }
        else {
            sort = Sort.by(sortBy).ascending();
        }

        Pageable pageable = PageRequest.of(pageNumber ,pageSize , sort);

        Page<Product> products = productRepository.findByTitleContaining(subTitle, pageable);

        PageableResponse<ProductDto> pageableResponse = Helper.getPageableResponse(products, ProductDto.class);

        return pageableResponse;

    }

    @Override
    public ProductDto createWithCategory(ProductDto productDto, String categoryId) {
        // fetch the category to check its exists or not
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("category not found with given category_id : " + categoryId));

        Product product = mapToEntity(productDto);

        // generate product id and generated date
        String productId = UUID.randomUUID().toString();
        Date addedDate = new Date();

        product.setProductId(productId);
        product.setAddedDate(addedDate);
        // set category
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);

        return mapToDto(savedProduct);
    }

    @Override
    public ProductDto updateProductCategory(String productId, String categoryId) {

        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product with given id not found  Product_id : " + productId));

        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("category not found with given category_id : " + categoryId));

        product.setCategory(category);

        Product savedProduct = productRepository.save(product);

        return mapToDto(savedProduct);
    }

    @Override
    public PageableResponse<ProductDto> getAllOfCategory(String categoryId , int pageNumber, int pageSize, String sortBy, String sortDir) {

        Sort sort;

        if(sortDir.equalsIgnoreCase("ASC")){
            sort = Sort.by(sortBy).ascending();
        } else if (sortDir.equalsIgnoreCase("DSC")) {
            sort = Sort.by(sortBy).descending();
        }
        else {
            sort = Sort.by(sortBy).ascending();
        }

        Pageable pageable = PageRequest.of(pageNumber ,pageSize , sort);

        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("category not found with given category_id : " + categoryId));

        Page<Product> products = productRepository.findByCategory(category, pageable);

        PageableResponse<ProductDto> pageableResponse = Helper.getPageableResponse(products, ProductDto.class);

        return pageableResponse;
    }


    // map to dto
    private ProductDto mapToDto(Product product){
        return modelMapper.map(product , ProductDto.class);
    }

    // map to entity
    private Product mapToEntity(ProductDto productDto){
        return modelMapper.map(productDto , Product.class);
    }
}


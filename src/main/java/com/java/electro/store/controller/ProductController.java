package com.java.electro.store.controller;

import com.java.electro.store.dto.ApiResponseMessage;
import com.java.electro.store.dto.ImageResponse;
import com.java.electro.store.dto.PageableResponse;
import com.java.electro.store.dto.ProductDto;
import com.java.electro.store.repository.ProductRepository;
import com.java.electro.store.service.FileService;
import com.java.electro.store.service.ProductService;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    private Logger logger = LoggerFactory.getLogger(ProductRepository.class);

    @Autowired
    private FileService fileService;

    @Value("${product.image.path}")
    private String imagePath;

    // create
    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto productDto){
        ProductDto product = productService.createProduct(productDto);
        return  new ResponseEntity<>(product , HttpStatus.CREATED);
    }

    //update
    @PutMapping("/{productId}")
    public ResponseEntity<ProductDto> updateProduct(@RequestBody ProductDto productDto ,@PathVariable String productId){
        ProductDto product = productService.updateProduct(productDto, productId);
        return  new ResponseEntity<>(product , HttpStatus.OK);
    }

    // delete
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponseMessage> deleteProduct(@PathVariable String productId){
        productService.deleteProduct(productId);
        ApiResponseMessage responseMessage = ApiResponseMessage.builder().message("Product deleted successfully..!! with product_id : " + productId).status(HttpStatus.OK).success(true).build();
        return  new ResponseEntity<>(responseMessage , HttpStatus.OK);
    }

    // get by id
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable String productId){
        ProductDto product = productService.getProductById(productId);
        return  new ResponseEntity<>(product , HttpStatus.OK);
    }

    // get all
    @GetMapping()
    public ResponseEntity<PageableResponse> getAllProducts(
            @RequestParam(value = "pageNumber" ,defaultValue = "0" ,required = false) int pageNumber,
            @RequestParam(value = "pageSize" ,defaultValue = "10" ,required = false) int pageSize,
            @RequestParam(value = "sortBy" ,defaultValue = "title" ,required = false) String sortBy,
            @RequestParam(value = "sortDir" ,defaultValue = "ASC" ,required = false) String sortDir
    ){
        PageableResponse<ProductDto> products = productService.getAllProducts(pageNumber, pageSize, sortBy, sortDir);
        return  new ResponseEntity<>(products , HttpStatus.OK );
    }

    // get live
    @GetMapping("/live")
    public ResponseEntity<PageableResponse> getAllLiveProducts(
            @RequestParam(value = "pageNumber" ,defaultValue = "0" ,required = false) int pageNumber,
            @RequestParam(value = "pageSize" ,defaultValue = "10" ,required = false) int pageSize,
            @RequestParam(value = "sortBy" ,defaultValue = "title" ,required = false) String sortBy,
            @RequestParam(value = "sortDir" ,defaultValue = "ASC" ,required = false) String sortDir
    ){
        PageableResponse<ProductDto> products = productService.getAllLiveProducts(pageNumber, pageSize, sortBy, sortDir);
        return  new ResponseEntity<>(products  ,HttpStatus.OK);
    }

    // search by title
    @GetMapping("/search/{subTitle}")
    public ResponseEntity<PageableResponse> searchProductByTitle(
            @PathVariable String subTitle,
            @RequestParam(value = "pageNumber" ,defaultValue = "0" ,required = false) int pageNumber,
            @RequestParam(value = "pageSize" ,defaultValue = "10" ,required = false) int pageSize,
            @RequestParam(value = "sortBy" ,defaultValue = "title" ,required = false) String sortBy,
            @RequestParam(value = "sortDir" ,defaultValue = "ASC" ,required = false) String sortDir
    ){
        PageableResponse<ProductDto> products = productService.searchByTitle(subTitle, pageNumber, pageSize, sortBy, sortDir);

        return  new ResponseEntity<>(products ,HttpStatus.OK);
    }

    // upload image
    @PostMapping("/image/{productId}")
    public ResponseEntity<ImageResponse> uploadProductImage(
            @PathVariable String productId,
            @RequestParam ("productImage") MultipartFile image
            ) throws IOException {
        String fileName = fileService.uploadImage(image, imagePath);

        ProductDto product = productService.getProductById(productId);

        product.setProductImageName(fileName);

        ProductDto updateProduct = productService.updateProduct(product, productId);

        ImageResponse imageResponse = ImageResponse.builder().imageName(updateProduct.getProductImageName()).message("Product Image Uploaded successfully..!!").status(HttpStatus.OK).success(true).build();

        return new ResponseEntity<>(imageResponse , HttpStatus.OK);
    }

    // serve image
    @GetMapping("/image/{productId}")
    public void serveProductImage(@PathVariable String productId , HttpServletResponse response) throws IOException {
        ProductDto product = productService.getProductById(productId);

        logger.info("user Image name :{} " ,product.getProductImageName());

        InputStream resource = fileService.getResource(imagePath , product.getProductImageName());
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);

        StreamUtils.copy(resource , response.getOutputStream());

    }
}

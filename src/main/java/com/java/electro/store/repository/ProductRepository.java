package com.java.electro.store.repository;

import com.java.electro.store.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product , String> {
    List<Product> findByTitleContaining(String subTitle);

    Page<Product> findByTitleContaining(String subTitle,Pageable pageable);

    List<Product> findByLiveTrue();

    Page<Product> findByLiveTrue(Pageable pageable);


}

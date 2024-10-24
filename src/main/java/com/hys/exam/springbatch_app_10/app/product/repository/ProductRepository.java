package com.hys.exam.springbatch_app_10.app.product.repository;

import com.hys.exam.springbatch_app_10.app.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
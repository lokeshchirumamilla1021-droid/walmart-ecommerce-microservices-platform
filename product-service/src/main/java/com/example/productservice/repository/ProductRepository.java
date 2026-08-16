package com.example.productservice.repository;
import com.example.productservice.entity.Product;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;


@Repository

public interface ProductRepository extends JpaRepository<Product, Long> {
    //Product tells repository to work with product entity
    //Long is a datatype of ProductID
    //JpaRepository automatically provides methods such as save(),findAll(),findById()
}

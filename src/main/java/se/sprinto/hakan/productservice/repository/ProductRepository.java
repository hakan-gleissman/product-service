package se.sprinto.hakan.productservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.sprinto.hakan.productservice.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}

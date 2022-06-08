package br.com.ecommerce.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.ecommerce.domain.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

	
}

package br.com.vinharia.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.vinharia.domain.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

	
}

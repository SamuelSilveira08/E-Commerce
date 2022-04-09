package br.com.vinharia.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.vinharia.domain.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

	
}

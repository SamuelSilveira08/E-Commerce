package br.com.vinharia.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.vinharia.domain.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {

}

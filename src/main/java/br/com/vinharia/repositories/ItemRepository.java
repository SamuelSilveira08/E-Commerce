package br.com.vinharia.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.vinharia.domain.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
	
	public Page<Item> findAllByCartId(Integer id, Pageable pageable);

}

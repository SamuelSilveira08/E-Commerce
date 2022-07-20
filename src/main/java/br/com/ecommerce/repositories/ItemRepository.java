package br.com.ecommerce.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.ecommerce.domain.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
	
	public Page<Item> findAllByCartId(Integer id, Pageable pageable);
	public List<Item> findByIdIn(List<Integer> ids);

}

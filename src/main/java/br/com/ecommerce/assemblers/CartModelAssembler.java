package br.com.ecommerce.assemblers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import br.com.ecommerce.controllers.CartController;
import br.com.ecommerce.domain.Cart;
import br.com.ecommerce.domain.UserPrincipal;
import br.com.ecommerce.dto.CartDTO;

public class CartModelAssembler implements RepresentationModelAssembler<Cart, CartDTO> {
	
	@Autowired
	private ModelMapper mapper;
	
	@Autowired
	private UserPrincipal userPrincipal;

	@Override
	public CartDTO toModel(Cart entity) {
		CartDTO cartDTO = mapper.map(entity, CartDTO.class);
		Link selfLink = linkTo(methodOn(CartController.class).getCartById(cartDTO.getId(), userPrincipal)).withSelfRel();
		cartDTO.add(selfLink);
		return cartDTO;
	}

}

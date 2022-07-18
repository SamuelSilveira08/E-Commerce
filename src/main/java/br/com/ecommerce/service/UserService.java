package br.com.ecommerce.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import br.com.ecommerce.domain.User;
import br.com.ecommerce.domain.UserPrincipal;
import br.com.ecommerce.dto.ChangePasswordDTO;
import br.com.ecommerce.dto.UserDTO;
import br.com.ecommerce.exceptions.BadRequestException;
import br.com.ecommerce.exceptions.ForbiddenException;
import br.com.ecommerce.exceptions.InvalidPasswordException;
import br.com.ecommerce.exceptions.NotFoundException;
import br.com.ecommerce.exceptions.UsernameAlreadyExistsException;
import br.com.ecommerce.repositories.UserRepository;
import br.com.ecommerce.utils.PasswordUtils;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ModelMapper mapper;

	/**
	 * Return all users' data
	 * 
	 * @author Samuel
	 * 
	 * @param pageNum     number of the page to be returned
	 * @param numberItems number of items per page
	 * @param sortBy      User class' field to sort data by
	 * @return list with all users by page identified by pageNum parameters (number
	 *         of items is up to numberItems parameter) sorted by sortBy parameter
	 * @throws BadRequestException if sortBy parameter is not a field of User class
	 */

	public List<UserDTO> getUsers(Integer pageNum, Integer numberItems, String sortBy) {
		try {
			User.class.getDeclaredField(sortBy);
		} catch (NoSuchFieldException e) {
			throw new BadRequestException("Field %s not found.".formatted(sortBy));
		}
		Pageable pageable = PageRequest.of(pageNum, numberItems, Sort.by(sortBy));
		Page<User> page = userRepository.findAll(pageable);
		List<User> result = page.hasContent() ? page.getContent() : new ArrayList<>();
		List<UserDTO> userDtos = Arrays.asList(mapper.map(result, UserDTO[].class));
		return userDtos;
	}

	/**
	 * Return a single user's data
	 * 
	 * @author Samuel
	 * 
	 * @param id            identifier of the user to retrieve data
	 * @param userPrincipal authenticated user
	 * @return DTO containing data from given user
	 * @throws ForbiddenException if authenticated user is not the user being
	 *                            searched
	 * @throws NotFoundException  if there is no user with given id in database
	 */

	public UserDTO getUser(Integer id, UserPrincipal userPrincipal) {
		if (id == userPrincipal.getUser().getId() || userPrincipal.isAdmin()) {
			User result = userRepository.findById(id)
					.orElseThrow(() -> new NotFoundException("User with id %d not found".formatted(id)));
			UserDTO userDto = mapper.map(result, UserDTO.class);
			return userDto;
		} else {
			throw new ForbiddenException("You don't have permission to access another user's data");
		}
	}

	/**
	 * Save a user in database
	 * 
	 * @author Samuel
	 * 
	 * @param userDTO containing user's data to be persisted
	 * @return persisted user's data (generated id included)
	 */

	public UserDTO saveUser(UserDTO userDTO) {
		// should encode after mapping
		String encodedPassword = PasswordUtils.encode(userDTO.getPassword());
		userDTO.setPassword(encodedPassword);
		
		// Cannot register an admin user through public endpoint
		userDTO.setAdmin(false);
		
		User user = mapper.map(userDTO, User.class);
		
		// Check whether username is already in database
		if(userRepository.checkIfUsernameAlreadyExists(user.getEmail()) == 1) {
			throw new UsernameAlreadyExistsException("Oops! It seems like this email is already in our database");
		};
		
		user = userRepository.save(user);
		userDTO = mapper.map(user, UserDTO.class);
		return userDTO;
	}

	/**
	 * Update an user's data
	 * 
	 * @param userDTO       containing user's data to be updated
	 * @param id            identifier of the user to be updated
	 * @param userPrincipal authenticated user
	 * @return updated user's data
	 * @throws ForbiddenException if authenticated user is not the user being
	 *                            updated
	 * @throws NotFoundException  if there is no users with given id in database
	 */

	public UserDTO updateUser(UserDTO userDTO, Integer id, UserPrincipal userPrincipal) {
		String email = userRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("User with given id %d not found".formatted(id))).getEmail();
		
		/* Line below checks if user is an admin (that can update any user's data) or if user being updated is
		 * the same user that is logged in ('cause normal users can only updated their own data) 
		 */
		if (email.equals(userPrincipal.getUsername()) || userPrincipal.isAdmin()) {
			
			/* Below code checks whether user is admin and, if positive, checks if admin is trying to update themself
			 * or another user and sets admin field accordingly
			 */
			
			if(userPrincipal.isAdmin()) {
				if(email.equals(userPrincipal.getUsername())) {
					userDTO.setAdmin(true);
				}
			} else {
				userDTO.setAdmin(false);
			}
			
			userDTO.setId(id);
			User user = mapper.map(userDTO, User.class);
			String encodedPassword = PasswordUtils.encode(user.getPassword());
			user.setPassword(encodedPassword);
			user = userRepository.save(user);
			userDTO = mapper.map(user, UserDTO.class);
			return userDTO;
		} else {
			throw new ForbiddenException("You don't have permission to update a user's data");
		}
	}

	/**
	 * Delete an user from database
	 * 
	 * @author Samuel
	 * 
	 * @param id            identifier of the user to be deleted
	 * @param userPrincipal authenticated user
	 * @throws NotFoundException  if there is no users with given id in database
	 * @throws ForbiddenException if authenticated user is not the user being
	 *                            deleted
	 */

	public void deleteUser(Integer id, UserPrincipal userPrincipal) {
		if (id == userPrincipal.getUser().getId() || userPrincipal.isAdmin()) {
			User userToDelete = userRepository.findById(id)
					.orElseThrow(() -> new NotFoundException("User with id %d not found".formatted(id)));
			userRepository.delete(userToDelete);
		} else {
			throw new ForbiddenException("You don't have permission to delete a user from the database");
		}
	}

	/**
	 * Change an user's password
	 * 
	 * @author Samuel
	 * 
	 * @param passwordDTO   containing the new password and the current user's
	 *                      password
	 * @param userPrincipal authenticated user
	 * @throws InvalidPasswordException if the current password doesn't match to the
	 *                                  authenticated user's password or if the new
	 *                                  password is the same as the current password
	 */

	public void changePassword(ChangePasswordDTO passwordDTO, UserPrincipal userPrincipal) {
		if (PasswordUtils.matches(passwordDTO.getCurrentPassword(), userPrincipal.getPassword())) {
			if (!PasswordUtils.matches(passwordDTO.getNewPassword(), userPrincipal.getPassword())) {
				userPrincipal.getUser().setPassword(PasswordUtils.encode(passwordDTO.getNewPassword()));
				userRepository.save(userPrincipal.getUser());
			} else {
				throw new InvalidPasswordException("The new password cannot be equal to the current password");
			}
		} else {
			throw new InvalidPasswordException("Wrong password");
		}
	}

}

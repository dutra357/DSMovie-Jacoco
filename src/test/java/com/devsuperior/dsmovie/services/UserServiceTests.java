package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.repositories.UserRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import com.devsuperior.dsmovie.tests.UserFactory;
import com.devsuperior.dsmovie.utils.CustomUserUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
public class UserServiceTests {

	@InjectMocks
	private UserService service;

	@Mock
	private UserRepository userRepository;
	@Mock
	private CustomUserUtil userUtil;

	private UserEntity user;
	private String validUsername, invalidusername;

	@BeforeEach
	void setUp() throws Exception {
		user = UserFactory.createUserEntity();

		validUsername = "maria@gmail.com";
		invalidusername = "joao@invalid.com";

		Mockito.when(userRepository.findByUsername(validUsername)).thenReturn(Optional.ofNullable(user));
		Mockito.when(userRepository.findByUsername(invalidusername)).thenReturn(Optional.empty());

	}


	@Test
	public void authenticatedShouldReturnUserEntityWhenUserExists() {
		Mockito.when(userUtil.getLoggedUsername()).thenReturn(validUsername);
		UserEntity user = service.authenticated();

		Assertions.assertNotNull(user);
		Assertions.assertEquals(user.getName(), "Maria");
		Mockito.verify(userUtil, Mockito.times(1)).getLoggedUsername();
		Mockito.verify(userRepository, Mockito.times(1)).findByUsername(validUsername);
	}

	@Test
	public void authenticatedShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {
		Mockito.when(userUtil.getLoggedUsername()).thenReturn(invalidusername);

		Assertions.assertThrows(UsernameNotFoundException.class, () -> {
			UserEntity result = service.authenticated();

			Assertions.assertNull(result);
		});

		Mockito.verify(userUtil, Mockito.times(1)).getLoggedUsername();
		Mockito.verify(userRepository, Mockito.times(1)).findByUsername(invalidusername);
	}

	@Test
	public void loadUserByUsernameShouldReturnUserDetailsWhenUserExists() {
	}

	@Test
	public void loadUserByUsernameShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {
	}
}

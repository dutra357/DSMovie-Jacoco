package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.dto.ScoreDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.entities.ScoreEntity;
import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.repositories.ScoreRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import com.devsuperior.dsmovie.tests.ScoreFactory;
import com.devsuperior.dsmovie.tests.UserFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.mockito.Mockito.times;

@ExtendWith(SpringExtension.class)
public class ScoreServiceTests {
	
	@InjectMocks
	private ScoreService service;

	@Mock
	private ScoreRepository scoreRepository;
	@Mock
	private UserService userService;
	@Mock
	private MovieRepository movieRepository;

	private ScoreEntity score;
	private ScoreDTO scoreDTO;
	private Long existsId, nonExistsId;
	private MovieEntity movie;
	private MovieDTO movieDTO;
	private UserEntity user;

	@BeforeEach
	void setUp() throws Exception {
		score = ScoreFactory.createScoreEntity();
		scoreDTO = ScoreFactory.createScoreDTO();
		movie = MovieFactory.createMovieEntity();
		movieDTO = MovieFactory.createMovieDTO();

		user = UserFactory.createUserEntity();

		existsId = 1L;
		nonExistsId = 99L;

		Mockito.when(movieRepository.findById(existsId)).thenReturn(Optional.ofNullable(movie));
		Mockito.when(movieRepository.findById(nonExistsId)).thenReturn(Optional.empty());

		Mockito.when(userService.authenticated()).thenReturn(user);

		Mockito.when(scoreRepository.saveAndFlush(ArgumentMatchers.any())).thenReturn(score);

		Mockito.when(movieRepository.save(ArgumentMatchers.any())).thenReturn(movie);
	}
	
	@Test
	public void saveScoreShouldReturnMovieDTO() {
		MovieDTO result = service.saveScore(scoreDTO);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(scoreDTO.getMovieId(), result.getId());
		Assertions.assertEquals("Test Movie", result.getTitle());

		Mockito.verify(movieRepository, times(1)).findById(scoreDTO.getMovieId());
		Mockito.verify(scoreRepository, times(1)).saveAndFlush(ArgumentMatchers.any());
	}
	
	@Test
	public void saveScoreShouldThrowResourceNotFoundExceptionWhenNonExistingMovieId() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			scoreDTO.setMovieId(nonExistsId);
			service.saveScore(scoreDTO);
		});

		Mockito.verify(movieRepository, Mockito.times(1)).findById(scoreDTO.getMovieId());
		Mockito.verify(scoreRepository, Mockito.times(0)).saveAndFlush(score);
	}
}

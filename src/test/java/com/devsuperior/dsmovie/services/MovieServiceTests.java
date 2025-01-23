package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.times;

@ExtendWith(SpringExtension.class)
public class MovieServiceTests {
	
	@InjectMocks
	private MovieService service;

	@Mock
	private MovieRepository repository;

	private String parametro;
	private PageImpl<MovieEntity> page;
	private MovieEntity movie;
	private MovieDTO movieDto;
	private Long existsId, nonExistsId;


	@BeforeEach
	void setUp() throws Exception {
		parametro = "The";
		movie = MovieFactory.createMovieEntity();
		movieDto = MovieFactory.createMovieDTO();

		page = new PageImpl<>(List.of(movie));

		existsId = 1L;
		nonExistsId = 888L;

		Mockito.when(repository.searchByTitle(ArgumentMatchers.any(), (Pageable) ArgumentMatchers.any()))
				.thenReturn(page);

		Mockito.when(repository.findById(existsId)).thenReturn(Optional.ofNullable(movie));
		Mockito.when(repository.findById(nonExistsId)).thenReturn(Optional.empty());

		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(movie);



	}

	@Test
	public void findAllShouldReturnPagedMovieDTO() {
		Pageable pageable = PageRequest.of(0, 12);
		Page<MovieDTO> result = service.findAll(parametro, pageable);

		Assertions.assertNotNull(result);
		Assertions.assertEquals("Test Movie", result.getContent().get(0).getTitle());
		Mockito.verify(repository, times(1)).searchByTitle(parametro, pageable);

	}
	
	@Test
	public void findByIdShouldReturnMovieDTOWhenIdExists() {
		MovieDTO movieDTO = service.findById(existsId);

		Assertions.assertNotNull(movieDTO);
		Assertions.assertEquals("Test Movie", movieDTO.getTitle());
		Mockito.verify(repository, times(1)).findById(existsId);
	}
	
	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findById(nonExistsId);
		});

		Mockito.verify(repository, Mockito.times(1)).findById(nonExistsId);
	}
	
	@Test
	public void insertShouldReturnMovieDTO() {
		MovieDTO inserted = service.insert(movieDto);

		Assertions.assertNotNull(inserted);
		Assertions.assertEquals("Test Movie", inserted.getTitle());
		Mockito.verify(repository, times(1)).save(ArgumentMatchers.any());
	}
	
	@Test
	public void updateShouldReturnMovieDTOWhenIdExists() {
	}
	
	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
	}
	
	@Test
	public void deleteShouldDoNothingWhenIdExists() {
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
	}
	
	@Test
	public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
	}
}

package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.services.exceptions.DatabaseException;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
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
	private Long existsId, nonExistsId, dependentId;


	@BeforeEach
	void setUp() throws Exception {
		parametro = "The";
		movie = MovieFactory.createMovieEntity();
		movieDto = MovieFactory.createMovieDTO();

		page = new PageImpl<>(List.of(movie));

		existsId = 1L;
		nonExistsId = 888L;
		dependentId = 1000L;

		Mockito.when(repository.searchByTitle(ArgumentMatchers.any(), (Pageable) ArgumentMatchers.any()))
				.thenReturn(page);

		Mockito.when(repository.findById(existsId)).thenReturn(Optional.ofNullable(movie));
		Mockito.when(repository.findById(nonExistsId)).thenReturn(Optional.empty());

		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(movie);

		Mockito.when(repository.getReferenceById(existsId)).thenReturn(movie);
		Mockito.when(repository.getReferenceById(nonExistsId)).thenThrow(EntityNotFoundException.class);

		Mockito.when(repository.existsById(existsId)).thenReturn(true);
		Mockito.when(repository.existsById(nonExistsId)).thenReturn(false);
		Mockito.when(repository.existsById(dependentId)).thenReturn(true);

		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);


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
		MovieDTO updated = service.update(existsId, movieDto);

		Assertions.assertNotNull(updated);
		Assertions.assertEquals("Test Movie", updated.getTitle());
		Mockito.verify(repository, times(1)).save(ArgumentMatchers.any());
	}
	
	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.update(nonExistsId, movieDto);
		});

		Mockito.verify(repository, Mockito.times(1)).getReferenceById(nonExistsId);
	}
	
	@Test
	public void deleteShouldDoNothingWhenIdExists() {
		Assertions.assertDoesNotThrow(() -> {
			service.delete(existsId);
		});

		Mockito.verify(repository, Mockito.times(1)).deleteById(existsId);
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistsId);
		});

		Mockito.verify(repository, Mockito.times(1)).existsById(nonExistsId);
		Mockito.verify(repository, Mockito.times(0)).deleteById(nonExistsId);
	}
	
	@Test
	public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
		Assertions.assertThrows(DatabaseException.class, () -> {
			service.delete(dependentId);
		});

		Mockito.verify(repository, Mockito.times(1)).existsById(dependentId);
		Mockito.verify(repository, Mockito.times(1)).deleteById(dependentId);
	}
}

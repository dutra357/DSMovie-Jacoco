package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.controllers.MovieController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.services.exceptions.DatabaseException;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class MovieService {

	@Autowired
	private MovieRepository repository;

	@Transactional(readOnly = true)
	public Page<MovieDTO> findAll(String title, Pageable pageable) {
		Page<MovieEntity> result = repository.searchByTitle(title, pageable);
		return result.map(x -> new MovieDTO(x)
				.add(linkTo(methodOn(MovieController.class).findAll(null, null)).withSelfRel())
				.add(linkTo(methodOn(MovieController.class).findById(x.getId())).withRel("Get movie by ID")));
	}

	@Transactional(readOnly = true)
	public MovieDTO findById(Long id) {
		MovieEntity result = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado"));
		return new MovieDTO(result)
				.add(linkTo(methodOn(MovieController.class).findById(id)).withSelfRel())
				.add(linkTo(methodOn(MovieController.class).findAll(null, null)).withRel("All movies"))
				.add(linkTo(methodOn(MovieController.class).update(id, null)).withRel("Update movie"))
				.add(linkTo(methodOn(MovieController.class).delete(id)).withRel("Delete movie"));
	}

	@Transactional
	public MovieDTO insert(MovieDTO dto) {
		MovieEntity entity = new MovieEntity();
		copyDtoToEntity(dto, entity);
		entity = repository.save(entity);
		return new MovieDTO(entity)
				.add(linkTo(methodOn(MovieController.class).findById(entity.getId())).withSelfRel())
				.add(linkTo(methodOn(MovieController.class).findAll(null, null)).withRel("All movies"))
				.add(linkTo(methodOn(MovieController.class).update(entity.getId(), null)).withRel("Update movie"))
				.add(linkTo(methodOn(MovieController.class).delete(entity.getId())).withRel("Delete movie"));
	}

	@Transactional
	public MovieDTO update(Long id, MovieDTO dto) {
		try {
			MovieEntity entity = repository.getReferenceById(id);
			copyDtoToEntity(dto, entity);
			entity = repository.save(entity);
			return new MovieDTO(entity)
					.add(linkTo(methodOn(MovieController.class).findById(entity.getId())).withSelfRel())
					.add(linkTo(methodOn(MovieController.class).findAll(null, null)).withRel("All movies"))
					.add(linkTo(methodOn(MovieController.class).delete(entity.getId())).withRel("Delete movie"));

		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Recurso não encontrado");
		}
	}

	public void delete(Long id) {
		if (!repository.existsById(id))
			throw new ResourceNotFoundException("Recurso não encontrado");
		try {
			repository.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Falha de integridade referencial");
		}
	}

	private void copyDtoToEntity(MovieDTO dto, MovieEntity entity) {
		entity.setTitle(dto.getTitle());
		entity.setScore(dto.getScore());
		entity.setCount(dto.getCount());
		entity.setImage(dto.getImage());
	}
}
package com.devsuperior.dsmovie.controllers;

import java.net.URI;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.services.MovieService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/movies")
@Tag(name = "Movies", description = "Movies CRUD")
public class MovieController {

	@Autowired
	private MovieService service;

	@Operation(
			description = "Get all movies",
			summary = "Paged list of all movies.",
			responses = {
					@ApiResponse(description = "OK", responseCode = "200")
			}
	)
	@GetMapping(produces = "application/json")
	public Page<MovieDTO> findAll(
			@RequestParam(value="title", defaultValue = "") String title, 
			Pageable pageable) {
		return service.findAll(title, pageable);
	}

	@Operation(
			description = "Get movie by id",
			summary = "Get movie by id.",
			responses = {
					@ApiResponse(description = "OK", responseCode = "200"),
					@ApiResponse(description = "NotFound", responseCode = "404")
			}
	)
	@GetMapping(value = "/{id}", produces = "application/json")
	public MovieDTO findById(@PathVariable Long id) {
		return service.findById(id);
	}

	@Operation(
			description = "Update a movie",
			summary = "Update a movie.",
			responses = {
					@ApiResponse(description = "Created", responseCode = "201"),
					@ApiResponse(description = "BadRequest", responseCode = "400"),
					@ApiResponse(description = "NotFound", responseCode = "404"),
					@ApiResponse(description = "Unauthorized", responseCode = "401"),
					@ApiResponse(description = "Forbidden", responseCode = "403"),
					@ApiResponse(description = "UnProcessableEntity", responseCode = "422")
			}
	)
	@SecurityRequirement(name = "BearerAuth")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping(produces = "application/json")
	public ResponseEntity<MovieDTO> insert(@Valid @RequestBody MovieDTO dto) {
		dto = service.insert(dto);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.getId()).toUri();
		return ResponseEntity.created(uri).body(dto);
	}

	@Operation(
			description = "Update a movie",
			summary = "Update a movie.",
			responses = {
					@ApiResponse(description = "Ok", responseCode = "200"),
					@ApiResponse(description = "BadRequest", responseCode = "400"),
					@ApiResponse(description = "NotFound", responseCode = "404"),
					@ApiResponse(description = "Unauthorized", responseCode = "401"),
					@ApiResponse(description = "Forbidden", responseCode = "403"),
					@ApiResponse(description = "UnProcessableEntity", responseCode = "422")
			}
	)
	@SecurityRequirement(name = "BearerAuth")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PutMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<MovieDTO> update(@PathVariable Long id, @Valid @RequestBody MovieDTO dto) {
		dto = service.update(id, dto);
		return ResponseEntity.ok().body(dto);
	}

	@Operation(
			description = "Update a movie",
			summary = "Update a movie.",
			responses = {
					@ApiResponse(description = "NoContent", responseCode = "204"),
					@ApiResponse(description = "NotFound", responseCode = "404"),
					@ApiResponse(description = "Unauthorized", responseCode = "401"),
					@ApiResponse(description = "Forbidden", responseCode = "403")
			}
	)
	@SecurityRequirement(name = "BearerAuth")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@DeleteMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<MovieDTO> delete(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
}

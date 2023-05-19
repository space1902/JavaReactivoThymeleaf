package com.webflux.app.services;

import com.webflux.app.documents.Categoria;
import com.webflux.app.documents.Producto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface productoService {

	public Flux<Producto> findAll();
	
	public Flux<Producto> findAllConNombre();
	
	public Flux<Producto> findAllConNombreUpperRepeat();
	
	public Mono<Producto> findById(String id);
	
	public Mono<Producto> save(Producto producto);
	
	public Mono<Void> delete(Producto producto);
	
	public Flux<Categoria> findAllCategoria();
	
	public Mono<Categoria> findCategoriaById(String id);
	
	public Mono<Categoria> saveCategoria(Categoria categoria);
}

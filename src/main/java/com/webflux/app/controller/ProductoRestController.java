package com.webflux.app.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webflux.app.SpringBootWebfluxApplication;
import com.webflux.app.dao.ProductoDao;
import com.webflux.app.documents.Producto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/productos")
public class ProductoRestController {

	@Autowired
	private ProductoDao productDao;
	
	private static final Logger log = LoggerFactory.getLogger(SpringBootWebfluxApplication.class);
	
	@GetMapping()
	public Flux<Producto> index(){
	Flux<Producto> productos = productDao.findAll().map(prod ->{
		prod.setNombre(prod.getNombre().toUpperCase());
		return prod;
	}).doOnNext(prod -> log.info(prod.getNombre()));
	
	return productos;	
	}
	
	@GetMapping("/{id}")
	public Mono<Producto> show(@PathVariable String id){
	//Mono<Producto> producto = productDao.findById(id);
	Flux<Producto> productos = productDao.findAll();
	Mono<Producto> producto = productos.filter(p -> p.getId().equals(id)).next();
	return producto;	
	}
	
}

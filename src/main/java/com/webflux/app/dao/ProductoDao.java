package com.webflux.app.dao;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import com.webflux.app.documents.Producto;

public interface ProductoDao extends ReactiveMongoRepository<Producto, String>{

	
}

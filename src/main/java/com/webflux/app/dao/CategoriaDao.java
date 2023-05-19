package com.webflux.app.dao;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.webflux.app.documents.Categoria;

public interface CategoriaDao extends ReactiveMongoRepository<Categoria, String>{

}

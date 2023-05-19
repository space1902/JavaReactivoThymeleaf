package com.webflux.app.documents;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Document(collection = "productos")
public class Producto {
	
	@Id
	private String id;
	
	@NotEmpty
	private String nombre;
	@NotNull
	private Double precio;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date createAt;
	@Valid
	private Categoria categoria;
	
	private String foto;
	
	public Producto() {
		super();
	}

	public Producto(String nombre, Double precio, Categoria categoria) {
		this(nombre, precio);
		this.categoria = categoria;
		
	}
	
	public Producto(String nombre, Double precio) {
		this.nombre = nombre;
		this.precio = precio;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public Double getPrecio() {
		return precio;
	}
	public void setPrecio(Double precio) {
		this.precio = precio;
	}
	public Date getCreateAt() {
		return createAt;
	}
	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}
	
	
	
}

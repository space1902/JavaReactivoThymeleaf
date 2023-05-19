package com.webflux.app.controller;

import java.io.File;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.thymeleaf.spring6.context.webflux.ReactiveDataDriverContextVariable;

import com.webflux.app.documents.Categoria;
import com.webflux.app.documents.Producto;
import com.webflux.app.services.productoService;

import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SessionAttributes("producto")
@Controller
public class ProductoController {

	@Autowired
	private productoService productService;
	
	@Value("${config.uploads.path}")
	private String path;
	
	@ModelAttribute("categorias")
	public Flux<Categoria> categoria(){
		return productService.findAllCategoria();
	}

	private static final Logger log = LoggerFactory.getLogger(ProductoController.class);
	
	@GetMapping({"/listar", "/"})
	public String listar(Model model) {
		Flux<Producto> productos = productService.findAllConNombreUpperRepeat();
		
		productos.subscribe(prod -> log.info(prod.getNombre()));
		model.addAttribute("productos", productos);
		model.addAttribute("titulo", "listado de productos");
		return "listar";
	}
	
	@GetMapping("/form")
	public Mono<String> crear(Model model){
		model.addAttribute("producto", new Producto());
		model.addAttribute("titulo", "Formulario de productos");
		model.addAttribute("boton", "Crear");
		return Mono.just("form");
	}
	
	@PostMapping("/form")
	public Mono<String> guardar(@Valid Producto producto, BindingResult result, Model model, @RequestPart FilePart file, SessionStatus status){
		
		if(result.hasErrors()) {

			model.addAttribute("titulo", "Errores en formulario producto");
			model.addAttribute("boton", "Guardar");
			return Mono.just("form");
		}else {
			
		status.setComplete();
		
		Mono<Categoria> categoria = productService.findCategoriaById(producto.getCategoria().getId());
		return categoria.flatMap(c ->{

			if(producto.getCreateAt() == null) {
				producto.setCreateAt(new Date());
			}
			
			if(!file.filename().isEmpty()) {
				producto.setFoto(UUID.randomUUID().toString() + "-" + file.filename()
				.replace(" ", "")
				.replace(":", "")
				.replace("\\", ""));
			}
			producto.setCategoria(c);
			return productService.save(producto);
		}).doOnNext(p ->{
			log.info("producto guardado: " + p.getNombre() + " Id: " + p.getId());
		})
				.flatMap(p -> {
					if(!file.filename().isEmpty()) {
						return file.transferTo(new File(path + p.getFoto()));
					}
					return Mono.empty();
				})
				.thenReturn("redirect:/listar");

		}
	}
	
	@GetMapping("/eliminar/{id}")
	public Mono<String> eliminar(@PathVariable String id){
		return productService.findById(id)

				.defaultIfEmpty(new Producto())
				.flatMap(p-> {
					if(p.getId() == null) {
						return Mono.error(new InterruptedException("No existe el producto a eliminar"));
					}
					return Mono.just(p);
				})
				
				.flatMap(p -> {
			return productService.delete(p);
		}).then(Mono.just("redirect:/listar?success=producto+eliminadi+con+exito"))
				.onErrorResume(ex -> Mono.just("redirect:/listar?error=no+existe+el+producto+a+eliminar"));
	}
	
	@GetMapping("/form/{id}")
	public Mono<String> editar(@PathVariable String id, Model model){
		Mono<Producto> productoMono = productService.findById(id).doOnNext(p->{
			log.info("producto " + p.getNombre());
		}).defaultIfEmpty(new Producto());
		
		model.addAttribute("titulo", "Editar producto");
		model.addAttribute("boton", "Editar");
		model.addAttribute("producto", productoMono);
		
		return Mono.just("form");
	}
	
	@GetMapping("/form-v2/{id}")
	public Mono<String> editarV2(@PathVariable String id, Model model){
		return  productService.findById(id).doOnNext(p->{
			log.info("producto " + p.getNombre());
			model.addAttribute("titulo", "Editar producto");
			model.addAttribute("boton", "Editar");
			model.addAttribute("producto", p);
		}).defaultIfEmpty(new Producto())
				.flatMap(p-> {
					if(p.getId() == null) {
						return Mono.error(new InterruptedException("No existe el producto"));
					}
					return Mono.just(p);
				})
				.then(Mono.just("form"))
				.onErrorResume(ex -> Mono.just("redirect:/listar?error=no+existe+el+producto"));
		
		
	}

	@GetMapping("/listar-datadriver")
	public String listarDatadriver(Model model) {
		Flux<Producto> productos = productService.findAllConNombre().delayElements(Duration.ofSeconds(1));
		
		productos.subscribe(prod -> log.info(prod.getNombre()));
		model.addAttribute("productos", new ReactiveDataDriverContextVariable(productos, 2));
		model.addAttribute("titulo", "listado de productos");
		return "listar";
	}

	@GetMapping("/listar-full")
	public String listarFull(Model model) {
		Flux<Producto> productos = productService.findAll().map(prod ->{
			prod.setNombre(prod.getNombre().toUpperCase());
			return prod;
		}).repeat(5000);
		
		model.addAttribute("productos", new ReactiveDataDriverContextVariable(productos, 2));
		model.addAttribute("titulo", "listado de productos");
		return "listar";
	}

	@GetMapping("/listar-chunked")
	public String listarChunked(Model model) {
		Flux<Producto> productos = productService.findAllConNombreUpperRepeat();
		
		model.addAttribute("productos", new ReactiveDataDriverContextVariable(productos, 2));
		model.addAttribute("titulo", "listado de productos");
		return "listar-chunked";
	}
}

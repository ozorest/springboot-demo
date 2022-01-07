package br.com.zozorest.sprbootdemo;

import org.hibernate.annotations.Comment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.persistence.Entity;
import javax.persistence.Id;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SprbootDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SprbootDemoApplication.class, args);
	}

}

@RestController
@RequestMapping("/coffees")
class RestApiDemoController {
	private final CoffeeRepository coffeeRepository;

	public RestApiDemoController(CoffeeRepository coffeeRepository) {
		this.coffeeRepository = coffeeRepository;


	}

	@GetMapping
	Iterable<Coffee> getCoffees() {
		return coffeeRepository.findAll();
	}

	@GetMapping("/{id}")
	Optional<Coffee> getCoffeeById(@PathVariable String id) {
		return coffeeRepository.findById(id);
	}

	@PostMapping
	Coffee postCoffee(@RequestBody Coffee coffee) {
		return coffeeRepository.save(coffee);
	}

	@PutMapping("/{id}")
	ResponseEntity<Coffee> putCoffee(@PathVariable String id,
									 @RequestBody Coffee coffee) {

		return (coffeeRepository.existsById(id)) ?
				new ResponseEntity<>(coffeeRepository.save(coffee), HttpStatus.OK) :
				new ResponseEntity<>(coffeeRepository.save(coffee), HttpStatus.CREATED);
	}

	@DeleteMapping("/{id}")
	void deleteCoffee(@PathVariable String id) {
		coffeeRepository.deleteById(id);
	}
}

@RestController
@RequestMapping("/greeting")
class GreetingController {
	@Value("${greeting-name: Padrão}")
	private String name;

	@Value("${greeting-coffee: ${greeting-name} is drinking Café 3 Corações}")
	private String coffee;

	@GetMapping
	String getGreeting() {
		return name;
	}

	@GetMapping("/coffee")
	String getNameAndCoffee() {
		return coffee;
	}
}

@ConfigurationProperties(prefix = "greeting")
class Greeting {
	private String name;
	private String coffee;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCoffee() {
		return coffee;
	}

	public void setCoffee(String coffee) {
		this.coffee = coffee;
	}
}

@Entity
class Coffee {

	@Id
	private String id;
	private String name;

	public Coffee() {}

	public Coffee(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public Coffee(String name) {
		this(UUID.randomUUID().toString(), name);
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}
}

interface CoffeeRepository extends CrudRepository<Coffee, String> {}

@Component
class DataLoader {
	private final CoffeeRepository coffeeRepository;

	public DataLoader(CoffeeRepository coffeeRepository) {
		this.coffeeRepository = coffeeRepository;
	}

	@PostConstruct
	private void loadData() {
		coffeeRepository.saveAll(List.of(
			new Coffee("Café Cereza"),
			new Coffee("Café Ganador"),
			new Coffee("Café Lareño"),
			new Coffee("Café Três Pontas")
		));
	}
}
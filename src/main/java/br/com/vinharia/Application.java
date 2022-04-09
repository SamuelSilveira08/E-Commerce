package br.com.vinharia;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {
	
//	@Autowired
//	private ProductRepository repository;
	
	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		
	}

	
//	@PostConstruct
//	public void postConstruct() {
//		Product p = new Product();
//		p.setName("Vinho tinto suave - 1L");
//		p.setPrice(20.0);
//		p.setStock(100);
//		repository.save(p);
//	}
	 

}

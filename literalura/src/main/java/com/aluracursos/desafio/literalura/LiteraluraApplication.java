package com.aluracursos.desafio.literalura;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LiteraluraApplication implements CommandLineRunner {


    public static void main(String[] args) {
		SpringApplication.run(LiteraluraApplication.class, args);
	}


	@Autowired
	private Main principal;  // Inyectamos la clase Main

	@Override
	public void run(String... args) throws Exception {

		principal.mostrarMenu();
}
}

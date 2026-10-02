package com.efeakif.intibak_control;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class IntibakControlApplication {

	public static void main(String[] args) {

		Dotenv env = Dotenv.configure().ignoreIfMissing().load();
		env.entries().forEach(entry -> {
			System.setProperty(entry.getKey(), entry.getValue());
		});

		SpringApplication.run(IntibakControlApplication.class, args);
	}

}

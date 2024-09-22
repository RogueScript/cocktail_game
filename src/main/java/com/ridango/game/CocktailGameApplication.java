package com.ridango.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication
public class CocktailGameApplication implements CommandLineRunner {
	@Autowired
	private ApiService apiService;
	@Autowired
	private GameService gameService;
	public static void main(String[] args) {
		SpringApplication.run(CocktailGameApplication.class, args);
	}

	@Override public void run(String... args) throws Exception {
		System.out.println("CocktailGameApplication has started successfully!");
		gameService.StartGame();
		System.exit(0);
	}

}

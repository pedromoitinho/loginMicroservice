package com.codecraft.auth;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AuthApplication {
	public static void main(String[] args) {
		try {
			// Try to load .env file (for local development)
			Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
			if (dotenv.get("DB_URL") != null) {
				System.setProperty("DB_URL", dotenv.get("DB_URL"));
				System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
				System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
			}
		} catch (Exception e) {
			// If .env file is not found, environment variables should be provided by Docker
			System.out.println("No .env file found, using environment variables from Docker");
		}

		SpringApplication.run(AuthApplication.class, args);
	}
}

package com.codecraft.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication(scanBasePackages = { "com.codecraft.auth", "com.codecraft.forms" })
@EnableJpaRepositories(basePackages = { "com.codecraft.auth.repository", "com.codecraft.forms.repository" })
@EntityScan(basePackages = { "com.codecraft.auth.entity", "com.codecraft.forms.entity" })
public class AuthApplication {
	@SuppressWarnings("UseSpecificCatch")
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

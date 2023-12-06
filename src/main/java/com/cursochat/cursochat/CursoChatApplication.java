package com.cursochat.cursochat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication @EnableCaching
public class CursoChatApplication {
	public static void main(String[] args) {
		SpringApplication.run(CursoChatApplication.class, args);
	}

}

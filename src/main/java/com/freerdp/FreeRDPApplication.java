package com.freerdp;

import com.freerdp.services.UserDataManager;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class FreeRDPApplication {
	@PostConstruct
	public void customInit() {
		UserDataManager.CreateFreeRDPInstances();
	}

	@PreDestroy
	public void customDestroy() {
		UserDataManager.DeleteFreeRDPInstances();
	}

	public static void main(String[] args) {
		SpringApplication.run(FreeRDPApplication.class, args);
	}

}

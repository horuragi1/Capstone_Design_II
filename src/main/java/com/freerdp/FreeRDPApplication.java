package com.freerdp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.freerdp.services.LibFreeRDP;

@SpringBootApplication
public class FreeRDPApplication {

	public static void main(String[] args) {
		LibFreeRDP.init();
		SpringApplication.run(FreeRDPApplication.class, args);
	}

}

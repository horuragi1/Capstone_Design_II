package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {
	static {
		System.loadLibrary("JNI");
	}

	native static int login(String input);

	public static void main(String[] args) {
		int ret = login("wfreerdp.exe /u:<username> /p:<password> /v:<ip>");
		if(ret== 0)
			System.out.println("login success!");
		else
			System.out.println("login fail! (" + ret + ")");
		SpringApplication.run(DemoApplication.class, args);
	}

}

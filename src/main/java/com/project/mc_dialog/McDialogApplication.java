package com.project.mc_dialog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class McDialogApplication {

	public static void main(String[] args) {
		SpringApplication.run(McDialogApplication.class, args);
	}

}

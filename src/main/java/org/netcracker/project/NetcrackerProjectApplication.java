package org.netcracker.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class NetcrackerProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(NetcrackerProjectApplication.class, args);
	}

}

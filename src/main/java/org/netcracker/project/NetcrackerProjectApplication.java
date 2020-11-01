package org.netcracker.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableSpringDataWebSupport
public class NetcrackerProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(NetcrackerProjectApplication.class, args);
	}

}

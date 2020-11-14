package org.netcracker.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(exclude = { EmbeddedMongoAutoConfiguration.class })
@EnableAsync
@EnableSpringDataWebSupport
public class NetcrackerProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(NetcrackerProjectApplication.class, args);
	}

}

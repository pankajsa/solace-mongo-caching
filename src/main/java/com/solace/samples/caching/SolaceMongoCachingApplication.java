package com.solace.samples.caching;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class SolaceMongoCachingApplication {

	@Autowired
    private MongoManager mongoManager;

	@Autowired
    private MongoCallback mongoCallback;

	
	public static void main(String[] args) {
		SpringApplication.run(SolaceMongoCachingApplication.class, args);
	}

	
	@Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		
        return args -> {
            log.info("Application Started");

            mongoCallback.setupSolaceClient();
            mongoManager.deleteAll();
            mongoManager.send();
            mongoManager.query();
            
            log.info("Application Terminated");
        };
	}

	
}

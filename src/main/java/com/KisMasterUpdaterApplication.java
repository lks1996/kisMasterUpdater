package com;

import com.Service.KisMasterFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@Slf4j
public class KisMasterUpdaterApplication {

    public static void main(String[] args) {
		SpringApplication.run(KisMasterUpdaterApplication.class, args);
	}
}

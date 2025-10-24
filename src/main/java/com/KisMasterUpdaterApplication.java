package com;

import com.Service.KisMasterFileService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KisMasterUpdaterApplication implements CommandLineRunner  {

	private final KisMasterFileService kisMasterFileService;

    public KisMasterUpdaterApplication(KisMasterFileService kisMasterFileService) {
        this.kisMasterFileService = kisMasterFileService;
    }

    public static void main(String[] args) {
		SpringApplication.run(KisMasterUpdaterApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
//		kisMasterFileService.processAllMasterFiles();
	}
}

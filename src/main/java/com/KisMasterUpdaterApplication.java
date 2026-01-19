package com;

import com.Service.KisMasterFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@Slf4j
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
		log.warn("===== [START] Kis Master Updater =====");
		try {
			kisMasterFileService.processAllMasterFiles();
			log.warn("===== [SUCCESS] Kis Master Updater =====");
		} catch (Exception e) {
			log.error("===== [FAIL] An error occurred during the scheduled job =====", e);
		}
		System.exit(0);
	}
}

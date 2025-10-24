package com.Scheduler;

import com.Service.KisMasterFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JobScheduller {

    private final KisMasterFileService kisMasterFileService;

    public JobScheduller(KisMasterFileService kisMasterFileService) {
        this.kisMasterFileService = kisMasterFileService;
    }

    /**
     * cron = "[초] [분] [시] [일] [월] [요일]"
     */
//    @Scheduled(cron = "0 0 0 * * SAT,TUE")
    @Scheduled(cron = "0 5 * * * *")
    public void scheduleWeeklyFilingUpdate() {
        log.warn("===== [START] Kis Master Updater =====");
        try {
            kisMasterFileService.processAllMasterFiles();
            log.warn("===== [SUCCESS] Kis Master Updater =====");
        } catch (Exception e) {
            log.error("===== [FAIL] An error occurred during the scheduled job =====", e);
        }
    }
}

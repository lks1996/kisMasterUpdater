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
     * 상시 구동 후 정해진 시간에 동작하는게 아닌 docker kubernetes cronjob 으로 동작하도록 수정. 26.01.19
     */
    @Scheduled(cron = "0 0 21 * * SAT,TUE")
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

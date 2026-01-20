package com.Scheduler;

import com.Service.KisMasterFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("batch")
@RequiredArgsConstructor
@Slf4j
public class BatchJobRunner implements CommandLineRunner {

    private final KisMasterFileService kisMasterFileService; // 기존에 main에서 호출하던 서비스

    /**
     * profile이 batch인 경유에만 동작.
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
        log.warn("===== [START] Kis Master Updater =====");

        try {
            // [이사 완료] main에 있던 로직을 여기로 옮기세요.
            kisMasterFileService.processAllMasterFiles();

            log.warn("===== [SUCCESS] Kis Master Updater =====");
        } catch (Exception e) {
            log.error("===== [FAIL] An error occurred during the scheduled job =====", e);
            e.printStackTrace();
            // 에러 시 비정상 종료 코드(1) 반환 -> K8s가 실패했음을 인지함
            System.exit(1);
        }
        System.exit(0);
    }
}

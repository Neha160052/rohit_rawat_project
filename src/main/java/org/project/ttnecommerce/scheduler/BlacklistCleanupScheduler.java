package org.project.ttnecommerce.scheduler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.ttnecommerce.repository.BlacklistedTokenRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class BlacklistCleanupScheduler {

    private final BlacklistedTokenRepository blacklistedTokenRepository;

    @Scheduled(cron = "* * * * * *")
    public void cleanBlacklist() {
        log.info("Running blacklist cleanup job...");
        blacklistedTokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());
        log.info("Blacklist cleanup completed");
    }
}
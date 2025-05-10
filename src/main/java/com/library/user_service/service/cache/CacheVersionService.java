package com.library.user_service.service.cache;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class CacheVersionService {

    private final AtomicLong version = new AtomicLong();

    public long getCurrentVersion() {
        return version.get();
    }

    public void incrementVersion() {
        version.incrementAndGet();
    }

    @Scheduled(fixedRate = 86_400_000)
    @SchedulerLock(
            name = "resetDailyCounters",
            lockAtLeastFor = "30s",
            lockAtMostFor = "1m"
    )
    public void resetDailyCounters() {
        version.set(0L);
    }

}

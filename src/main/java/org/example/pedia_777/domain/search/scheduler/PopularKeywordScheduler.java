package org.example.pedia_777.domain.search.scheduler;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pedia_777.domain.search.service.PopularKeywordService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PopularKeywordScheduler {

    private final PopularKeywordService popularKeywordService;

    // 매일 새벽 4시에 실행
    @Scheduled(cron = "0 0 4 * * *", zone = "Asia/Seoul")
    public void scheduleDailyPopularKeywordBackup() {

        LocalDate yesterday = LocalDate.now().minusDays(1);

        log.info("[RankingBackupScheduler] {} 인기 검색어 백업 시작", yesterday);
        popularKeywordService.backupDailyPopularKeywords(yesterday);
    }
}
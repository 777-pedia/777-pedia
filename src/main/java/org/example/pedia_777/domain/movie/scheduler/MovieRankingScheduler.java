package org.example.pedia_777.domain.movie.scheduler;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pedia_777.domain.movie.entity.RankingPeriod;
import org.example.pedia_777.domain.movie.service.MovieRankingService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MovieRankingScheduler {

    private final MovieRankingService movieRankingService;

    // 매일 새벽 4시 10분에 실행
    @Scheduled(cron = "0 10 4 * * *", zone = "Asia/Seoul")
    public void scheduleDailyMovieRankingBackup() {

        LocalDate yesterday = LocalDate.now().minusDays(1);
        log.info("[MovieRankingScheduler] 일간 영화 랭킹 백업 작업을 시작 (날짜: {})", yesterday);
        movieRankingService.backupMovieRankings(yesterday, RankingPeriod.DAILY);
    }

    // 매주 월요일 새벽 4시 20분에 실행
    @Scheduled(cron = "0 20 4 * * MON", zone = "Asia/Seoul")
    public void scheduleWeeklyMovieRankingBackup() {

        LocalDate lastWeekDate = LocalDate.now().minusWeeks(1);
        log.info("[MovieRankingScheduler] 주간 영화 랭킹 백업 작업을 시작 (대상 주차 포함 날짜: {})", lastWeekDate);
        movieRankingService.backupMovieRankings(lastWeekDate, RankingPeriod.WEEKLY);
    }
}

package org.example.pedia_777.domain.search.service;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.StopWatch;

@Slf4j
@ActiveProfiles("local")
//@Disabled("성능 테스트는 필요한 경우 로컬 환경에서만 실행합니다.")
@SpringBootTest
class SearchServicePerformanceTest {

    @Autowired
    private SearchService searchService;

    @Test
    @DisplayName("영화 검색 간단 성능 테스트")
    void searchMovies_performanceTest() {

        String keyword = "위대한";
        int PAGE_SIZE = 30;
        int ITERATIONS = 10;

        PageRequest pageable = PageRequest.of(0, PAGE_SIZE);

        List<Long> executionTimes = new ArrayList<>();

        for (int i = 1; i <= ITERATIONS; i++) {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            var result = searchService.searchMovies(keyword, pageable);

            stopWatch.stop();
            long diff = stopWatch.getTotalTimeMillis();
            executionTimes.add(diff);

            log.info("[{} iteration] 반환한 영화 수 = {}, 실행 시간 = {} ms",
                    i, result.totalElements(), diff);
        }

        // 평균 시간 계산
        double average = executionTimes.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0);

        log.info("{}회 실행, 평균 실행 시간 = {} ms", ITERATIONS, average);
    }
}
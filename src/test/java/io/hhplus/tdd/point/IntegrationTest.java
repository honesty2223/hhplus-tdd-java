package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class IntegrationTest {

    private static final int THREAD_COUNT = 10;
    private static final int POINTS_TO_ADD = 100;

    private UserPoint table;
    private final CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
    private final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);

    @Autowired
    private PointService service;

    @Autowired
    private UserPointTable userPointTable;

    @BeforeEach
    public void setUp() {
        // 테스트를 위한 초기 데이터 설정
        this.table = userPointTable.insertOrUpdate(1, 1000);
    }

    @Test
    @DisplayName("포인트 충전 동시성 테스트")
    public void chargeConcurrencyTest() {
        // when
        for (int i = 0; i < THREAD_COUNT; i++) {
            executorService.submit(() -> {
                synchronized (this) {
                    service.chargePoint(table.id(), POINTS_TO_ADD); // 포인트를 충전하는 비즈니스 로직 호출
                }
                latch.countDown();
            });
        }

        try {
            boolean awaitSuccess = latch.await(10, TimeUnit.SECONDS);   // 모든 스레드가 완료될 때까지 대기
            if (!awaitSuccess) {
                throw new RuntimeException("모든 스레드가 완료되기까지 시간 초과");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("스레드 대기 중 인터럽트 발생", e);
        }

        // then
        this.table = service.getUserPoint(table.id());
        assertEquals(2000, table.point());  // 기대값인 2000과 실제 결과 비교
        System.out.println(table);  // 확인하기 위해 코드 작성함
    }

    @Test
    @DisplayName("포인트 사용 동시성 테스트")
    public void UseConcurrencyTest() {
        // when
        for (int i = 0; i < THREAD_COUNT; i++) {
            executorService.submit(() -> {
                synchronized (this) {
                    service.usePoint(table.id(), POINTS_TO_ADD); // 포인트를 사용하는 비즈니스 로직 호출
                }
                latch.countDown();
            });
        }

        try {
            boolean awaitSuccess = latch.await(10, TimeUnit.SECONDS);   // 모든 스레드가 완료될 때까지 대기
            if (!awaitSuccess) {
                throw new RuntimeException("모든 스레드가 완료되기까지 시간 초과");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("스레드 대기 중 인터럽트 발생", e);
        }

        // then
        this.table = service.getUserPoint(table.id());
        assertEquals(0, table.point());  // 기대값인 0과 실제 결과 비교
        System.out.println(table);  // 확인하기 위해 코드 작성함
    }
}

package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class IntegrationTest {

    @Autowired
    private PointService service;

    @Autowired
    private UserPointTable userPointTable;

    private UserPoint table;

    @BeforeEach
    public void setUp() {
        // 테스트를 위한 초기 데이터 설정
        this.table = userPointTable.insertOrUpdate(1, 10000);
    }

    @Test
    @DisplayName("포인트 충전 및 사용 동시성 테스트")
    public void concurrencyTest() {
        System.out.println(table);  // 확인하기 위해 코드 작성함

        // when
        CompletableFuture.allOf(
                CompletableFuture.runAsync(() -> service.usePoint(1, 1000)),
                CompletableFuture.runAsync(() -> service.chargePoint(1, 4000)),
                CompletableFuture.runAsync(() -> service.usePoint(1, 600)),
                CompletableFuture.runAsync(() -> service.chargePoint(1, 2000)),
                CompletableFuture.runAsync(() -> service.chargePoint(1, 300)),
                CompletableFuture.runAsync(() -> service.usePoint(1, 5000)),
                CompletableFuture.runAsync(() -> service.chargePoint(1, 1500)),
                CompletableFuture.runAsync(() -> service.usePoint(1, 1200)),
                CompletableFuture.runAsync(() -> service.usePoint(1, 3000)),
                CompletableFuture.runAsync(() -> service.chargePoint(1, 200))
        ).join(); // 모든 비동기 작업이 완료될 때까지 대기

        // then
        this.table = service.getUserPoint(table.id());
        System.out.println(table);  // 확인하기 위해 코드 작성함

        // 올바른 예상 포인트 계산
        assertThat(table.point()).isEqualTo(10000 - 1000 + 4000 - 600 + 2000 + 300 - 5000 + 1500 - 1200 - 3000 + 200);
        System.out.println(table);  // 확인하기 위해 코드 작성함
    }
}

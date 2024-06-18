package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PointServiceTest {

    @Mock
    private PointHistoryTable pointHistoryTable;

    @Mock
    private UserPointTable userPointTable;

    @InjectMocks
    private PointService pointService;

    @Test
    @DisplayName("포인트 조회")
    public void getUserPoint() {
        // given
        long userId = 1L;
        UserPoint expectedUserPoint = new UserPoint(userId, 1000, 0);

        when(userPointTable.selectById(userId)).thenReturn(expectedUserPoint);

        // when
        UserPoint actualUserPoint = pointService.getUserPoint(userId);

        // then
        assertEquals(expectedUserPoint, actualUserPoint);
        verify(userPointTable, times(1)).selectById(userId);
    }

    @Test
    @DisplayName("포인트 충전/이용 내역 조회")
    public void getUserPointHistory() {
        // given
        long userId = 1L;
        List<PointHistory> expectedHistory = Arrays.asList(
                new PointHistory(1, userId, 500, TransactionType.CHARGE, 0),
                new PointHistory(2, userId, 300, TransactionType.USE, 0)
        );

        when(pointHistoryTable.selectAllByUserId(userId)).thenReturn(expectedHistory);

        // when
        List<PointHistory> actualHistory = pointService.getUserPointHistory(userId);

        // then
        assertEquals(expectedHistory, actualHistory);
        verify(pointHistoryTable, times(1)).selectAllByUserId(userId);
    }

    @Test
    @DisplayName("포인트 충전")
    public void chargePoint() {
        // given
        long userId = 1L;
        long amount = 400;
        UserPoint currentUserPoint = new UserPoint(userId, 1000, 0);
        UserPoint expectedUserPoint = new UserPoint(userId, 1400, 0);

        when(userPointTable.selectById(userId)).thenReturn(currentUserPoint);
        when(userPointTable.insertOrUpdate(userId, 1400)).thenReturn(expectedUserPoint);

        // when
        UserPoint actualUserPoint = pointService.chargePoint(userId, amount);

        // then
        assertEquals(expectedUserPoint, actualUserPoint);
        verify(userPointTable, times(1)).selectById(userId);
        verify(userPointTable, times(1)).insertOrUpdate(userId, 1400);
    }

    @Test
    @DisplayName("포인트 사용")
    public void usePoint() {
        // given
        long userId = 1L;
        long amount = 400;
        UserPoint currentUserPoint = new UserPoint(userId, 1000, 0);
        UserPoint expectedUserPoint = new UserPoint(userId, 600, 0);

        when(userPointTable.selectById(userId)).thenReturn(currentUserPoint);
        when(userPointTable.insertOrUpdate(userId, 600)).thenReturn(expectedUserPoint);

        // when
        UserPoint actualUserPoint = pointService.usePoint(userId, amount);

        // then
        assertEquals(expectedUserPoint, actualUserPoint);
        verify(userPointTable, times(1)).selectById(userId);
        verify(userPointTable, times(1)).insertOrUpdate(userId, 600);
    }

    @Test
    @DisplayName("포인트 부족 시 사용 요청")
    public void usePoint_InsufficientPoints() {
        // given
        long userId = 1L;
        long amount = 1500;
        UserPoint currentUserPoint = new UserPoint(userId, 1000, 0);

        when(userPointTable.selectById(userId)).thenReturn(currentUserPoint);

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            pointService.usePoint(userId, amount);
        });

        verify(userPointTable, times(1)).selectById(userId);
        verify(userPointTable, times(0)).insertOrUpdate(anyLong(), anyLong());
    }
}

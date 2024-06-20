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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PointRepositoryTest {

    @Mock
    private UserPointTable userPointTable;

    @Mock
    private PointHistoryTable pointHistoryTable;

    @InjectMocks
    private PointRepositoryImpl pointRepository;

    /**
     * 특정 유저의 포인트 조회 테스트
     */
    @Test
    @DisplayName("포인트 조회")
    public void getUserPoint() {
        // given
        long userId = 1L;
        UserPoint expectedUserPoint = new UserPoint(userId, 100, 0);    // 예상되는 UserPoint 객체 생성

        // Mock 데이터 설정
        when(userPointTable.selectById(userId)).thenReturn(expectedUserPoint);  // Mock 객체에 대한 행동 설정: selectById 메서드 호출 시 expectedUserPoint 반환

        // when
        UserPoint actualUserPoint = pointRepository.getUserPoint(userId);   // 테스트 대상 메서드 호출

        // then
        assertEquals(expectedUserPoint, actualUserPoint);   // 예상된 사용자 포인트와 실제 반환된 사용자 포인트 비교
        verify(userPointTable, times(1)).selectById(userId);    // userPointTable 의 selectById 메서드가 1회 호출되었는지 검증
    }

    /**
     * 특정 유저의 포인트 충전/이용 내역 조회 테스트
     */
    @Test
    @DisplayName("포인트 충전/이용 내역 조회")
    public void getUserPointHistory() {
        // given
        long userId = 1L;
        List<PointHistory> expectedHistory = Arrays.asList(
                new PointHistory(1, userId, 500, TransactionType.CHARGE, 0),
                new PointHistory(2, userId, 300, TransactionType.USE, 0)
        );  // 예상되는 PointHistory 리스트 생성

        // Mock 데이터 설정
        when(pointHistoryTable.selectAllByUserId(userId)).thenReturn(expectedHistory);  // Mock 객체에 대한 행동 설정: selectAllByUserId 메서드 호출 시 expectedHistory 반환

        // when
        List<PointHistory> actualHistory = pointRepository.getUserPointHistory(userId); // 테스트 대상 메서드 호출

        // then
        assertEquals(expectedHistory, actualHistory);   // 예상된 포인트 내역 리스트와 실제 반환된 포인트 내역 리스트 비교
        verify(pointHistoryTable, times(1)).selectAllByUserId(userId);  // pointHistoryTable 의 selectAllByUserId 메서드가 1회 호출되었는지 검증
    }

    /**
     * 특정 유저의 포인트 충전/이용 후의 금액으로 UserPoint 업데이트 테스트
     */
    @Test
    @DisplayName("포인트 충전/이용 후의 금액으로 UserPoint 업데이트")
    public void updateUserPoint() {
        // given
        long userId = 1L;
        long updatedPoint = 1500;
        UserPoint expectedUserPoint = new UserPoint(userId, updatedPoint, 0);   // 예상되는 UserPoint 객체 생성

        // Mock 데이터 설정
        when(userPointTable.insertOrUpdate(userId, updatedPoint)).thenReturn(expectedUserPoint);    // Mock 객체에 대한 행동 설정: insertOrUpdate 메서드 호출 시 expectedUserPoint 반환

        // when
        UserPoint actualUserPoint = pointRepository.updateUserPoint(userId, updatedPoint);  // 테스트 대상 메서드 호출

        // then
        assertEquals(expectedUserPoint, actualUserPoint);   // 예상된 사용자 포인트와 실제 반환된 사용자 포인트 비교
        verify(userPointTable, times(1)).insertOrUpdate(userId, updatedPoint);  // userPointTable 의 insertOrUpdate 메서드가 1회 호출되었는지 검증
    }

    /**
     * 특정 유저의 포인트 변경 내역 추가 테스트
     */
    @Test
    @DisplayName("포인트 변경 내역 추가")
    public void insertUserPointHistory() {
        // given
        long userId = 1L;
        long amount = 1500;
        TransactionType type = TransactionType.CHARGE;
        long currentTimeMillis = System.currentTimeMillis();

        // when
        pointRepository.insertUserPointHistory(userId, amount, type, currentTimeMillis);    // 테스트 대상 메서드 호출

        // then
        verify(pointHistoryTable, times(1)).insert(userId, amount, type, currentTimeMillis);    // pointHistoryTable 의 insert 메서드가 1회 호출되었는지 검증
    }
}

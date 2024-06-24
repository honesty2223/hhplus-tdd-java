package io.hhplus.tdd.point;

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
    private PointRepository pointRepository;

    @InjectMocks
    private PointService pointService;

    /**
     * 특정 유저의 포인트 조회 테스트
     */
    @Test
    @DisplayName("포인트 조회")
    public void getUserPoint() {
        // given
        long userId = 1L;
        UserPoint expectedUserPoint = new UserPoint(userId, 1000, 0);   // 예상되는 UserPoint 객체 생성

        // Mock 데이터 설정
        when(pointRepository.getUserPoint(userId)).thenReturn(expectedUserPoint);   // Mock 객체에 대한 행동 설정: getUserPoint 메서드 호출 시 expectedUserPoint 반환

        // when
        UserPoint actualUserPoint = pointService.getUserPoint(userId);  // 테스트 대상 메서드 호출 및 반환값 저장

        // then
        assertEquals(expectedUserPoint, actualUserPoint);   // 예상값과 실제 반환값 비교
        verify(pointRepository, times(1)).getUserPoint(userId); // pointRepository 의 getUserPoint 메서드가 1회 호출되었는지 확인
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
        when(pointRepository.getUserPointHistory(userId)).thenReturn(expectedHistory);  // Mock 객체에 대한 행동 설정: getUserPointHistory 메서드 호출 시 expectedHistory 반환

        // when
        List<PointHistory> actualHistory = pointService.getUserPointHistory(userId);    // 테스트 대상 메서드 호출 및 반환값 저장

        // then
        assertEquals(expectedHistory, actualHistory);   // 예상값과 실제 반환값 비교
        assertEquals(expectedHistory.size(), actualHistory.size()); // 리스트의 크기 비교
        verify(pointRepository, times(1)).getUserPointHistory(userId);  // pointRepository 의 getUserPointHistory 메서드가 1회 호출되었는지 확인
    }

    /**
     * 특정 유저의 포인트 충전 테스트
     */
    @Test
    @DisplayName("포인트 충전")
    public void chargePoint() {
        // given
        long userId = 1L;
        long amount = 400;  // 충전 금액
        UserPoint currentUserPoint = new UserPoint(userId, 1000, 0);    // 현재 UserPoint 객체 생성
        UserPoint expectedUserPoint = new UserPoint(userId, 1400, 0);   // 예상되는 UserPoint 객체 생성

        // Mock 데이터 설정
        when(pointRepository.getUserPoint(userId)).thenReturn(currentUserPoint);    // Mock 객체에 대한 행동 설정: getUserPoint 메서드 호출 시 currentUserPoint 반환
        when(pointRepository.updateUserPoint(userId, 1400)).thenReturn(expectedUserPoint);  // Mock 객체에 대한 행동 설정: updateUserPoint 메서드 호출 시 expectedUserPoint 반환

        // when
        UserPoint actualUserPoint = pointService.chargePoint(userId, amount);   // 테스트 대상 메서드 호출 및 반환값 저장

        // then
        assertEquals(expectedUserPoint, actualUserPoint);   // 예상값과 실제 반환값 비교
        verify(pointRepository, times(1)).getUserPoint(userId);     // pointRepository 의 getUserPoint 메서드가 1회 호출되었는지 확인
        verify(pointRepository, times(1)).updateUserPoint(userId, 1400);    // pointRepository 의 updateUserPoint 메서드가 1회 호출되었는지 확인
        verify(pointRepository, times(1)).insertUserPointHistory(anyLong(), anyLong(), any(TransactionType.class), anyLong());  // pointRepository 의 insertUserPointHistory 메서드 호출 여부 확인
    }

    /**
     * 특정 유저의 포인트 사용 테스트
     */
    @Test
    @DisplayName("포인트 사용")
    public void usePoint() {
        // given
        long userId = 1L;
        long amount = 400;  // 사용 금액
        UserPoint currentUserPoint = new UserPoint(userId, 1000, 0);    // 현재 UserPoint 객체 생성
        UserPoint expectedUserPoint = new UserPoint(userId, 600, 0);    // 예상되는 UserPoint 객체 생성

        // Mock 데이터 설정
        when(pointRepository.getUserPoint(userId)).thenReturn(currentUserPoint);    // Mock 객체에 대한 행동 설정: getUserPoint 메서드 호출 시 currentUserPoint 반환
        when(pointRepository.updateUserPoint(userId, 600)).thenReturn(expectedUserPoint);   // Mock 객체에 대한 행동 설정: updateUserPoint 메서드 호출 시 expectedUserPoint 반환

        // when
        UserPoint actualUserPoint = pointService.usePoint(userId, amount);  // 테스트 대상 메서드 호출 및 반환값 저장

        // then
        assertEquals(expectedUserPoint, actualUserPoint);   // 예상값과 실제 반환값 비교
        verify(pointRepository, times(1)).getUserPoint(userId);     // pointRepository 의 getUserPoint 메서드가 1회 호출되었는지 확인
        verify(pointRepository, times(1)).updateUserPoint(userId, 600);     // pointRepository 의 updateUserPoint 메서드가 1회 호출되었는지 확인
        verify(pointRepository, times(1)).insertUserPointHistory(anyLong(), anyLong(), any(TransactionType.class), anyLong());  // pointRepository 의 insertUserPointHistory 메서드 호출 여부 확인
    }

    /**
     * 포인트 부족 시 사용 요청 예외 처리 테스트
     */
    @Test
    @DisplayName("포인트 부족 시 사용 요청")
    public void usePoint_InsufficientPoints() {
        // given
        long userId = 1L;
        long amount = 1500;
        UserPoint currentUserPoint = new UserPoint(userId, 1000, 0);    // 현재 UserPoint 객체 생성

        // Mock 데이터 설정
        when(pointRepository.getUserPoint(userId)).thenReturn(currentUserPoint);    // Mock 객체에 대한 행동 설정: getUserPoint 메서드 호출 시 currentUserPoint 반환

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            pointService.usePoint(userId, amount);  // 특정 예외가 발생해야 하는 테스트 대상 메서드 호출
        });

        verify(pointRepository, times(1)).getUserPoint(userId);     // pointRepository 의 getUserPoint 메서드가 1회 호출되었는지 확인
        verify(pointRepository, times(0)).updateUserPoint(anyLong(), anyLong());    // updateUserPoint 메서드 호출이 없어야 함
        verify(pointRepository, times(0)).insertUserPointHistory(anyLong(), anyLong(), any(TransactionType.class), anyLong());  // insertUserPointHistory 메서드 호출이 없어야 함
    }
}

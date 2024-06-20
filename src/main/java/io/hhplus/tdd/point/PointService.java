package io.hhplus.tdd.point;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PointService {

    @Autowired
    private PointRepository pointRepository;

    /**
     * 특정 유저의 포인트 조회
     */
    public UserPoint getUserPoint(long id) {
        return pointRepository.getUserPoint(id);
    }

    /**
     * 특정 유저의 포인트 충전/이용 내역 조회
     */
    public List<PointHistory> getUserPointHistory(long id) {
        return pointRepository.getUserPointHistory(id);
    }

    /**
     * 특정 유저의 포인트 충전
     */
    public UserPoint chargePoint(long id, long amount) {
        UserPoint user = pointRepository.getUserPoint(id);
        long originalPoint = user.point();
        long updatedPoint = originalPoint + amount;

        UserPoint updatedUser = pointRepository.updateUserPoint(id, updatedPoint);

        // updateUserPoint 가 성공했을 때만 insertUserPointHistory 호출
        if (updatedUser != null) {
            pointRepository.insertUserPointHistory(id, amount, TransactionType.CHARGE, System.currentTimeMillis());
        }

        return updatedUser;
    }

    /**
     * 특정 유저의 포인트 사용
     */
    public UserPoint usePoint(long id, long amount) {
        UserPoint user = pointRepository.getUserPoint(id);
        long originalPoint = user.point();

        if (originalPoint < amount) {
            throw new IllegalArgumentException("포인트가 부족합니다. 현재 포인트: " + originalPoint);
        }

        long updatedPoint = originalPoint - amount;

        UserPoint updatedUser = pointRepository.updateUserPoint(id, updatedPoint);

        // updateUserPoint 가 성공했을 때만 insertUserPointHistory 호출
        if (updatedUser != null) {
            pointRepository.insertUserPointHistory(id, amount, TransactionType.USE, System.currentTimeMillis());
        }

        return updatedUser;
    }
}

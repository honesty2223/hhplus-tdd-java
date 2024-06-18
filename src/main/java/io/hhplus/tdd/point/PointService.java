package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class PointService {

    @Autowired
    private PointHistoryTable pointHistoryTable;

    @Autowired
    private UserPointTable userPointTable;

    /**
     * TODO - 특정 유저의 포인트를 조회하는 기능을 작성해주세요.
     */
    public UserPoint getUserPoint(long id) {
        return userPointTable.selectById(id);
    }

    /**
     * TODO - 특정 유저의 포인트 충전/이용 내역을 조회하는 기능을 작성해주세요.
     */
    public List<PointHistory> getUserPointHistory(long id) {
        return pointHistoryTable.selectAllByUserId(id);
    }

    /**
     * TODO - 특정 유저의 포인트를 충전하는 기능을 작성해주세요.
     */
    public UserPoint chargePoint(long id, long amount) {
        UserPoint user = userPointTable.selectById(id);
        long originalPoint = user.point();
        long updatedPoint = originalPoint + amount;
        return userPointTable.insertOrUpdate(id, updatedPoint);
    }

    /**
     * TODO - 특정 유저의 포인트를 사용하는 기능을 작성해주세요.
     */
    public UserPoint usePoint(long id, long amount) {
        UserPoint user = userPointTable.selectById(id);
        long originalPoint = user.point();
        if(originalPoint < amount) {
            throw new IllegalArgumentException("Insufficient points");
        }
        long updatedPoint = originalPoint - amount;
        return userPointTable.insertOrUpdate(id, updatedPoint);
    }
}

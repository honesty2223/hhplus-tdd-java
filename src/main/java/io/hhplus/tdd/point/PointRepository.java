package io.hhplus.tdd.point;

import java.util.List;

public interface PointRepository {
    UserPoint getUserPoint(long id);
    List<PointHistory> getUserPointHistory(long id);
    UserPoint updateUserPoint(long id, long updatedPoint);
    void insertUserPointHistory(long userId, long amount, TransactionType type, long updateMillis);
}

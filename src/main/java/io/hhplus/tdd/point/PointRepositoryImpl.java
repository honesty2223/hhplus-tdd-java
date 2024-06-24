package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PointRepositoryImpl implements PointRepository {

    @Autowired
    private PointHistoryTable pointHistoryTable;

    @Autowired
    private UserPointTable userPointTable;

    @Override
    public UserPoint getUserPoint(long id) {
        return userPointTable.selectById(id);
    }

    @Override
    public List<PointHistory> getUserPointHistory(long id) {
        return pointHistoryTable.selectAllByUserId(id);
    }

    @Override
    public UserPoint updateUserPoint(long id, long updatedPoint) {
        return userPointTable.insertOrUpdate(id, updatedPoint);
    }

    @Override
    public void insertUserPointHistory(long userId, long amount, TransactionType type, long updateMillis) {
        pointHistoryTable.insert(userId, amount, type, updateMillis);
    }
}

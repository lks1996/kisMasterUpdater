package com.Repository;

import com.Entity.OverseasStockInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OverseasStockInfoRepository extends JpaRepository<OverseasStockInfo, Long> {

    // 특정 거래소 ID에 해당하는 모든 데이터를 삭제.
    @Modifying // SELECT 외의 쿼리(INSERT, UPDATE, DELETE) 실행 시 필요
    @Query("DELETE FROM OverseasStockInfo o WHERE o.exchangeId = :exchangeId")
    void deleteByExchangeId(String exchangeId);
}

package com.Repository;

import com.Entity.OverseasStockInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OverseasStockInfoRepository extends JpaRepository<OverseasStockInfo, Long> {

    // 특정 거래소 ID에 해당하는 모든 데이터를 삭제.
    @Modifying // SELECT 외의 쿼리(INSERT, UPDATE, DELETE) 실행 시 필요
    @Query("DELETE FROM OverseasStockInfo o WHERE o.exchangeId = :exchangeId")
    void deleteByExchangeId(String exchangeId);

    /**
     * 주어진 심볼(티커) 목록에 해당하는 모든 OverseasStockInfo 엔티티 조회.
     * @param symbols 조회할 심볼 리스트
     * @return 조회된 OverseasStockInfo 엔티티 리스트
     */
    List<OverseasStockInfo> findBySymbolIn(List<String> symbols);
}

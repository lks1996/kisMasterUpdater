package com.Repository;

import com.Entity.OverseasStockInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OverseasStockInfoRepository extends JpaRepository<OverseasStockInfo, Long> {

    // 특정 거래소 ID에 해당하는 모든 데이터를 삭제.
    void deleteByExchangeId(String exchangeId);
}

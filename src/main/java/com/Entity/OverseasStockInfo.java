package com.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "OVERSEAS_STOCK_MASTER")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class OverseasStockInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // DB 기본 키

    @Column(length = 10)
    private String nationalCode;        // 국가 코드
    @Column(length = 10)
    private String exchangeId;          // 거래소 ID (ex: NAS, NYS) - 중요!
    @Column(length = 20)
    private String exchangeCode;        // 거래소 코드 (숫자 등)
    @Column(length = 50)
    private String exchangeName;        // 거래소 명칭
    @Column(length = 30, unique = true, nullable = false) // Symbol을 고유 식별자로 사용 가능
    private String symbol;              // 종목 심볼 (티커) - 중요!
    @Column(length = 30)
    private String realtimeSymbol;      // 실시간 심볼
    @Column(length = 200)
    private String koreanName;          // 한글 종목명
    @Column(length = 200)
    private String englishName;         // 영문 종목명
    @Column(length = 5)
    private String securityType;        // 증권 종류 (1:Index, 2:Stock, 3:ETF, 4:Warrant)
    @Column(length = 10)
    private String currency;            // 통화
    @Column(length = 5)
    private String floatPosition;       // 소수점 위치
    @Column(length = 5)
    private String dataType;            // 데이터 타입
    @Column(length = 20)
    private String basePrice;           // 기준가
    @Column(length = 10)
    private String bidOrderSize;        // 매수 주문 단위
    @Column(length = 10)
    private String askOrderSize;        // 매도 주문 단위
    @Column(length = 10)
    private String marketStartTime;     // 장 시작 시간 (HHMM)
    @Column(length = 10)
    private String marketEndTime;       // 장 종료 시간 (HHMM)
    @Column(length = 5)
    private String isDr;                // DR 여부 (Y/N)
    @Column(length = 10)
    private String drCountryCode;       // DR 국가 코드
    @Column(length = 20)
    private String industryCode;        // 업종 분류 코드
    @Column(length = 5)
    private String hasIndexConstituents;// 지수 구성 종목 존재 여부 (0/1)
    @Column(length = 10)
    private String tickSizeType;        // Tick Size Type
    @Column(length = 10)
    private String categoryCode;        // 구분 코드 (001:ETF, 002:ETN, ...)
    @Column(length = 20)
    private String tickSizeTypeDetail;  // Tick Size Type 상세
}

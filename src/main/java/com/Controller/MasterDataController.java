package com.Controller;

import com.Common.ApiResponse;
import com.Dto.StockInfoDto;
import com.Service.MasterQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class MasterDataController {
    private final MasterQueryService queryService;

    public MasterDataController(MasterQueryService queryService) {
        this.queryService = queryService;
    }

    /**
     * 심볼 목록을 받아 각 심볼의 마스터 정보를 반환하는 API 엔드포인트.
     * ex. /api/master/stock-info?symbols=AAPL,NVDA,MSFT
     * @param symbols 쉼표(,)로 구분된 심볼 문자열 리스트
     * @return ApiResponse<List<StockMasterDto>>
     */
    @GetMapping("/api/master/stock-info") // 엔드포인트 이름 수정
    public ResponseEntity<ApiResponse<List<StockInfoDto>>> getStockInfo(@RequestParam List<String> symbols) {
        log.info("Request received for stock info with symbols: {}", symbols);
        try {
            List<StockInfoDto> result = queryService.findStockMasterInfoBySymbols(symbols);
            log.info("Returning {} stock info entries.", result.size());
            return new ResponseEntity<>(ApiResponse.success(result), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error fetching stock info for symbols: {}", symbols, e);
            return new ResponseEntity<>(ApiResponse.error("Failed to fetch stock info: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

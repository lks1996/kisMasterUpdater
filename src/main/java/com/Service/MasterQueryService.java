package com.Service;

import com.Dto.StockInfoDto;
import com.Repository.OverseasStockInfoRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MasterQueryService {

    private final OverseasStockInfoRepository repository;

    public MasterQueryService(OverseasStockInfoRepository repository) {
        this.repository = repository;
    }

    /**
     * 주어진 심볼(티커) 목록에 해당하는 마스터 정보를 조회하여 반환합니다.
     * @param symbols 조회할 심볼 리스트
     * @return StockMasterDto 리스트
     */
    public List<StockInfoDto> findStockMasterInfoBySymbols(List<String> symbols) {
        if (symbols == null || symbols.isEmpty()) {
            return Collections.emptyList(); // 빈 리스트 반환
        }
        // DB에서 symbol 목록으로 조회
        return repository.findBySymbolIn(symbols)
                .stream()
                // 결과를 StockMasterDto로 변환
                .map(info -> new StockInfoDto(
                        info.getSymbol(),
                        info.getExchangeId(),
                        info.getRealtimeSymbol(),
                        info.getKoreanName(),
                        info.getEnglishName(),
                        info.getCurrency(),
                        info.getSecurityType()))
                .collect(Collectors.toList());
    }
}

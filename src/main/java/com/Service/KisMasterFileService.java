package com.Service;

import com.Entity.OverseasStockInfo;
import com.Repository.OverseasStockInfoRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@Slf4j
public class KisMasterFileService {
    private static final String KIS_MASTER_FILE_URL_FORMAT = "https://new.real.download.dws.co.kr/common/master/%smst.cod.zip";
    private static final int EXPECTED_COLUMNS = 24; // 원본 파일의 컬럼 개수.

    @Value("${kis.masterfile.base-path}")
    private String basePathString;
    private Path basePath;

    private final OverseasStockInfoRepository repository;

    public KisMasterFileService(OverseasStockInfoRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    private void init() {
        this.basePath = Paths.get(basePathString);
        try {
            // 서비스 시작 시 다운로드 폴더 생성.
            Files.createDirectories(basePath);
        } catch (IOException e) {
            log.error("Failed to create base directory for master files: {}", basePathString, e);
            throw new RuntimeException("Failed to initialize KisMasterFileService", e);
        }
    }

    /**
     * 모든 해외 거래소의 마스터 파일을 다운로드 후 DB에 업데이트.
     */
    @Transactional
    public void processAllMasterFiles() {
        // 순서대로 나스닥, 뉴욕, 아멕스, 상해A, 상해B(지수), 심천A, 심천B(지수), 도쿄, 홍콩, 하노이, 호치민
        List<String> exchangeIds = Arrays.asList("nas", "nys", "ams", "shs", "shi", "szs", "szi", "tse", "hks", "hnx", "hsx");
        log.warn("Starting processing all overseas master files for exchanges: {}", exchangeIds);

        for (String id : exchangeIds) {
            try {
                processSingleMasterFile(id);
            } catch (Exception e) {
                // 특정 파일 처리 실패 시 로깅하고 다음 파일로 진행.
                log.error("Failed to process master file for exchange id: {}", id, e);
            }
        }
        log.info("Finished processing all overseas master files.");
    }

    /**
     * 특정 거래소의 마스터 파일을 다운로드하고 DB에 업데이트.
     * @param exchangeId 거래소 코드
     */
    @Transactional
    public void processSingleMasterFile(String exchangeId) throws IOException {
        log.info("Processing master file for exchange: {}", exchangeId);

        // 1. 파일 다운로드 및 압축 해제.
        Path codFilePath = downloadAndUnzipMasterFile(exchangeId);

        // 2. 파일 파싱.
        List<OverseasStockInfo> stockInfos = parseMasterFile(codFilePath, exchangeId);

        // 3. DB 업데이트. (기존 데이터 삭제 후 새로 삽입)
        if (!stockInfos.isEmpty()) {
            log.warn("Deleting existing data for exchange: {}", exchangeId);
            repository.deleteByExchangeId(exchangeId.toUpperCase()); // 거래소 ID 기준으로 삭제 (대문자 통일)
            repository.saveAll(stockInfos);
            log.warn("Successfully updated {} for exchange id : {}", stockInfos.size(), exchangeId);
        } else {
            log.warn("No stock info found after parsing for exchange: {}", exchangeId);
        }

        // 처리 완료 후 다운로드한 파일 삭제.
         Files.deleteIfExists(codFilePath.getParent().resolve(exchangeId + "mst.cod.zip"));
         Files.deleteIfExists(codFilePath);
    }

    /**
     * 마스터 ZIP 파일을 다운로드 후 압축 해제.
     * @param exchangeCode 거래소 코드
     * @return 압축 해제된 .cod 파일의 경로
     */
    private Path downloadAndUnzipMasterFile(String exchangeCode) throws IOException {
        String downloadUrl = String.format(KIS_MASTER_FILE_URL_FORMAT, exchangeCode);
        Path zipFilePath = basePath.resolve(exchangeCode + "mst.cod.zip");
        Path codFilePath = basePath.resolve(exchangeCode + "mst.cod"); // 압축 해제될 파일 경로

        log.info("Downloading from: {}", downloadUrl);
        // URL에서 InputStream을 열고 파일로 복사.
        try (InputStream in = new URL(downloadUrl).openStream()) {
            Files.copy(in, zipFilePath, StandardCopyOption.REPLACE_EXISTING);
        }
        log.info("Downloaded to: {}", zipFilePath);

        log.info("Unzipping: {}", zipFilePath);
        // ZipInputStream을 사용하여 압축 해제
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipFilePath))) {
            ZipEntry zipEntry = zis.getNextEntry();
            if (zipEntry != null && !zipEntry.isDirectory() && zipEntry.getName().toLowerCase().endsWith(".cod")) {
                Files.copy(zis, codFilePath, StandardCopyOption.REPLACE_EXISTING);
                log.info("Unzipped to: {}", codFilePath);
                zis.closeEntry();
            } else {
                throw new IOException("Could not find .cod file in the zip archive for " + exchangeCode);
            }
        }
        return codFilePath;
    }

    /**
     * .cod 파일을 파싱하여 OverseasStockInfo 리스트로 변환.
     * @param codFilePath 파싱할 .cod 파일 경로
     * @param exchangeId 이 데이터의 거래소 ID
     * @return 파싱된 OverseasStockInfo 리스트
     */
    private List<OverseasStockInfo> parseMasterFile(Path codFilePath, String exchangeId) throws IOException {
        List<OverseasStockInfo> stockInfos = new ArrayList<>();
        // CP949 인코딩 사용.
        try (BufferedReader reader = Files.newBufferedReader(codFilePath, Charset.forName("CP949"))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (lineNumber == 1) continue; // 첫 줄은 헤더로 가정. 건너뛰거나, 컬럼 수가 다르면 무시.

                String[] columns = line.split("\t", -1); // 탭으로 분리, 빈 문자열 유지.

                if (columns.length < EXPECTED_COLUMNS) {
                    log.warn("Skipping line {} in {}: Expected {} columns, but found {}. Line: {}",
                            lineNumber, codFilePath.getFileName(), EXPECTED_COLUMNS, columns.length, line);
                    continue; // 컬럼 수가 부족하면 건너뜀.
                }

                try {
                    OverseasStockInfo info = new OverseasStockInfo();
                    info.setNationalCode(columns[0].trim());
                    info.setExchangeId(exchangeId.toUpperCase()); // URL 파라미터로 받은 exchangeId 사용 (대문자 통일)
                    info.setExchangeCode(columns[2].trim());
                    info.setExchangeName(columns[3].trim());
                    info.setSymbol(columns[4].trim());           // 티커
                    info.setRealtimeSymbol(columns[5].trim());
                    info.setKoreanName(columns[6].trim());
                    info.setEnglishName(columns[7].trim());
                    info.setSecurityType(columns[8].trim());
                    info.setCurrency(columns[9].trim());
                    info.setFloatPosition(columns[10].trim());
                    info.setDataType(columns[11].trim());
                    info.setBasePrice(columns[12].trim());
                    info.setBidOrderSize(columns[13].trim());
                    info.setAskOrderSize(columns[14].trim());
                    info.setMarketStartTime(columns[15].trim());
                    info.setMarketEndTime(columns[16].trim());
                    info.setIsDr(columns[17].trim());
                    info.setDrCountryCode(columns[18].trim());
                    info.setIndustryCode(columns[19].trim());
                    info.setHasIndexConstituents(columns[20].trim());
                    info.setTickSizeType(columns[21].trim());
                    info.setCategoryCode(columns[22].trim());
                    info.setTickSizeTypeDetail(columns[23].trim());

                    if (info.getSymbol() != null && !info.getSymbol().isEmpty()) { // Symbol이 비어있지 않으면 추가.
                        stockInfos.add(info);
                    } else {
                        log.warn("Skipping line {} due to empty symbol.", lineNumber);
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    log.warn("Error parsing line {}: {}. Exception: {}", lineNumber, line, e.getMessage());
                }
            }
        }
        return stockInfos;
    }
}

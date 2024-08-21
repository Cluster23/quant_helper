package Project.quantHelper.service;

import Project.quantHelper.domain.Stock;
import Project.quantHelper.dto.StockDTO;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Service
@Transactional
@Slf4j
public class DartService {

    private final StockService stockService;
    private final WebClient webClient;

    private final String baseUrl;

    private final String apiKey;

    @Autowired
    public DartService(StockService stockService, WebClient webClient, @Value("${spring.dart-api.endpoint-url}") final String baseUrl, @Value("${spring.dart-api.api-key}") final String apiKey){
        this.stockService = stockService;
        this.webClient = webClient;
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }

    /**
     * 재무제표를 Dart open api로 불러오는 메소드
     * @param corpName, year, quarter
     * @return String
     */
    public Mono<String> getFinancialStatementFromDart(StockDTO stockDTO, int year, int quarter) {
        String[] reprtCode = {"11013", "11012", "11014", "11011"};

        String fullUrl = baseUrl + "/api/fnlttSinglAcnt.json" +
                "?crtfc_key=" + apiKey +
                "&corp_code=" + stockDTO.getCorpCode() +
                "&bsns_year=" + year +
                "&reprt_code=" + reprtCode[quarter - 1];

        return webClient.get()
                .uri(fullUrl)
                .retrieve()
                .bodyToMono(String.class);
    }

    /**
     * 발행된 주식의 총 수를 Dart open api로 불러오는 메소드
     * @param corpName, year, quarter
     * @return String
     */
    public Mono<String> getStockAmountFromDart(StockDTO stockDTO, int year, int quarter) {
        String[] reprtCode = {"11013", "11012", "11014", "11011"};

        String fullUrl = baseUrl + "/api/stockTotqySttus.json" +
                "?crtfc_key=" + apiKey +
                "&corp_code=" + stockDTO.getCorpCode() +
                "&bsns_year=" + year +
                "&reprt_code=" + reprtCode[quarter - 1];

        return webClient.get()
                .uri(fullUrl)
                .retrieve()
                .bodyToMono(String.class);
    }

    /**
     * 주당 배당금과 주당순이익(EPS)를 Dart open api로 불러오는 메소드
     * @param corpName, year, quarter
     * @return String
     */
    public Mono<String> getAllocationAndEpsFromDart(StockDTO stockDTO, int year, int quarter) {
        String[] reprtCode = {"11013", "11012", "11014", "11011"};

        String fullUrl = baseUrl + "/api/alotMatter.json" +
                "?crtfc_key=" + apiKey +
                "&corp_code=" + stockDTO.getCorpCode() +
                "&bsns_year=" + year +
                "&reprt_code=" + reprtCode[quarter - 1];

        return webClient.get()
                .uri(fullUrl)
                .retrieve()
                .bodyToMono(String.class);
    }
}
package Project.quantHelper.service;


import Project.quantHelper.util.KisAccessToken;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
@Slf4j
public class KisService {

    private final WebClient webClient;
    private final String baseUrl;
    private final String appKey;
    private final String appSecretKey;

    private String accessToken;

    @Autowired
    public KisService(WebClient webClient,
                      @Value("${spring.kis-api.endpoint-url}") String baseUrl,
                      @Value("${spring.kis-api.app-key}")String appKey,
                      @Value("${spring.kis-api.app-secret-key}") String appSecretKey,
                      KisAccessToken kisAccessToken) {
        this.webClient = webClient;
        this.baseUrl = baseUrl;
        this.appKey = appKey;
        this.appSecretKey = appSecretKey;
        accessToken = kisAccessToken.getAccessToken();
    }

    /**
     * KIS API를 사용하여 주식 이름 가져오기
     * @return Mono</String> Stock 이름 및 잡다한 정보가 들어있는 JSON
     */
    public Mono<String> getStockNameByCode(String code){
        String fullUrl = baseUrl + "/uapi/domestic-stock/v1/quotations/search-stock-info" + "?PDNO=" + code + "&PRDT_TYPE_CD=300";
        return webClient.get()
                .uri(fullUrl)
                .header("content-type","application/json")
                .header("authorization","Bearer " + accessToken)
                .header("appkey",appKey)
                .header("appsecret",appSecretKey)
                .header("tr_id","CTPF1604R")
                .header("custtype","P")
                .retrieve()
                .bodyToMono(String.class);
    }

    /**
     * KIS API를 사용하여 주식 정보 가져오기
     * @return Mono</String> Stock 정보들이 들어있는 JSON
     */
    public Mono<String> getStockInfoByCode(String code){
        code = code.substring(Math.max(code.length() - 6, 0));
        log.info(code);
        String fullUrl = baseUrl + "/uapi/domestic-stock/v1/quotations/inquire-price" + "?fid_cond_mrkt_div_code=J&fid_input_iscd=" + code;
        return webClient.get()
                .uri(fullUrl)
                .header("content-type","application/json")
                .header("authorization","Bearer " + accessToken)
                .header("appkey",appKey)
                .header("appsecret",appSecretKey)
                .header("tr_id","FHKST01010100")
                .retrieve()
                .bodyToMono(String.class);
    }

    /**
     * KIS API를 사용하여 주식 가격 가져오기
     * @return Mono</String> Stock 가격(시가, 종가, 최고가 등)이 들어있는 JSON
     */
    public Mono<String> getStockPriceByCodeAndDate(String code, String startDate, String endDate){
        code = code.substring(Math.max(code.length() - 6, 0));
        String fullUrl = baseUrl + "/uapi/domestic-stock/v1/quotations/inquire-daily-itemchartprice" +
                "?FID_COND_MRKT_DIV_CODE=J" +
                "&FID_INPUT_ISCD=" + code +
                "&FID_INPUT_DATE_1=" + startDate +
                "&FID_INPUT_DATE_2=" + endDate +
                "&FID_PERIOD_DIV_CODE=D" +
                "&fid_org_adj_prc=0";
        return webClient.get()
                .uri(fullUrl)
                .header("content-type","application/json")
                .header("authorization","Bearer " + accessToken)
                .header("appkey",appKey)
                .header("appsecret",appSecretKey)
                .header("tr_id","FHKST03010100")
                .retrieve()
                .bodyToMono(String.class);
    }
}

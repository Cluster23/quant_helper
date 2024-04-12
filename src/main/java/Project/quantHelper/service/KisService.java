package Project.quantHelper.service;


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
    public KisService(WebClient webClient, @Value("${spring.kis-api.endpoint-url}") String baseUrl, @Value("${spring.kis-api.app-key}")String appKey, @Value("${spring.kis-api.app-secret-key}") String appSecretKey) {
        this.webClient = webClient;
        this.baseUrl = baseUrl;
        this.appKey = appKey;
        this.appSecretKey = appSecretKey;
    }

    /**
     * 한국 투자 증권 API에 접근하기 위한 Access Token를 반환하는 메소드
     * @return String AccessToken
     */
    public String getAccessTokenFromKis() throws JsonProcessingException {

        Map<String, String> bodyMap = new HashMap<>();
        bodyMap.put("grant_type", "client_credentials");
        bodyMap.put("appkey", appKey);
        bodyMap.put("appsecret", appSecretKey);

        String fullUrl = baseUrl + "/oauth2/tokenP"; // 호스트와 경로를 조합
        Mono<String> monoAccess = webClient.post()
                    .uri(fullUrl) // 전체 URL을 명시적으로 지정
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(bodyMap))
                    .retrieve()
                    .bodyToMono(String.class);

        String accessTokenJson = monoAccess.block();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String,String> AccessTokenMap = objectMapper.readValue(accessTokenJson, Map.class);
        accessToken = AccessTokenMap.get("access_token");
        log.info(accessToken);
        return accessToken;
    }

    /**
     * KIS API를 사용하여 주식 정보 가져오기
     * @return Mono</String> Stock 정보들이 들어있는 JSON
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
}

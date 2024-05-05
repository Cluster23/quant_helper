package Project.quantHelper.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;


@Component
@Slf4j
@Getter
public class KisAccessToken {
    private String accessToken;
    private final WebClient webClient;
    private final String baseUrl;
    private final String appKey;
    private final String appSecretKey;

    /**
     * 한국 투자 증권 API에 접근하기 위한 Access Token를 받아와서 빈 내부에 저장
     */
    public KisAccessToken(WebClient webClient,
                          @Value("${spring.kis-api.endpoint-url}") String baseUrl,
                          @Value("${spring.kis-api.app-key}")String appKey,
                          @Value("${spring.kis-api.app-secret-key}") String appSecretKey) throws JsonProcessingException {
        this.webClient = webClient;
        this.baseUrl = baseUrl;
        this.appKey = appKey;
        this.appSecretKey = appSecretKey;
        refreshAccessToken();  // 초기 토큰 로드
    }

    // 23시간마다 자동 갱신
    @Scheduled(fixedRate = 82800000, initialDelay = 82800000)
    public void refreshAccessToken() throws JsonProcessingException {
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
        Map<String, String> AccessTokenMap = objectMapper.readValue(accessTokenJson, Map.class);
        accessToken = AccessTokenMap.get("access_token");
        log.info(accessToken);
    }
}

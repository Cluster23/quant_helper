package Project.quantHelper.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserter;
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

    @Autowired
    public KisService(WebClient webClient, @Value("${spring.kis-api.endpoint-url}") String baseUrl, @Value("${spring.kis-api.app-key}")String appKey, @Value("${spring.kis-api.app-secret-key}") String appSecretKey) {
        this.webClient = webClient;
        this.baseUrl = baseUrl;
        this.appKey = appKey;
        this.appSecretKey = appSecretKey;
    }


    public Mono<String> getApprovalKeyFromKis() {

        Map<String, String> bodyMap = new HashMap<>();
        bodyMap.put("grant_type", "client_credentials");
        bodyMap.put("appkey", appKey);
        bodyMap.put("secretkey", appSecretKey);

        String fullUrl = baseUrl + "/oauth2/Approval"; // 호스트와 경로를 조합
        return webClient.post()
                .uri(fullUrl) // 전체 URL을 명시적으로 지정
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(bodyMap))
                .retrieve()
                .bodyToMono(String.class);
    }

}

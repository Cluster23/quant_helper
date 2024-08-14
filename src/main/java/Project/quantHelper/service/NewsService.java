package Project.quantHelper.service;

import Project.quantHelper.util.KisAccessToken;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Transactional
@Slf4j
public class NewsService {

    private final WebClient webClient;
    private final String baseUrl;
    private final String clientId;
    private final String clientSecret;

    @Autowired
    public NewsService(WebClient webClient,
                      @Value("${spring.naver-api.endpoint-url}") String baseUrl,
                      @Value("${spring.naver-api.client-id}")String clientId,
                      @Value("${spring.naver-api.client-secret}") String clientSecret) {
        this.webClient = webClient;
        this.baseUrl = baseUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public String getNews(String query){
        String fullUrl = baseUrl + "?query=" + query + "&sort=date";

        return webClient.get()
                .uri(fullUrl)
                .header("content-type", "application/json")
                .header("X-Naver-Client-Id", clientId)
                .header("X-Naver-Client-Secret", clientSecret)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

}

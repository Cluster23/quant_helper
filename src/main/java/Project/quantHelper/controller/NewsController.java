package Project.quantHelper.controller;

import Project.quantHelper.dto.StockDTO;
import Project.quantHelper.dto.request.GetStockRequest;
import Project.quantHelper.dto.response.ErrorResponse;
import Project.quantHelper.dto.response.SuccessResponse;
import Project.quantHelper.service.NewsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/news")
@RequiredArgsConstructor
@Slf4j
public class NewsController {

    @Autowired
    private final NewsService newsService;

    @GetMapping("/")
    @Operation(
            summary = "get news by specific query",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful operation",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SuccessResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Bad credentials",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    public ResponseEntity<?> news(@RequestParam(name = "query") String query) throws JsonProcessingException {
        log.info("news controller executed");

        String news = newsService.getNews(query);

        JsonElement jsonElement = JsonParser.parseString(news);
        JsonArray items = jsonElement.getAsJsonObject().get("items").getAsJsonArray();
        JsonArray newsJson = new JsonArray();

        for (JsonElement element : items) {
            JsonObject temp = new JsonObject();
            temp.add("title", element.getAsJsonObject().get("title"));
            temp.add("link", element.getAsJsonObject().get("link"));
            newsJson.add(temp);
        }

        return ResponseEntity.ok().body(newsJson.toString());
    }
}

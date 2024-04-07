package Project.quantHelper.service;

import Project.quantHelper.domain.Stock;
import Project.quantHelper.dto.StockDTO;
import Project.quantHelper.repository.StockRepository;
import jakarta.transaction.Transactional;
import org.aspectj.lang.annotation.After;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StockServiceTest {

    @Autowired
    StockRepository stockRepository;

    @Autowired
    StockService stockService;

    @AfterEach
    void clear() {
        stockRepository.deleteAll();
    }

    @Test
    void saveStockTest() {
        // given
        StockDTO stockDTO = StockDTO.builder()
                .stockId(1L)
                .stockName("samsung")
                .price(1000L)
                .theme("Test Theme")
                .stockPriceIndex("KOSPI")
                .status("Active")
                .build();

        // when
        Long savedStockId = stockService.save(stockDTO);

        // then
        Assertions.assertThat(stockRepository.existsById(1L)).isTrue();
    }

    @Test
    void findByStockNameTest() {
        //given
        StockDTO stockDTO = StockDTO.builder()
                .stockId(1L)
                .stockName("samsung")
                .price(1000L)
                .theme("Test Theme")
                .stockPriceIndex("KOSPI")
                .status("Active")
                .build();
        stockService.save(stockDTO);

        //when
        StockDTO foundStockDTO = stockService.findByStockName("samsung");

        //then
        Assertions.assertThat(foundStockDTO.getTheme()).isEqualTo("Test Theme");
    }
}
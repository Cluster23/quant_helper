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

    @Test
    void saveStockTest() {
        // given
        StockDTO stockDTO = StockDTO.builder()
                .stockCode("A00")
                .stockName("samsung")
                .price(1000L)
                .theme("Test Theme")
                .stockPriceIndex("KOSPI")
                .status("Active")
                .build();

        // when
        String savedStockCode = stockService.save(stockDTO);

        // then
        Assertions.assertThat(stockRepository.existsById(1L)).isTrue();
    }

    @Test
    void findByStockNameTest() {
        //given

        //when
        StockDTO foundStockDTO = stockService.getStockDTOByStockName("삼성전자");

        //then
        Assertions.assertThat(foundStockDTO.getStockName()).isEqualTo("삼성전자");
    }
}
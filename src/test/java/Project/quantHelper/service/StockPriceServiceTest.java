package Project.quantHelper.service;

import Project.quantHelper.dto.StockPriceDTO;
import Project.quantHelper.repository.StockPriceRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StockPriceServiceTest {

    @Autowired
    StockPriceRepository stockPriceRepository;

    @Autowired
    StockPriceService stockPriceService;

    @AfterEach
    public void clear(){
        stockPriceRepository.deleteAll();
    }

    @Test
    public void saveStockPriceTest(){
        StockPriceDTO stockPriceDTO = StockPriceDTO.builder()
                .stockId(1L)
                .date(LocalDate.now())
                .maxPriceDay(160000L)
                .minPriceDay(150000L)
                .openPrice(150000L)
                .closePrice(160000L)
                .tradingVolume(2753215L)
                .build();
        Long savedStockPriceId = stockPriceService.saveStockPrice(stockPriceDTO);
        Assertions.assertThat(stockPriceRepository.findById(1L).get().getMaxPriceDay()).isEqualTo(160000L);
    }
}
package Project.quantHelper.service;

import Project.quantHelper.dto.FinancialStatementDTO;
import Project.quantHelper.dto.StockDTO;
import Project.quantHelper.repository.FinancialStatementRepository;
import Project.quantHelper.repository.StockRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Year;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FinancialStatementServiceTest {

    @Autowired
    FinancialStatementRepository financialStatementRepository;

    @Autowired
    FinancialStatementService financialStatementService;

    @Autowired
    StockRepository stockRepository;

    @Autowired
    StockService stockService;

    @AfterEach
    void clear(){
        financialStatementRepository.deleteAll();
        stockRepository.deleteAll();
    }

    @Test
    void save() {
        StockDTO stockDTO = StockDTO.builder()
                .stockCode("A00")
                .stockName("samsung")
                .price(1000L)
                .theme("Test Theme")
                .stockPriceIndex("KOSPI")
                .status("Active")
                .build();
        stockService.save(stockDTO);

        FinancialStatementDTO FSDTO = FinancialStatementDTO.builder()
                .stockId(1L)
                .year(2023)
                .content("hello")
                .quarter(2)
                .build();
        Long savedId = financialStatementService.save(FSDTO);

        assertThat(savedId).isEqualTo(1L);
    }

    @Test
    void find3yearsFinancialStatementByStockName() {
        // given
        StockDTO stockDTO = StockDTO.builder()
                .stockCode("A00")
                .stockName("samsung")
                .price(1000L)
                .theme("Test Theme")
                .stockPriceIndex("KOSPI")
                .status("Active")
                .build();
        stockService.save(stockDTO);

        FinancialStatementDTO FSDTO = FinancialStatementDTO.builder()
                .stockId(1L)
                .year(2023)
                .content("hello")
                .quarter(2)
                .build();
        Long savedId = financialStatementService.save(FSDTO);

        // when

        List<FinancialStatementDTO> financialStatementDTOs = financialStatementService.find3yearsFinancialStatementByStockName("samsung");

        // then
        assertThat(financialStatementDTOs.get(0).getContent()).isEqualTo("hello");
    }
}
package Project.quantHelper.domain;

import Project.quantHelper.repository.StockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class StockTest {

    @Autowired
    StockRepository stockRepository;

    @AfterEach
    public void clean(){
        stockRepository.deleteAll();
    }

    @Test
    public void testSaveStock() {
        // given
        Stock stock = Stock.builder()
                .stockId(1L)
                .stockName("samsung")
                .price(1000L)
                .theme("Test Theme")
                .stockPriceIndex("KOSPI")
                .status("Active")
                .build();


        // when
        Stock savedStock = stockRepository.save(stock);

        // then
        assertThat(savedStock.getStockId()).isNotNull();
        assertThat(savedStock.getStockName()).isEqualTo("samsung");
        assertThat(savedStock.getPrice()).isEqualTo(1000);
        assertThat(savedStock.getTheme()).isEqualTo("Test Theme");
        assertThat(savedStock.getStockPriceIndex()).isEqualTo("KOSPI");
        assertThat(savedStock.getStatus()).isEqualTo("Active");
    }

}
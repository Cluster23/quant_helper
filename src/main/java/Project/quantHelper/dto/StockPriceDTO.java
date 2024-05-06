package Project.quantHelper.dto;

import Project.quantHelper.domain.StockPrice;
import lombok.*;

import java.sql.Date;
import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor
public class StockPriceDTO {
    private Long stockId;
    private LocalDate date;
    private Long maxPriceDay;
    private Long minPriceDay;
    private Long openPrice;
    private Long closePrice;
    private Long tradingVolume;
    private Long movingAverageLine5;
    private Long movingAverageLine10;
    private Long movingAverageLine20;

    @Builder
    public StockPriceDTO(Long stockId, LocalDate date, Long maxPriceDay, Long minPriceDay, Long openPrice, Long closePrice, Long tradingVolume, Long movingAverageLine5, Long movingAverageLine10, Long movingAverageLine20) {
        this.stockId = stockId;
        this.date = date;
        this.maxPriceDay = maxPriceDay;
        this.minPriceDay = minPriceDay;
        this.openPrice = openPrice;
        this.closePrice = closePrice;
        this.tradingVolume = tradingVolume;
        this.movingAverageLine5 = movingAverageLine5;
        this.movingAverageLine10 = movingAverageLine10;
        this.movingAverageLine20 = movingAverageLine20;
    }
}

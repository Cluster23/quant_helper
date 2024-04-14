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

    @Builder
    public StockPriceDTO(Long stockId, LocalDate date, Long maxPriceDay, Long minPriceDay, Long openPrice, Long closePrice, Long tradingVolume) {
        this.stockId = stockId;
        this.date = date;
        this.maxPriceDay = maxPriceDay;
        this.minPriceDay = minPriceDay;
        this.openPrice = openPrice;
        this.closePrice = closePrice;
        this.tradingVolume = tradingVolume;
    }
}

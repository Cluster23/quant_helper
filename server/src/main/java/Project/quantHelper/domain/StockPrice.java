package Project.quantHelper.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Entity
@Getter
public class StockPrice {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_price_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stockId", referencedColumnName = "stockId")
    private Stock stock;
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
    public StockPrice(LocalDate date, Long maxPriceDay, Long minPriceDay, Long openPrice, Long closePrice, Long tradingVolume, Long movingAverageLine5, Long movingAverageLine10, Long movingAverageLine20) {
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



    public StockPrice() {
    }

    public void changeStock(Stock stock){
        this.stock = stock;
        stock.getStockPriceList().add(this);
    }
}

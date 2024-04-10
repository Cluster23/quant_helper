package Project.quantHelper.dto;

import Project.quantHelper.domain.Stock;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter @Setter
@NoArgsConstructor
public class StockDTO {
    private Long stockId;
    private String stockName;
    private Long price;
    private String theme;
    private String stockPriceIndex;
    private String status;

    public Stock toEntity(){
        return Stock.builder()
                .stockId(stockId)
                .stockName(stockName)
                .price(price)
                .theme(theme)
                .stockPriceIndex(stockPriceIndex)
                .status(status)
                .build();
    }

    @Builder
    public StockDTO(Long stockId, String stockName, Long price, String theme, String stockPriceIndex, String status) {
        this.stockId = stockId;
        this.stockName = stockName;
        this.price = price;
        this.theme = theme;
        this.stockPriceIndex = stockPriceIndex;
        this.status = status;
    }

}


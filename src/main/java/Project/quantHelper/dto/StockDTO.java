package Project.quantHelper.dto;

import Project.quantHelper.domain.Stock;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter @Setter
@NoArgsConstructor
public class StockDTO {
    private String stockCode;
    private String stockName;
    private Long price;
    private String theme;
    private String stockPriceIndex;
    private String status;
    private String corpCode;

    public Stock toEntity(){
        return Stock.builder()
                .stockCode(stockCode)
                .stockName(stockName)
                .price(price)
                .theme(theme)
                .stockPriceIndex(stockPriceIndex)
                .status(status)
                .corpCode(corpCode)
                .build();
    }

    @Builder
    public StockDTO(String stockCode, String stockName, Long price, String theme, String stockPriceIndex, String status, String corpCode) {
        this.stockCode = stockCode;
        this.stockName = stockName;
        this.price = price;
        this.theme = theme;
        this.stockPriceIndex = stockPriceIndex;
        this.status = status;
        this.corpCode = corpCode;
    }

}


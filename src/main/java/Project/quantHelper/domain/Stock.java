package Project.quantHelper.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 주식 테이블 엔티티
 */
@Entity
@Getter
public class Stock {

    @Id
    private Long stockId; // PK
    private String stockName; // 주식 이름
    private Long price; // 현재가
    private String theme; // 업종 구분
    private String stockPriceIndex; // KOSPI인지 KOSDAQ인지
    private String status;

    @OneToMany
    private List<CorporateInformation> corporateInformationList;

    @Builder
    public Stock(Long stockId, String stockName, Long price, String theme, String stockPriceIndex, String status, List<CorporateInformation> corporateInformationList) {
        this.stockId = stockId;
        this.stockName = stockName;
        this.price = price;
        this.theme = theme;
        this.stockPriceIndex = stockPriceIndex;
        this.status = status;
        this.corporateInformationList = corporateInformationList;
    }

    public Stock() {
    }
}

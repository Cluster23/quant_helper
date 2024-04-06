package Project.quantHelper.dto;

import Project.quantHelper.domain.CorporateInformation;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CorporateInformationDTO {
    private Long id; // PK
    private Long stockId; // FK
    private String corporationName; // 회사 이름
    private Date foundationDate;
    private Long marketCapitalization; // 시가 총액
    private Long maxPriceYear; // 52주 최고가
    private Long minPriceYear; // 52주 최저가

    public CorporateInformation toEntity(CorporateInformation corporateInformation) {
        return CorporateInformation.builder()
                .id(stockId)
                .corporationName(corporationName)
                .foundationDate(foundationDate)
                .marketCapitalization(marketCapitalization)
                .maxPriceYear(maxPriceYear)
                .minPriceYear(minPriceYear)
                .build();
    }

    @Builder
    public CorporateInformationDTO(Long id, Long stockId, String corporationName, Date foundationDate, Long marketCapitalization, Long maxPriceYear, Long minPriceYear) {
        this.id = id;
        this.stockId = stockId;
        this.corporationName = corporationName;
        this.foundationDate = foundationDate;
        this.marketCapitalization = marketCapitalization;
        this.maxPriceYear = maxPriceYear;
        this.minPriceYear = minPriceYear;
    }
}
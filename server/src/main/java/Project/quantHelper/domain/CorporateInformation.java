package Project.quantHelper.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Entity
@Getter
public class CorporateInformation {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "corporate_information_id")
        private Long id; // PK

        @OneToOne
        @JoinColumn(name = "stockId", referencedColumnName = "stockId")
        private Stock stock; // FK

        private String corporationName; // 회사 이름
        private Date foundationDate;
        private Long marketCapitalization; // 시가 총액
        private Long maxPriceYear; // 52주 최고가
        private Long minPriceYear; // 52주 최저가

        @Builder
        public CorporateInformation(Long id, String corporationName, Date foundationDate, Long marketCapitalization, Long maxPriceYear, Long minPriceYear) {
                this.id = id;
                this.corporationName = corporationName;
                this.foundationDate = foundationDate;
                this.marketCapitalization = marketCapitalization;
                this.maxPriceYear = maxPriceYear;
                this.minPriceYear = minPriceYear;
        }


        public CorporateInformation() {
        }
}

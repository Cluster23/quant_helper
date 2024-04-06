package Project.quantHelper.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter @Setter
public class CorporateInformation {
        @Id
        @GeneratedValue
        @Column(name = "corporate_information_id")
        private Long id; // PK

        @ManyToOne
        @JoinColumn(name = "stock_id", referencedColumnName = "stock_id")
        private Stock stock; // FK

        private String corporation_name; // 회사 이름
        private Date foundation_date;
        private Long market_capitalization; // 시가 총액
        private Long max_price_year; // 52주 최고가
        private Long min_price_year; // 52주 최저가

}

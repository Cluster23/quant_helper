package Project.quantHelper.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 주식 테이블 엔티티
 */
@Entity
@Getter
public class Stock {

    @Id @GeneratedValue
    private Long stockId; // PK
    private String stockCode; // 주식 코드
    private String stockName; // 주식 이름
    private Long price; // 현재가
    private String theme; // 업종 구분
    private String stockPriceIndex; // KOSPI인지 KOSDAQ인지
    private String status;
    private String corpCode;

    // 하나의 주식은 여러개의 재무제표(분기별, 연도별)에서 사용될 수 있다. 따라서 One(주식정보)toMany(재무제표s)를 사용한다.
    @OneToMany(mappedBy = "stock")
    private List<FinancialStatement> financialStatementlist = new ArrayList<>();

    // 하나의 주식은 단 하나의 회사와 매핑된다. 따라서 OneToOne을 사용한다.
    @OneToOne(mappedBy = "stock")
    private CorporateInformation  corporateInformation;

    @Builder
    public Stock(String stockCode, String stockName, Long price, String theme, String stockPriceIndex, String status, String corpCode, List<FinancialStatement> financialStatementlist, CorporateInformation corporateInformation) {
        this.stockCode = stockCode;
        this.stockName = stockName;
        this.price = price;
        this.theme = theme;
        this.stockPriceIndex = stockPriceIndex;
        this.status = status;
        this.corpCode = corpCode;
        this.financialStatementlist = financialStatementlist;
        this.corporateInformation = corporateInformation;
    }


    public Stock() {
    }
}
